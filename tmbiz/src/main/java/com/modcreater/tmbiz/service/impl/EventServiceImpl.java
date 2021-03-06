package com.modcreater.tmbiz.service.impl;

import com.alibaba.fastjson.JSON;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.dto.EventPersons;
import com.modcreater.tmbeans.pojo.*;
import com.modcreater.tmbeans.show.ShowSingleEvent;
import com.modcreater.tmbeans.utils.NaturalWeek;
import com.modcreater.tmbeans.vo.QueryMsgStatusVo;
import com.modcreater.tmbeans.vo.eventvo.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedDeleteEventIds;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmbiz.service.EventService;
import com.modcreater.tmbiz.config.EventUtil;
import com.modcreater.tmdao.mapper.*;
import com.modcreater.tmutils.*;
import com.modcreater.tmutils.messageutil.CreateInviteMessage;
import com.modcreater.tmutils.messageutil.InviteMessage;
import com.modcreater.tmutils.messageutil.UpdateInviteMessage;
import io.rong.messages.TxtMessage;
import io.rong.models.response.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import com.alibaba.fastjson.JSONObject;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-04-29
 * Time: 11:32
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class EventServiceImpl implements EventService {

    private static final String SYSTEMID = "100000";
    private static final String YES = "1";
    private static final String NO = "0";

    @Resource
    private EventMapper eventMapper;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private AppTypeMapper appTypeMapper;

    @Resource
    private StatisticsMapper statisticsMapper;

    @Resource
    private TempEventMapper tempEventMapper;

    @Resource
    private UserSettingsMapper userSettingsMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private EventViceMapper eventViceMapper;

    @Resource
    private UserServiceMapper userServiceMapper;

    @Resource
    private MsgStatusMapper msgStatusMapper;

    @Resource
    private DeviceTokenMapper deviceTokenMapper;

    @Resource
    private GroupMapper groupMapper;

    @Resource
    private SynchronHistoryMapper synchronHistoryMapper;

    @Resource
    private BacklogMapper backlogMapper;

    private Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);
    @Resource
    private EventUtil eventUtil;

    @Override
    public synchronized Dto addNewEvents(UploadingEventVo uploadingEventVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(uploadingEventVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!StringUtils.hasText(uploadingEventVo.getSingleEvent())) {
            return DtoUtil.getFalseDto("没有可上传的事件", 21002);
        }
        SingleEventAndBacklog singleEvent = JSONObject.parseObject(uploadingEventVo.getSingleEvent(), SingleEventAndBacklog.class);
        singleEvent.setUserid(Long.valueOf(uploadingEventVo.getUserId()));
        //这里开始判断是否是一个重复事件,如果状态值为真,则该事件为重复事件
        singleEvent.setIsLoop(SingleEventUtil.isLoopEvent(singleEvent.getRepeaTtime()) ? 1 : 0);
        if (singleEvent.getIsLoop() == 1) {
            List<SingleEvent> loopEventList = eventMapper.queryClashLoopEventList(singleEvent);
            if (!SingleEventUtil.loopEventTime(loopEventList, singleEvent)) {
                return DtoUtil.getFalseDto("时间段冲突,无法添加", 21012);
            }
        } else if (!SingleEventUtil.eventTime(eventMapper.queryClashEventList(singleEvent), Long.valueOf(singleEvent.getStarttime()), Long.valueOf(singleEvent.getEndtime()))) {
            return DtoUtil.getFalseDto("时间段冲突,无法添加", 21012);
        }
        //记录操作时间
        if (!ObjectUtils.isEmpty(singleEvent) && eventMapper.uploadingEvents(singleEvent) > 0) {
            //加入事件清单
            if (!ObjectUtils.isEmpty(singleEvent.getBacklogList()) && singleEvent.getBacklogList().size()>0){
                List<BacklogList> backlogLists=new ArrayList<>();
                for (BacklogList backlogList:singleEvent.getBacklogList()) {
                    backlogList.setSingleEventId(singleEvent.getId());
                    backlogLists.add(backlogList);
                }
                backlogMapper.insertBacklog(backlogLists);
            }
            return DtoUtil.getSuccessDto("事件上传成功", 100000);
        }
        return DtoUtil.getFalseDto("事件上传失败", 21001);
    }

    @Override
    public synchronized Dto deleteEvents(DeleteEventVo deleteEventVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(deleteEventVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!ObjectUtils.isEmpty(eventMapper.getChangingEventStatus(deleteEventVo))) {
            return DtoUtil.getFalseDto("重复操作:已经操作过了", 21003);
        }
        /*SingleEvent singleEvent = eventMapper.getAEvent(deleteEventVo.getUserId(), Long.valueOf(deleteEventVo.getEventId()), "singleevent");
        if (!ObjectUtils.isEmpty(singleEvent)) {
            if (singleEvent.getIsLoop() == 1 && "1".equals(deleteEventVo.getEventStatus())) {
                return DtoUtil.getSuccessDto("修改事件状态成功", 100000);
            }
        }*/
        if (eventMapper.withdrawEventsByUserId(deleteEventVo) > 0) {
            return DtoUtil.getSuccessDto("修改事件状态成功", 100000);
        }
        return DtoUtil.getFalseDto("修改事件状态失败", 21005);
    }

    /**
     * 修改事件
     *
     * @param updateEventVo
     * @param token
     * @return
     */
    @Override
    public synchronized Dto updateEvents(UploadingEventVo updateEventVo, String token) {
        if (!StringUtils.hasText(updateEventVo.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("操作失败,token未获取到", 21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(updateEventVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (ObjectUtils.isEmpty(updateEventVo)) {
            return DtoUtil.getFalseDto("修改条件接收失败", 21008);
        }
        SingleEventAndBacklog singleEvent = JSONObject.parseObject(updateEventVo.getSingleEvent(), SingleEventAndBacklog.class);
        singleEvent.setUserid(Long.valueOf(updateEventVo.getUserId()));
        //这里开始判断是否是一个重复事件,如果状态值为真,则该事件为重复事件
        singleEvent.setIsLoop(SingleEventUtil.isLoopEvent(singleEvent.getRepeaTtime()) ? 1 : 0);
        SingleEvent result = eventMapper.querySingleEventTime(singleEvent);
        if (result.getIsOverdue() != 0) {
            return DtoUtil.getFalseDto("事件已过期", 21023);
        }
        if (!(singleEvent.getStarttime().equals(result.getStarttime()) && singleEvent.getEndtime().equals(result.getEndtime()))) {
            if (singleEvent.getIsLoop() == 1) {
                List<SingleEvent> loopEventList = eventMapper.queryClashLoopEventList(singleEvent);
                if (!SingleEventUtil.loopEventTime(loopEventList, singleEvent)) {
                    return DtoUtil.getFalseDto("时间段冲突,无法修改", 21012);
                }
            } else if (!SingleEventUtil.eventTime(eventMapper.queryClashEventList(singleEvent), Long.valueOf(singleEvent.getStarttime()), Long.valueOf(singleEvent.getEndtime()))) {
                return DtoUtil.getFalseDto("时间段冲突,无法修改", 21012);
            }
        }
        List<String> personList1 = new ArrayList();
        List<String> personList2 = new ArrayList();
        try {
            EventPersons eventPersons = JSONObject.parseObject(singleEvent.getPerson(), EventPersons.class);
            if (!StringUtils.isEmpty(eventPersons.getFriendsId())) {
                String[] persons = eventPersons.getFriendsId().split(",");
                for (int i = 0; i < persons.length; i++) {
                    Friendship friendship = accountMapper.queryFriendshipDetail(persons[i], updateEventVo.getUserId());
                    UserSettings settings = userSettingsMapper.queryAllSettings(persons[i]);
                    if (friendship.getInvite() == 1 && settings.getFriendInvite() == 0) {
                        //只添加满足条件的人
                        personList1.add(persons[i]);
                    } else {
                        //查询屏蔽人的id
                        personList2.add(persons[i]);
                    }
                }
                //给list1发送邀请信息
                for (int i = 0; i < personList1.size(); i++) {
                    MsgStatus msgStatus = new MsgStatus();
                    msgStatus.setType(1L);
                    msgStatus.setUserId(Long.parseLong(updateEventVo.getUserId()));
                    msgStatus.setReceiverId(Long.parseLong(personList1.get(i)));
                    if (msgStatusMapper.addNewMsg(msgStatus) == 0) {
                        logger.info("修改事件时消息状态保存失败,id====>" + msgStatus.getId());
                    }
                    RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                    String date = singleEvent.getYear() + "/" + singleEvent.getMonth() + "/" + singleEvent.getDay();
                    InviteMessage inviteMessage = new InviteMessage(singleEvent.getEventname(), date, JSON.toJSONString(SingleEventUtil.getShowSingleEvent(singleEvent)), "2", msgStatus.getId().toString());
                    logger.info(JSON.toJSONString(SingleEventUtil.getShowSingleEvent(singleEvent)));
                    ResponseResult result1 = rongCloudMethodUtil.sendPrivateMsg(updateEventVo.getUserId(), new String[]{personList1.get(i)}, 0, inviteMessage);

                    if (result1.getCode() != 200) {
                        logger.info("修改事件时融云消息异常" + result1.toString());
                        return DtoUtil.getFalseDto("消息发送失败", 21040);
                    }
                    //如果是ios发送推送信息
                    UserDeviceToken userDeviceToken = deviceTokenMapper.queryDeviceToken(personList1.get(i));
                    if (!ObjectUtils.isEmpty(userDeviceToken) && userDeviceToken.getAppType() == 1L && !StringUtils.isEmpty(userDeviceToken.getDeviceToken())) {
//                        IOSPushUtil.push(userDeviceToken.getDeviceToken(), "你的好友邀请你参与他的事件", 1);
                    }
                    msgStatusMapper.addNewEventMsg(personList1.get(i), singleEvent.getEventid(), updateEventVo.getUserId(), "邀请你参与事件", System.currentTimeMillis() / 1000);
                }
                //list2给创建者发送拒绝信息
                for (int i = 0; i < personList2.size(); i++) {
                    RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                    TxtMessage txtMessage = new TxtMessage("我拒绝了你的事件：“" + singleEvent.getEventname() + "”的邀请", "");
                    ResponseResult result2 = rongCloudMethodUtil.sendPrivateMsg(personList2.get(i), new String[]{updateEventVo.getUserId()}, 0, txtMessage);
                    if (result2.getCode() != 200) {
                        logger.info("新增邀请事件回应邀请时融云消息异常：" + result2.toString());
                        return DtoUtil.getFalseDto("消息发送失败", 21040);
                    }
                    msgStatusMapper.addNewEventMsg(updateEventVo.getUserId(), singleEvent.getEventid(), personList2.get(i), "拒绝了你的事件", System.currentTimeMillis() / 1000);
                }
                //在事件副表插入创建者
                SingleEventVice singleEventVice = new SingleEventVice();
                singleEventVice.setCreateBy(Long.parseLong(updateEventVo.getUserId()));
                singleEventVice.setUserId(singleEvent.getUserid());
                singleEventVice.setEventId(singleEvent.getEventid());
                eventViceMapper.createEventVice(singleEventVice);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return DtoUtil.getFalseDto("修改事件失败", 21007);
        }
        //这里开始判断是否是一个重复事件,如果状态值为真,则该事件为重复事件
        singleEvent.setIsLoop(SingleEventUtil.isLoopEvent(singleEvent.getRepeaTtime()) ? 1 : 0);
        EventPersons eventPersons = JSONObject.parseObject(singleEvent.getPerson(), EventPersons.class);
        eventPersons.setFriendsId("");
        singleEvent.setPerson(JSON.toJSONString(eventPersons));
        if (eventMapper.alterEventsByUserId(singleEvent) > 0) {
            //判断同步权限
            if (!StringUtils.isEmpty(singleEvent.getIsSync()) && "1".equals(singleEvent.getIsSync())){
                BacklogList backlogList=new BacklogList();
                backlogList.setIsSync(1L);
                backlogList.setSingleEventId(singleEvent.getId());
                backlogMapper.updateBacklogIsSyncByEventId(backlogList);
            }
            return DtoUtil.getSuccessDto("修改成功", 100000);
        }
        return DtoUtil.getFalseDto("修改事件失败", 21007);
    }

    @Override
    public synchronized Dto firstUplEvent(SynchronousUpdateVo synchronousUpdateVo, String token) {
        if (ObjectUtils.isEmpty(synchronousUpdateVo)) {
            return DtoUtil.getFalseDto("同步数据未获取到", 26001);
        }
        System.out.println("第一次上传" + synchronousUpdateVo.toString());
        if (StringUtils.isEmpty(synchronousUpdateVo.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("操作失败,token未获取到", 21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(synchronousUpdateVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        //判断上传有没有数据
        //判断是否第一次上传
        if (eventMapper.queryEventByUserId(synchronousUpdateVo.getUserId()) > 0) {
            return DtoUtil.getFalseDto("该用户已经上传过了", 26003);
        }
        boolean flag = false;
        if (!StringUtils.isEmpty(synchronousUpdateVo.getDayEventList())) {
            //转换集合
            List<ArrayList> dayEvents = JSONObject.parseObject(synchronousUpdateVo.getDayEventList(), ArrayList.class);
            //上传普通事件
            for (Object dayEventsList : dayEvents) {
                //转换成DayEvents
                DayEvents dayEvents1 = JSONObject.parseObject(dayEventsList.toString(), DayEvents.class);
                //把getMySingleEventList()转换成集合
                ArrayList<SingleEvent> singleEventList = JSONObject.parseObject(dayEvents1.getMySingleEventList().toString(), ArrayList.class);
                for (Object singleEvent : singleEventList) {
                    //把遍历出的元素转换成对象
                    SingleEventAndBacklog singleEvent1 = JSONObject.parseObject(singleEvent.toString(), SingleEventAndBacklog.class);
                    //插入用户id
                    singleEvent1.setUserid(Long.parseLong(synchronousUpdateVo.getUserId()));
                    singleEvent1.setIsLoop(0);
                    //上传
                    if (eventMapper.uploadingEvents(singleEvent1) <= 0) {
                        return DtoUtil.getFalseDto("上传事件" + singleEvent1.getEventid() + "失败", 25005);
                    }
                    //上传事件清单
                    if (!ObjectUtils.isEmpty(singleEvent1.getBacklogList()) && singleEvent1.getBacklogList().size()>0){
                        List<BacklogList> backlogLists=new ArrayList<>();
                        for (BacklogList backlogList:singleEvent1.getBacklogList()) {
                            backlogList.setSingleEventId(singleEvent1.getId());
                            backlogLists.add(backlogList);
                        }
                        backlogMapper.insertBacklog(backlogLists);
                    }
                }
            }
            flag = true;
        }
        if (!StringUtils.isEmpty(synchronousUpdateVo.getLoopEventList())) {
            //外层集合转换
            List<ArrayList> loopEvents = JSONObject.parseObject(synchronousUpdateVo.getLoopEventList(), ArrayList.class);
            //上传重复事件
            for (List<SingleEvent> singleEvents : loopEvents) {
                if (!ObjectUtils.isEmpty(singleEvents)) {
                    //第二层转换
                    List<SingleEvent> singleEventList = JSONObject.parseObject(singleEvents.toString(), ArrayList.class);
                    for (Object loopEvent : singleEventList) {
                        //第三层转换
                        SingleEventAndBacklog singleEvent = JSONObject.parseObject(loopEvent.toString(), SingleEventAndBacklog.class);
                        singleEvent.setUserid(Long.parseLong(synchronousUpdateVo.getUserId()));
                        singleEvent.setIsLoop(1);
                        if (eventMapper.uploadingEvents(singleEvent) <= 0) {
                            return DtoUtil.getFalseDto("上传重复事件" + singleEvent.getEventid() + "失败", 25006);
                        }
                        if (!ObjectUtils.isEmpty(singleEvent.getBacklogList()) && singleEvent.getBacklogList().size()>0){
                            List<BacklogList> backlogLists=new ArrayList<>();
                            for (BacklogList backlogList:singleEvent.getBacklogList()) {
                                backlogList.setSingleEventId(singleEvent.getId());
                                backlogLists.add(backlogList);
                            }
                            backlogMapper.insertBacklog(backlogLists);
                        }
                    }
                }
            }
            flag = true;
        }
        if (flag) {
            return DtoUtil.getSuccessDto("数据同步成功", 100000);
        } else {
            return DtoUtil.getSuccessDto("数据同步失败", 25008);
        }
    }

    /**
     * 上传草稿
     *
     * @param draftVo
     * @param token
     * @return
     */
    @Override
    public synchronized Dto uplDraft(DraftVo draftVo, String token) {
        if (ObjectUtils.isEmpty(draftVo)) {
            return DtoUtil.getFalseDto("上传草稿未获取到", 27001);
        }
        if (StringUtils.isEmpty(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        if (StringUtils.isEmpty(draftVo.getUserId())) {
            return DtoUtil.getFalseDto("userId不能为空", 21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(draftVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        //判断是否已开通或者服务时间未消耗完
        ServiceRemainingTime time = userServiceMapper.getServiceRemainingTime(draftVo.getUserId(), "4");
        //用户未开通
        if (ObjectUtils.isEmpty(time)) {
            return DtoUtil.getSuccessDto("该用户尚未开通备份功能", 20000);
        }
        boolean flag = false;
        //判断是否最后一次
        if (time.getResidueDegree() == -1) {
            //删除服务
            userServiceMapper.deleteService(time.getId());
            flag = true;
        } else {
            //开通了,查询次卡是否有剩余
            if (time.getResidueDegree() == 0) {
                //无剩余,判断剩余年/月卡时间
                Long timeRemaining = time.getTimeRemaining();
                if (timeRemaining == 0 || timeRemaining < System.currentTimeMillis() / 1000) {
                    return DtoUtil.getSuccessDto("该用户尚未开通备份功能", 20000);
                }
            } else {
                //有剩余,判断此次查询完毕后是否剩余为0次
                time.setResidueDegree(time.getResidueDegree() - 1);
                //判断剩余次数-1后是否为0,如果为0...
                if (time.getResidueDegree() == 0 && time.getStorageTime() != 0) {
                    //如果有库存时间,将这个时间加入用户有效的剩余时间中
                    time.setTimeRemaining(System.currentTimeMillis() / 1000 + time.getStorageTime());
                    time.setStorageTime(0L);
                }
            }
        }
        ArrayList<Object> drafts = JSONObject.parseObject(draftVo.getSingleEvents(), ArrayList.class);
        for (Object draft : drafts) {
            System.out.println(draft);
            SingleEventAndBacklog draft1 = JSONObject.parseObject(draft.toString(), SingleEventAndBacklog.class);
            try {
                draft1.setPerson(JSON.toJSONString(JSONObject.parseObject(draft1.getPerson(), EventPersons.class)));
            } catch (Exception e) {
                draft1.setPerson("{\"friendsId\":\"\",\"others\":\"\"}");
            }
            //查看草稿是否已存在
            if (eventMapper.queryDraftCount(draftVo.getUserId(), draft1.getEventid().toString()) == 0) {
                //上传
                draft1.setUserid(Long.parseLong(draftVo.getUserId()));
                draft1.setIsLoop(SingleEventUtil.isLoopEvent(draft1.getRepeaTtime()) ? 1 : 0);
                draft1.setYear(draft1.getYear());
                draft1.setMonth(draft1.getMonth());
                draft1.setDay(draft1.getDay());
                if (eventMapper.uplDraft(draft1) == 0) {
                    return DtoUtil.getFalseDto("上传草稿失败", 27002);
                }
                //草稿添加事件清单
                if (!ObjectUtils.isEmpty(draft1.getBacklogList()) && draft1.getBacklogList().size()>0){
                    List<BacklogList> backlogLists=new ArrayList<>();
                    for (BacklogList backlogList:draft1.getBacklogList()) {
                        backlogList.setSingleEventId(draft1.getId());
                        backlogLists.add(backlogList);
                    }
                    backlogMapper.insertDraftBacklog(backlogLists);
                }
            }
        }
        if (!flag) {
            //修改用户服务剩余时间
            if (userServiceMapper.updateServiceRemainingTime(time) == 0) {
                //回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return DtoUtil.getFalseDto("修改用户服务剩余时间失败", 27004);
            }
        }
        return DtoUtil.getSuccessDto("上传草稿成功", 100000);
    }

    /**
     * 修改一个草稿
     *
     * @param addInviteEventVo
     * @param token
     * @return
     */
    @Override
    public synchronized Dto updDraft(AddInviteEventVo addInviteEventVo, String token) {
        if (ObjectUtils.isEmpty(addInviteEventVo)) {
            return DtoUtil.getFalseDto("修改草稿未获取到", 27001);
        }
        if (StringUtils.isEmpty(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        if (StringUtils.isEmpty(addInviteEventVo.getUserId())) {
            return DtoUtil.getFalseDto("userId不能为空", 21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(addInviteEventVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        SingleEvent singleEvent = JSONObject.parseObject(addInviteEventVo.getSingleEvent(), SingleEvent.class);
//        System.out.println("修改草稿:" + singleEvent.toString());
        if (ObjectUtils.isEmpty(singleEvent)) {
            return DtoUtil.getFalseDto("获取草稿失败", 21111);
        }
        if (eventMapper.updateDraft(singleEvent) == 0) {
            return DtoUtil.getFalseDto("修改草稿失败", 21112);
        }
        return DtoUtil.getSuccessDto("修改草稿成功", 100000);
    }


    @Override
    public Dto searchByDayEventIds(SearchEventVo searchEventVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(searchEventVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!StringUtils.hasText(searchEventVo.getDayEventId())) {
            return DtoUtil.getFalseDto("查询条件接收失败", 21004);
        }
        //拆分dayEventId并将查询条件逐一添加到对象中
        SingleEvent singleEvent = SingleEventUtil.getSingleEvent(searchEventVo.getUserId(), searchEventVo.getDayEventId());
        singleEvent.setIsOverdue(StringUtils.hasText(searchEventVo.getFriendId()) && searchEventVo.getFriendId().equals("seaPlans") ? null : 0L);
        System.out.println(singleEvent.toString());
        //只根据level升序
        List<SingleEvent> singleEventListOrderByLevel = completedLoopEvent(eventMapper.queryByDayOrderByLevel(singleEvent));
        List<ShowSingleEvent> showSingleEventListOrderByLevel = new ArrayList<>();
        //根据level和开始时间升序
        List<SingleEvent> singleEventListOrderByLevelAndDate = completedLoopEvent(eventMapper.queryByDayOrderByLevelAndDate(singleEvent));
        List<ShowSingleEvent> showSingleEventListOrderByLevelAndDate = new ArrayList<>();
        //添加一个未排序的结果集到dayEvents中
        DayEvents<ShowSingleEvent> dayEvents = new DayEvents<>();
        ArrayList<SingleEvent> singleEventList = (ArrayList<SingleEvent>) completedLoopEvent(eventMapper.queryEvents(singleEvent));
        ArrayList<ShowSingleEvent> showSingleEventList = new ArrayList<>();
        if (singleEventListOrderByLevel.size() != 0 && singleEventListOrderByLevelAndDate.size() != 0 && singleEventList.size() != 0) {
            showSingleEventListOrderByLevel = SingleEventUtil.getShowSingleEventList(singleEventListOrderByLevel);
            showSingleEventListOrderByLevelAndDate = SingleEventUtil.getShowSingleEventList(singleEventListOrderByLevelAndDate);
            showSingleEventList = (ArrayList<ShowSingleEvent>) SingleEventUtil.getShowSingleEventList(singleEventList);
        }
        dayEvents.setUserId(singleEvent.getUserid());
        dayEvents.setTotalNum((long) singleEventList.size());
        dayEvents.setDayEventId(Long.valueOf(searchEventVo.getDayEventId()));
        dayEvents.setMySingleEventList(showSingleEventList);
        //查询重复事件
        int week = DateUtil.stringToWeek(searchEventVo.getDayEventId());
        week = week == 7 ? 0 : week;
        //根据用户ID查询重复事件
        List<SingleEvent> loopEventListInDataBase = eventMapper.queryLoopEvents(searchEventVo.getUserId());
        //判断上一条查询结果是否有数据
        if (loopEventListInDataBase.size() != 0) {
            //遍历集合并将符合repeatTime = 星期 的对象分别添加到集合中
            for (SingleEvent singleEvent1 : loopEventListInDataBase) {
                if (!SingleEventUtil.eventTime(singleEventListOrderByLevel, Long.valueOf(singleEvent1.getStarttime()), Long.valueOf(singleEvent1.getEndtime()))) {
                    continue;
                }
                ShowSingleEvent showSingleEvent = SingleEventUtil.getShowSingleEvent1(singleEvent1);
                if (showSingleEvent.getRepeaTtime()[week]) {
                    showSingleEventListOrderByLevel.add(showSingleEvent);
                    showSingleEventListOrderByLevelAndDate.add(showSingleEvent);
                    if (StringUtils.hasText(searchEventVo.getFriendId()) && searchEventVo.getFriendId().equals("seaPlans")) {
                        if (Long.valueOf(showSingleEvent.getEndtime()) < DateUtil.getCurrentMinutes()) {
                            showSingleEvent.setIsOverdue(1L);
                        }
                        dayEvents.getMySingleEventList().add(showSingleEvent);
                    }
                }
            }
        }
        //将得到的数据封装到map作为返回
        Map<String, Object> result = new HashMap<>(3);
        result.put("ShowSingleEventListOrderByLevel", showSingleEventListOrderByLevel);
        result.put("ShowSingleEventListOrderByLevelAndDate", showSingleEventListOrderByLevelAndDate);
        result.put("dayEvents", dayEvents);
        if (!ObjectUtils.isEmpty(dayEvents)) {
            return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
        }
        return DtoUtil.getSuccessDto("没有数据", 200000);
    }

    @Override
    public Dto searchByDayEventIdsInMonth(SearchEventVo searchEventVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(searchEventVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!StringUtils.hasText(searchEventVo.getDayEventId())) {
            return DtoUtil.getFalseDto("查询条件接收失败", 21004);
        }
        SingleEvent singleEvent = SingleEventUtil.getSingleEvent(searchEventVo.getUserId(), searchEventVo.getDayEventId());
        //查询在该月内存在事件的日的集合
        List<Integer> days = eventMapper.queryDays(singleEvent);
        List<DayEvents<ShowSingleEvent>> dayEventsList = new ArrayList<>();
        if (days.size() == 0) {
            return DtoUtil.getSuccessDto("没有数据", 200000);
        }
        for (Integer day : days) {
            singleEvent.setDay(day.longValue());
            ArrayList<SingleEvent> singleEventList = eventMapper.queryEvents(singleEvent);
            if (singleEventList.size() == 0) {
                continue;
            }
            ArrayList<ShowSingleEvent> showSingleEventList = (ArrayList<ShowSingleEvent>) SingleEventUtil.getShowSingleEventList(completedLoopEvent(singleEventList));
            Iterator<ShowSingleEvent> iterator = showSingleEventList.iterator();
            while (iterator.hasNext()) {
                ShowSingleEvent showSingleEvent = iterator.next();
                if (showSingleEvent.getFlag() == 5) {
                    iterator.remove();
                }
            }
            DayEvents<ShowSingleEvent> dayEvents = new DayEvents<>();
            dayEvents.setUserId(singleEvent.getUserid());
            dayEvents.setTotalNum((long) singleEventList.size());
            String month = singleEvent.getMonth().toString();
            String day1 = singleEvent.getDay().toString();
            if (month.length() == 1) {
                month = "0" + month;
            }
            if (day1.length() == 1) {
                day1 = "0" + day1;
            }
            dayEvents.setDayEventId(Long.valueOf(singleEvent.getYear().toString() + month + day1));
            dayEvents.setMySingleEventList(showSingleEventList);
            dayEventsList.add(dayEvents);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功", dayEventsList, 100000);
    }

    @Override
    public Dto searchByDayEventIdsInWeek(SearchEventVo searchEventVo, String token) {
        if (!StringUtils.hasText(searchEventVo.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("操作失败,token未获取到", 21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(searchEventVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (ObjectUtils.isEmpty(searchEventVo)) {
            return DtoUtil.getFalseDto("查询条件接收失败", 21004);
        }
        System.out.println("按周查" + searchEventVo.toString());
        SingleEvent singleEvent;
        //按周查询单一事件
        List<DayEvents> dayEventsList = getDayEventsList(searchEventVo.getUserId(), "all", searchEventVo.getDayEventId());
        //按周查询重复事件
        List<SingleEvent> loopEventListInDataBase = eventMapper.queryLoopEvents(searchEventVo.getUserId());
        List<List<ShowSingleEvent>> loopEventList = getShowSingleEventListList(loopEventListInDataBase);
        if ((dayEventsList.size() + loopEventList.size()) == 0) {
            return DtoUtil.getSuccessDto("没有数据", 200000);
        }
        removeFlag5ClashSingleEvent(dayEventsList, loopEventList, searchEventVo.getDayEventId());
        Map<String, Object> result = new HashMap<>(2);
        result.put("dayEventsList", dayEventsList);
        result.put("loopEventList", loopEventList);
        return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
    }

    @Override
    public Dto seaByWeekWithPrivatePermission(SearchEventVo searchEventVo, String token) {
        if (!StringUtils.hasText(searchEventVo.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("操作失败,token未获取到", 21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(searchEventVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (ObjectUtils.isEmpty(searchEventVo)) {
            return DtoUtil.getFalseDto("查询条件接收失败", 21004);
        }
        Map<String, Object> result = new HashMap<>(3);
        List<List<ShowSingleEvent>> loopEventList = new ArrayList<>();
        List<DayEvents> dayEventsList = new ArrayList<>();
        //总权限 + 单一权限(这里的逻辑为在mybatis中userId和friendId值相反)
        try {
            if (userSettingsMapper.getFriendHide(searchEventVo.getFriendId()) == 0 && userSettingsMapper.getIsHideFromFriend(searchEventVo.getUserId(), searchEventVo.getFriendId()) == 1) {
                result.put("userPrivatePermission", "1");
            } else if (userSettingsMapper.getFriendHide(searchEventVo.getFriendId()) == 0 && userSettingsMapper.getIsHideFromFriend(searchEventVo.getUserId(), searchEventVo.getFriendId()) == 2) {
                result.put("userPrivatePermission", "2");
            } else {
                result.put("userPrivatePermission", "0");
                result.put("loopEventList", getShowSingleEventListList(new ArrayList<>()));
                result.put("dayEventsList", getDayEventsList(searchEventVo.getFriendId(), "none", searchEventVo.getDayEventId()));
                return DtoUtil.getSuccesWithDataDto("该用户设置了查看权限", result, 100000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("你们可能还不是好友", 23335);
        }

        if ("1".equals(result.get("userPrivatePermission"))) {
            //按周查询单一事件
            dayEventsList = getDayEventsList(searchEventVo.getFriendId(), "all", searchEventVo.getDayEventId());
            //按周查询重复事件
            List<SingleEvent> loopEventListInDataBase = eventMapper.queryLoopEvents(searchEventVo.getFriendId());
            loopEventList = getShowSingleEventListList(loopEventListInDataBase);
        } else if ("2".equals(result.get("userPrivatePermission"))) {
            //按周查询单一事件
            dayEventsList = getDayEventsList(searchEventVo.getFriendId(), "few", searchEventVo.getDayEventId());
            //按周查询重复事件
            List<SingleEvent> loopEventListInDataBase = eventMapper.queryLoopEventsWithFewInfo(searchEventVo.getFriendId());
            loopEventList = getShowSingleEventListList(loopEventListInDataBase);
        }
        if ((dayEventsList.size() + loopEventList.size()) == 0) {
            return DtoUtil.getSuccessDto("没有数据", 200000);
        }
        logger.error(dayEventsList.toString());
        removeFlag5ClashSingleEvent(dayEventsList, loopEventList, searchEventVo.getDayEventId());

        for (int i = 0; i <= 6; i++) {
            ArrayList<ShowSingleEvent> showSingleEvents = dayEventsList.get(i).getMySingleEventList();
            int week = DateUtil.stringToWeek(dayEventsList.get(i).getDayEventId().toString());
            week = week == 7 ? 0 : week;
            for (ShowSingleEvent singleEvent : showSingleEvents) {
                Iterator<ShowSingleEvent> iterator = loopEventList.get(week).iterator();
                while (iterator.hasNext()) {
                    ShowSingleEvent loopEvent = iterator.next();
                    if (ObjectUtils.isEmpty(loopEvent.getFlag())|| ObjectUtils.isEmpty(singleEvent.getFlag())){
                        continue;
                    }
                    if (SingleEventUtil.getClashTime(singleEvent.getStarttime(), singleEvent.getEndtime(), loopEvent.getStarttime(), loopEvent.getEndtime())) {
                        iterator.remove();
                    }
                }
            }
        }
        List<List<ShowSingleEvent>> loopEventList1 = new ArrayList<>();
        for (DayEvents dayEvents : dayEventsList) {
            int week = DateUtil.stringToWeek(dayEvents.getDayEventId().toString());
            week = week == 7 ? 0 : week;
            loopEventList1.add(loopEventList.get(week));
        }
        result.put("loopEventList", loopEventList1);
        result.put("dayEventsList", dayEventsList);
        return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
    }

    /**
     * 显示一个事件详情
     *
     * @param receivedSearchOnce
     * @param token
     * @return
     */
    @Override
    public Dto searchOnce(ReceivedSearchOnce receivedSearchOnce, String token) {
        try {
            if (StringUtils.isEmpty(receivedSearchOnce.getUserId())) {
                return DtoUtil.getFalseDto("请先登录", 21011);
            }
            if (!StringUtils.hasText(token)) {
                return DtoUtil.getFalseDto("操作失败,token未获取到", 21013);
            }
            if (!token.equals(stringRedisTemplate.opsForValue().get(receivedSearchOnce.getUserId()))) {
                return DtoUtil.getFalseDto("请重新登录", 21014);
            }
            System.out.println(receivedSearchOnce.toString());
            SingleEvent singleEvent = eventMapper.queryEventOne(receivedSearchOnce.getUserId(), receivedSearchOnce.getEventId());
            if (ObjectUtils.isEmpty(singleEvent)) {
                return DtoUtil.getFalseDto("事件已过期或者不存在", 200000);
            }
            Map<String, Object> maps = new HashMap<>();
            ArrayList<Map> list = new ArrayList<>();
            if (!StringUtils.isEmpty(singleEvent.getPerson())) {
                EventPersons eventPersons = JSONObject.parseObject(singleEvent.getPerson(), EventPersons.class);
                if (!StringUtils.isEmpty(eventPersons.getFriendsId())) {
                    String[] friendsId = eventPersons.getFriendsId().split(",");
                    for (String friendId : friendsId) {
                        Map map = new HashMap();
                        Account account = accountMapper.queryAccount(friendId);
                        map.put("friendId", account.getId().toString());
                        map.put("userCode", account.getUserCode());
                        map.put("userName", account.getUserName());
                        map.put("headImgUrl", account.getHeadImgUrl());
                        map.put("gender", account.getGender().toString());
                        list.add(map);
                    }
                }
                maps.put("friendsId", list);
                maps.put("others", eventPersons.getOthers());
                singleEvent.setPerson(JSON.toJSONString(maps));
                System.out.println("显示一个事件详情时输出的" + singleEvent.getPerson());
                SingleEventAndBacklog singleEventAndBacklog=JSONObject.parseObject(JSON.toJSONString(singleEvent),SingleEventAndBacklog.class);
                //查询该事件的清单
                List<BacklogList> backlogLists=backlogMapper.queryBacklogList(singleEvent.getId());
                //查询清单权限
                if (backlogLists.size()>0){
                    singleEventAndBacklog.setIsSync(backlogLists.get(0).getIsSync().toString());
                }
                singleEventAndBacklog.setBacklogList(backlogLists);
                return DtoUtil.getSuccesWithDataDto("查询成功", SingleEventUtil.getShowSingleEvent(singleEventAndBacklog), 100000);
            } else {
                singleEvent.setPerson(JSON.toJSONString(new EventPersons()));
                System.out.println("显示一个事件详情时输出的" + singleEvent.getPerson());
                SingleEventAndBacklog singleEventAndBacklog=JSONObject.parseObject(JSON.toJSONString(singleEvent),SingleEventAndBacklog.class);
                //查询该事件的清单
                List<BacklogList> backlogLists=backlogMapper.queryBacklogList(singleEvent.getId());
                singleEventAndBacklog.setBacklogList(backlogLists);
                return DtoUtil.getSuccesWithDataDto("查询成功", SingleEventUtil.getShowSingleEvent(singleEventAndBacklog), 100000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("查询事件出错", 2333);
        }
    }

    /**
     * 添加一条邀请事件
     *
     * @param addInviteEventVo
     * @param token
     * @return
     */
    @Override
    public synchronized Dto addInviteEvent(AddInviteEventVo addInviteEventVo, String token) {
        try {
            if (!token.equals(stringRedisTemplate.opsForValue().get(addInviteEventVo.getUserId()))) {
                return DtoUtil.getFalseDto("请重新登录", 21014);
            }
            SingleEventAndBacklog singleEvent = JSONObject.parseObject(addInviteEventVo.getSingleEvent(), SingleEventAndBacklog.class);
            singleEvent.setUserid(Long.parseLong(addInviteEventVo.getUserId()));
            //这里开始判断是否是一个重复事件,如果状态值为真,则该事件为重复事件
            singleEvent.setIsLoop(SingleEventUtil.isLoopEvent(singleEvent.getRepeaTtime()) ? 1 : 0);
            if (singleEvent.getIsLoop() == 1) {
                List<SingleEvent> loopEventList = eventMapper.queryClashLoopEventList(singleEvent);
                if (!SingleEventUtil.loopEventTime(loopEventList, singleEvent)) {
                    return DtoUtil.getFalseDto("时间段冲突,无法添加", 21012);
                }
            } else if (!SingleEventUtil.eventTime(eventMapper.queryClashEventList(singleEvent), Long.valueOf(singleEvent.getStarttime()), Long.valueOf(singleEvent.getEndtime()))) {
                return DtoUtil.getFalseDto("时间段冲突,无法添加", 21012);
            }
            logger.info("参与人员：" + singleEvent.getPerson());
            EventPersons eventPersons = JSONObject.parseObject(singleEvent.getPerson(), EventPersons.class);
            //好友存在变量里
            String friends = eventPersons.getFriendsId();
            eventPersons.setFriendsId("");
            singleEvent.setPerson(JSON.toJSONString(eventPersons));
            //事件保存在自己的时间轴里
            eventMapper.uploadingEvents(singleEvent);
            //添加事件清单
            if (!ObjectUtils.isEmpty(singleEvent.getBacklogList()) && singleEvent.getBacklogList().size()>0){
                List<BacklogList> backlogLists=new ArrayList<>();
                for (BacklogList backlogList:singleEvent.getBacklogList()) {
                    backlogList.setSingleEventId(singleEvent.getId());
                    //判断同步权限
                    if (!StringUtils.isEmpty(singleEvent.getIsSync()) && "1".equals(singleEvent.getIsSync())){
                        backlogList.setIsSync(1L);
                    }
                    backlogLists.add(backlogList);
                }
                backlogMapper.insertBacklog(backlogLists);
            }
            //在事件副表插入创建者
            SingleEventVice singleEventVice = new SingleEventVice();
            singleEventVice.setCreateBy(Long.parseLong(addInviteEventVo.getUserId()));
            singleEventVice.setUserId(singleEvent.getUserid());
            singleEventVice.setEventId(singleEvent.getEventid());
            eventViceMapper.createEventVice(singleEventVice);
            //邀请的好友数据
            String[] persons = friends.split(",");
            //要发送信息的人员
            ArrayList<String> personList1 = new ArrayList<>();
            //屏蔽的人员
            ArrayList<String> personList2 = new ArrayList<>();
            //判断好友是否开启了邀请权限
            for (int i = 0; i < persons.length; i++) {
                Friendship friendship = accountMapper.queryFriendshipDetail(persons[i], addInviteEventVo.getUserId());
                UserSettings settings = userSettingsMapper.queryAllSettings(persons[i]);
                if (friendship.getInvite() == 1 && settings.getFriendInvite() == 0) {
                    //只添加满足条件的人
                    personList1.add(persons[i]);
                } else {
                    //查询屏蔽人的id
                    personList2.add(persons[i]);
                }
            }
            //给list1发送邀请信息
            for (int i = 0; i < personList1.size(); i++) {
                MsgStatus msgStatus = new MsgStatus();
                msgStatus.setType(1L);
                msgStatus.setUserId(Long.parseLong(addInviteEventVo.getUserId()));
                msgStatus.setReceiverId(Long.parseLong(personList1.get(i)));
                if (msgStatusMapper.addNewMsg(msgStatus) == 0) {
                    logger.info("添加邀请事件时消息状态保存失败,id====>" + msgStatus.getId());
                }
                RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                String date = singleEvent.getYear() + "/" + singleEvent.getMonth() + "/" + singleEvent.getDay();
                InviteMessage inviteMessage = new InviteMessage(singleEvent.getEventname(), date, JSON.toJSONString(SingleEventUtil.getShowSingleEvent(singleEvent)), "2", msgStatus.getId().toString());
                logger.info(JSON.toJSONString(SingleEventUtil.getShowSingleEvent(singleEvent)));
                ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(addInviteEventVo.getUserId(), new String[]{personList1.get(i)}, 0, inviteMessage);
                if (result.getCode() != 200) {
                    logger.info("添加邀请事件时融云消息异常" + result.toString());
                    return DtoUtil.getFalseDto("消息发送失败", 21040);
                }
                msgStatusMapper.addNewEventMsg(personList1.get(i), singleEvent.getEventid(), addInviteEventVo.getUserId(), "邀请你参与事件", System.currentTimeMillis() / 1000);
            }
            //list2给创建者发送拒绝信息
            for (int i = 0; i < personList2.size(); i++) {
                RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                TxtMessage txtMessage = new TxtMessage("我拒绝了你的事件：“" + singleEvent.getEventname() + "”的邀请", "");
                ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(personList2.get(i), new String[]{addInviteEventVo.getUserId()}, 0, txtMessage);
                if (result.getCode() != 200) {
                    logger.info("新增邀请事件回应邀请时融云消息异常：" + result.toString());
                    return DtoUtil.getFalseDto("消息发送失败", 21040);
                }
                msgStatusMapper.addNewEventMsg(addInviteEventVo.getUserId(), singleEvent.getEventid(), personList2.get(i), "拒绝了你的事件", System.currentTimeMillis() / 1000);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return DtoUtil.getSuccessDto("消息已发送", 100000);
    }

    /**
     * 新增邀请事件回应邀请
     *
     * @param feedbackInviteVo
     * @param token
     * @return
     */
    @Override
    public synchronized Dto feedbackInvite(FeedbackInviteVo feedbackInviteVo, String token) {
        try {
            if (!token.equals(stringRedisTemplate.opsForValue().get(feedbackInviteVo.getUserId()))) {
                return DtoUtil.getFalseDto("请重新登录", 21014);
            }
            //判断不能重复回应
            MsgStatus msgStatus=msgStatusMapper.queryMsg(feedbackInviteVo.getMsgId());
            String fromId=msgStatus.getUserId().toString();
            GroupInviteMsgStatus groupInviteMsgStatus=groupMapper.queryGroupInviteMsgStatus(msgStatus.getId(),feedbackInviteVo.getUserId());
            if (!ObjectUtils.isEmpty(groupInviteMsgStatus) && groupInviteMsgStatus.getStatus() != 2){
                return DtoUtil.getFalseDto("不能重复回应", 21033);
            }else if (!ObjectUtils.isEmpty(msgStatus) && msgStatus.getStatus() != 2) {
                return DtoUtil.getFalseDto("不能重复回应", 21033);
            }
            //判断事件状态
            SingleEvent singleEvent = eventMapper.queryEventOne(fromId, feedbackInviteVo.getEventId());
            //如果自己回应
            if(feedbackInviteVo.getUserId().equals(singleEvent.getUserid().toString())){
                //更改邀请消息状态
                groupMapper.updGroupInviteMsg("3", feedbackInviteVo.getMsgId(),feedbackInviteVo.getUserId());
                return DtoUtil.getFalseDto("不能回应自己的邀请", 21034);
            }
            if (ObjectUtils.isEmpty(singleEvent) && singleEvent.getIsOverdue() != 0) {
                //更改邀请消息状态
                if (!ObjectUtils.isEmpty(groupInviteMsgStatus)){
                    groupMapper.updGroupInviteMsg("3", feedbackInviteVo.getMsgId(),feedbackInviteVo.getUserId());
                }else {
                    msgStatusMapper.updateMsgStatus("3", feedbackInviteVo.getMsgId());
                }
                return DtoUtil.getFalseDto("该事件已过期或者已被删除", 21034);
            }
            if (singleEvent.getPerson().indexOf(feedbackInviteVo.getUserId()) >= 0) {
                return DtoUtil.getFalseDto("已经加入了", 20199);
            }
            if (!ObjectUtils.isEmpty(tempEventMapper.queryTempEvent(singleEvent.getEventid().toString(), singleEvent.getUserid().toString()))) {
                return DtoUtil.getFalseDto("该事件正在修改中，不能加入", 2333);
            }
            logger.info("事件内容" + singleEvent.toString());
            SingleEvent singleEvent2 = new SingleEvent();
            singleEvent2.setUserid(singleEvent.getUserid());
            singleEvent2.setEventid(singleEvent.getEventid());
            singleEvent2.setPerson(singleEvent.getPerson());
            //如果同意
            if (YES.equals(feedbackInviteVo.getChoose())) {
                //修改事件userId
                singleEvent.setUserid(Long.parseLong(feedbackInviteVo.getUserId()));
                //这里开始判断是否是一个重复事件,如果状态值为真,则该事件为重复事件
                if (singleEvent.getIsLoop() == 1) {
                    List<SingleEvent> loopEventList = eventMapper.queryClashLoopEventList(singleEvent);
                    if (!SingleEventUtil.loopEventTime(loopEventList, singleEvent)) {
                        return DtoUtil.getFalseDto("时间段冲突,无法添加", 21012);
                    }
                } else if (!SingleEventUtil.eventTime(eventMapper.queryClashEventList(singleEvent), Long.valueOf(singleEvent.getStarttime()), Long.valueOf(singleEvent.getEndtime()))) {
                    return DtoUtil.getFalseDto("时间段冲突,无法添加", 21012);
                }
                //修改其他参与者的该事件
                eventUtil.updateInviterEvent(singleEvent2, feedbackInviteVo.getUserId());
                //事件添加到自己的事件轴
                EventPersons eventPersons = JSONObject.parseObject(singleEvent.getPerson(), EventPersons.class);
                String person = eventPersons.getFriendsId();
                if (StringUtils.isEmpty(person)) {
                    person = fromId;
                } else {
                    person = person.concat("," + fromId);
                }
                eventPersons.setFriendsId(person);
                singleEvent.setPerson(JSON.toJSONString(eventPersons));
                //查询该事件的清单
                List<BacklogList> backlogLists=backlogMapper.queryBacklogList(singleEvent.getId());
                logger.info("加入前事件id:===>"+singleEvent.getId());
                eventMapper.uploadingEvents(singleEvent);
                logger.info("加入后事件id:===>"+singleEvent.getId());
                //添加事件清单
                if (!ObjectUtils.isEmpty(backlogLists) && backlogLists.size()>0){
                    List<BacklogList> backlogLists1=new ArrayList<>();
                    for (BacklogList backlogList:backlogLists) {
                        backlogList.setSingleEventId(singleEvent.getId());
                        backlogLists1.add(backlogList);
                    }
                    backlogMapper.insertBacklog(backlogLists);
                }
                //事件副表中加入自己
                SingleEventVice singleEventVice = new SingleEventVice();
                singleEventVice.setCreateBy(Long.parseLong(fromId));
                singleEventVice.setUserId(Long.parseLong(feedbackInviteVo.getUserId()));
                singleEventVice.setEventId(singleEvent.getEventid());
                eventViceMapper.createEventVice(singleEventVice);
                //提醒其他参与者新成员加入
                RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                TxtMessage txtMessage = new TxtMessage("用户“" + accountMapper.queryAccount(feedbackInviteVo.getUserId()).getUserName() + "”加入了事件“" + singleEvent.getEventname() + "”，如计划有变可在首页选中待办事件进行调整", "");
                String[] persons = person.split(",");
                ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(SYSTEMID, persons, 0, txtMessage);
                //刷新
                rongCloudMethodUtil.refreshMsg(SYSTEMID,persons,"1");
                if (result.getCode() != 200) {
                    logger.info("新增邀请事件回应邀请时融云消息异常：" + result.toString());
                }
                //提醒用户事件添加成功   第一种事件同意之后智袖小助手会提示“xx”发起的事件已经加入你的时间轴，如计划有变可在首页选中待办事件进行调整
                TxtMessage txtMessage1 = new TxtMessage("“" + accountMapper.queryAccount(fromId).getUserName() + "”发起的事件“" + singleEvent.getEventname() + "”已经加入了你的时间轴，如计划有变可在首页选中待办事件进行调整", "");
                ResponseResult result1 = rongCloudMethodUtil.sendPrivateMsg(SYSTEMID, new String[]{feedbackInviteVo.getUserId()}, 0, txtMessage1);
                if (result1.getCode() != 200) {
                    logger.info("新增邀请事件回应邀请时融云消息异常：" + result.toString());
                }
                for (String s : persons) {
                    msgStatusMapper.addNewEventMsg(s, singleEvent.getEventid(), SYSTEMID, "帮您添加了一条新的邀请事件", System.currentTimeMillis() / 1000);
                }
                //更改邀请消息状态
                if (!ObjectUtils.isEmpty(groupInviteMsgStatus)){
                    groupMapper.updGroupInviteMsg(feedbackInviteVo.getChoose(),feedbackInviteVo.getMsgId(),feedbackInviteVo.getUserId());
                }else {
                    msgStatusMapper.updateMsgStatus(feedbackInviteVo.getChoose(), feedbackInviteVo.getMsgId());
                }
            } else if (NO.equals(feedbackInviteVo.getChoose())) {
                //如果拒绝
                //提醒事件发起者本人已拒绝
                RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                TxtMessage txtMessage = new TxtMessage("我拒绝了你的事件：“" + singleEvent.getEventname() + "”的邀请", "");
                ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(feedbackInviteVo.getUserId(), new String[]{fromId}, 0, txtMessage);
                if (result.getCode() != 200) {
                    logger.info("新增邀请事件回应邀请时融云消息异常：" + result.toString());
                }
                msgStatusMapper.addNewEventMsg(fromId, singleEvent.getEventid(), feedbackInviteVo.getUserId(), "拒绝了你的一条事件", System.currentTimeMillis() / 1000);
                //更改邀请消息状态
                if (!ObjectUtils.isEmpty(groupInviteMsgStatus)){
                    groupMapper.updGroupInviteMsg(feedbackInviteVo.getChoose(),feedbackInviteVo.getMsgId(),feedbackInviteVo.getUserId());
                }else {
                    msgStatusMapper.updateMsgStatus(feedbackInviteVo.getChoose(), feedbackInviteVo.getMsgId());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return DtoUtil.getSuccessDto("回应已发出", 100000);
    }

    /**
     * 修改一条邀请事件
     *
     * @param addInviteEventVo
     * @param token
     * @return
     */
    @Override
    public synchronized Dto updInviteEvent(AddInviteEventVo addInviteEventVo, String token) {
        try {
            if (!token.equals(stringRedisTemplate.opsForValue().get(addInviteEventVo.getUserId()))) {
                return DtoUtil.getFalseDto("请重新登录", 21014);
            }
            logger.info("修改一条邀请事件时输出的接收数据" + addInviteEventVo.toString());
            //接收到的修改信息
            SingleEvent singleEvent = JSONObject.parseObject(addInviteEventVo.getSingleEvent(), SingleEvent.class);
            //这里开始判断是否是一个重复事件,如果状态值为真,则该事件为重复事件
            singleEvent.setIsLoop(SingleEventUtil.isLoopEvent(singleEvent.getRepeaTtime()) ? 1 : 0);
            if (singleEvent.getIsLoop() == 1) {
                List<SingleEvent> loopEventList = eventMapper.queryClashLoopEventList(singleEvent);
                if (!SingleEventUtil.loopEventTime(loopEventList, singleEvent)) {
                    return DtoUtil.getFalseDto("时间段冲突,无法修改", 21012);
                }
            } else if (!SingleEventUtil.eventTime(eventMapper.queryClashEventList(singleEvent), Long.valueOf(singleEvent.getStarttime()), Long.valueOf(singleEvent.getEndtime()))) {
                return DtoUtil.getFalseDto("时间段冲突,无法修改", 21012);
            }
            //找到事件创建者
            SingleEventVice singleEventVice = new SingleEventVice();
            singleEventVice.setUserId(singleEvent.getUserid());
            singleEventVice.setEventId(singleEvent.getEventid());
            singleEventVice = eventViceMapper.queryEventVice(singleEventVice);
            SingleEvent st = tempEventMapper.queryTempEvent(singleEvent.getEventid().toString(), singleEventVice.getCreateBy().toString());
            if (!ObjectUtils.isEmpty(st)) {
                return DtoUtil.getFalseDto("该事件正在修改中，不能修改", 2333);
            }
            Map<String, String> newMap = SingleEvent.toMap(singleEvent);
            //原来的信息
            SingleEvent singleEventOld = eventMapper.queryEventOne(singleEvent.getUserid().toString(), singleEvent.getEventid().toString());
            if (singleEventOld.getIsOverdue() != 0) {
                return DtoUtil.getFalseDto("事件已过期", 21023);
            }
            Map<String, String> oldMap = SingleEvent.toMap(singleEventOld);
            //比较差异
            List<Map<String, String>> different = SingleEventUtil.eventDifferent(newMap, oldMap);
            EventPersons eventPersons = JSONObject.parseObject(singleEvent.getPerson(), EventPersons.class);
            //如果有新成员加入
            EventPersons eventPersons2 = JSONObject.parseObject(singleEventOld.getPerson(), EventPersons.class);
            //找出新成员
            String[] oldPerson = eventPersons2.getFriendsId().split(",");
            String[] newPerson = eventPersons.getFriendsId().split(",");
            List<String> o = Arrays.asList(oldPerson);
            logger.info("修改一条邀请事件时输出的老成员" + o.toString());
            List<String> n = Arrays.asList(newPerson);
            logger.info("修改一条邀请事件时输出的新成员" + n.toString());
            List<String> newFriends = new ArrayList<>();
            for (String person : newPerson) {
                if (!o.contains(person)) {
                    newFriends.add(person);
                }
            }
            if (different.size() == 0 && newFriends.size() == 0) {

                return DtoUtil.getFalseDto("没有任何更改", 29102);
            }
            logger.info("修改一条邀请事件时输出的修改的内容" + different);
            //如果是创建者修改
            if (singleEvent.getUserid().equals(singleEventVice.getCreateBy())) {
                if (newFriends.size() > 0) {
                    //给新成员发邀请消息
                    toldNewInviter(newFriends.toArray(new String[newFriends.size()]), singleEventOld, addInviteEventVo.getUserId());
                    //对修改后的事件参与成员做处理
                    singleEvent.setPerson(JSON.toJSONString(eventPersons2));
                }
                //如果暂时只有一个人
                if (StringUtils.isEmpty(eventPersons.getFriendsId())) {
                    //直接修改
                    if (eventMapper.alterEventsByUserId(singleEvent) > 0) {
                        //如果是IOS采用静默推送刷新事件详情
                        AppType appType=appTypeMapper.queryAppType(addInviteEventVo.getUserId());
                        if (appType.getAppType()==2L){
                            List<String> list=new ArrayList<>();
                            Map<String,Object> map=new HashMap<>(1);
                            map.put("refreshType",1);
                            list.add(appType.getDeviceToken());
                            IOSPushUtil.push(list,null,null,true,map,0,false);
                        }
                        return DtoUtil.getSuccessDto("修改成功", 100000);
                    } else {
                        return DtoUtil.getSuccessDto("修改失败", 2333);
                    }
                }
                if (different.size() > 0) {
                    //把要修改的事件放到临时表
                    //这里开始判断是否是一个重复事件,如果状态值为真,则该事件为重复事件
                    singleEvent.setIsLoop(SingleEventUtil.isLoopEvent(singleEvent.getRepeaTtime()) ? 1 : 0);
                    tempEventMapper.addTempEvent(singleEvent);
                    //生成统计表
                    List<StatisticsTable> tables = new ArrayList<>();
                    String[] friendsId = eventPersons2.getFriendsId().split(",");
                    for (String userId : friendsId) {
                        StatisticsTable statisticsTable = new StatisticsTable();
                        statisticsTable.setCreatorId(singleEventVice.getCreateBy());
                        statisticsTable.setEventId(singleEvent.getEventid());
                        statisticsTable.setUserId(Long.parseLong(userId));
                        tables.add(statisticsTable);
                        //生成同步历史
                        SynchronHistory syh = new SynchronHistory();
                        syh.setCreaterId(singleEventVice.getCreateBy());
                        syh.setSenderId(Long.parseLong(addInviteEventVo.getUserId()));
                        syh.setEventId(singleEvent.getEventid());
                        syh.setReceiverId(Long.parseLong(userId));
                        syh.setCreateDate(System.currentTimeMillis());
                        synchronHistoryMapper.addSynchronHistory(syh);
                    }
                    statisticsMapper.createStatistics(tables);
                    //给除了创建者之外的其他参与者发送信息
                    for (String friendId : friendsId) {
                        RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                        //消息状态保存在数据库
                        MsgStatus msgStatus = new MsgStatus();
                        msgStatus.setType(1L);
                        msgStatus.setUserId(Long.parseLong(addInviteEventVo.getUserId()));
                        msgStatus.setReceiverId(Long.parseLong(friendId));
                        if (msgStatusMapper.addNewMsg(msgStatus) == 0) {
                            logger.info("修改一条邀请事件时消息状态保存失败,id====>" + msgStatus.getId());
                        }
                        UpdateInviteMessage updateInviteMessage = new UpdateInviteMessage(singleEvent.getEventid().toString(), singleEventOld.getEventname(), String.valueOf(different.size()), singleEventOld.getType().toString(), different, msgStatus.getId().toString(), "1");
//                        logger.info(updateInviteMessage.toString());
                        ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(SYSTEMID, new String[]{friendId}, 0, updateInviteMessage);
                        if (result.getCode() != 200) {
                            logger.info("修改一条邀请事件时融云消息异常" + result.toString());
                            return DtoUtil.getFalseDto("消息发送失败", 21040);
                        }
                        msgStatusMapper.addNewEventMsg(friendId, singleEvent.getEventid(), addInviteEventVo.getUserId(), "想修改邀请事件", System.currentTimeMillis() / 1000);
                    }
                }
            } else {//不是创建者修改
                if (different.size() == 0) {
                    return DtoUtil.getFalseDto("没有任何更改", 29102);
                }
                //把要修改的事件放到临时表
                singleEvent.setUserid(singleEventVice.getCreateBy());
                SingleEvent singleEvent1 = eventMapper.queryEventOne(singleEventVice.getCreateBy().toString(), singleEvent.getEventid().toString());
                singleEvent.setPerson(singleEvent1.getPerson());
                //这里开始判断是否是一个重复事件,如果状态值为真,则该事件为重复事件
                singleEvent.setIsLoop(SingleEventUtil.isLoopEvent(singleEvent.getRepeaTtime()) ? 1 : 0);
                tempEventMapper.addTempEvent(singleEvent);
                //如果只邀请了一个人,直接给创建者发消息
                logger.info("修改一条邀请事件时的参与者：" + eventPersons.toString());
                if (eventPersons.getFriendsId().equals(singleEventVice.getCreateBy().toString())) {
                    RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                    //消息状态保存在数据库
                    MsgStatus msgStatus = new MsgStatus();
                    msgStatus.setType(1L);
                    msgStatus.setUserId(Long.valueOf(addInviteEventVo.getUserId()));
                    msgStatus.setReceiverId(singleEventVice.getCreateBy());
                    if (msgStatusMapper.addNewMsg(msgStatus) == 0) {
                        logger.info("修改一条邀请事件时消息状态保存失败,id====>" + msgStatus.getId());
                    }
                    CreateInviteMessage createInviteMessage = new CreateInviteMessage(singleEvent.getEventid().toString(), singleEventOld.getEventname(), String.valueOf(different.size()), singleEventOld.getType().toString(), different, msgStatus.getId().toString(), "1");
                    logger.info(createInviteMessage.toString());
                    ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(SYSTEMID, new String[]{singleEventVice.getCreateBy().toString()}, 0, createInviteMessage);
                    if (result.getCode() != 200) {
                        logger.info("修改一条邀请事件时融云消息异常" + result.toString());
                        return DtoUtil.getFalseDto("消息发送失败", 21040);
                    }
                    msgStatusMapper.addNewEventMsg(singleEventVice.getCreateBy().toString(), singleEvent.getEventid(), addInviteEventVo.getUserId(), "想修改邀请事件", System.currentTimeMillis() / 1000);
                } else {
                    //如果不止一人
                    //生成统计表(不包括创建者)
                    List<StatisticsTable> tables = new ArrayList<>();
                    String[] friendsId = eventPersons.getFriendsId().split(",");
                    for (String userId : friendsId) {
                        if (!singleEventVice.getCreateBy().toString().equals(userId)) {
                            StatisticsTable statisticsTable = new StatisticsTable();
                            statisticsTable.setCreatorId(singleEventVice.getCreateBy());
                            statisticsTable.setEventId(singleEvent.getEventid());
                            statisticsTable.setUserId(Long.parseLong(userId));
                            tables.add(statisticsTable);
                            //生成同步历史
                            SynchronHistory syh = new SynchronHistory();
                            syh.setCreaterId(singleEventVice.getCreateBy());
                            syh.setSenderId(Long.parseLong(addInviteEventVo.getUserId()));
                            syh.setEventId(singleEvent.getEventid());
                            syh.setReceiverId(Long.parseLong(userId));
                            syh.setCreateDate(System.currentTimeMillis());
                            synchronHistoryMapper.addSynchronHistory(syh);
                        }
                    }
                    statisticsMapper.createStatistics(tables);
                    //给除了创建者之外的其他参与者发送信息
                    for (String friendId : friendsId) {
                        if (!singleEventVice.getCreateBy().toString().equals(friendId)) {
                            RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                            //消息状态保存在数据库
                            MsgStatus msgStatus = new MsgStatus();
                            msgStatus.setType(1L);
                            msgStatus.setUserId(Long.parseLong(addInviteEventVo.getUserId()));
                            msgStatus.setReceiverId(Long.parseLong(friendId));
                            if (msgStatusMapper.addNewMsg(msgStatus) == 0) {
                                logger.info("修改一条邀请事件时消息状态保存失败,id====>" + msgStatus.getId());
                            }
                            UpdateInviteMessage updateInviteMessage = new UpdateInviteMessage(singleEvent.getEventid().toString(), singleEventOld.getEventname(), String.valueOf(different.size()), singleEventOld.getType().toString(), different, msgStatus.getId().toString(), "1");
                            logger.info(updateInviteMessage.toString());
                            ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(SYSTEMID, new String[]{friendId}, 0, updateInviteMessage);
                            if (result.getCode() != 200) {
                                logger.info("修改一条邀请事件时融云消息异常" + result.toString());
                                return DtoUtil.getFalseDto("消息发送失败", 21040);
                            }
                            msgStatusMapper.addNewEventMsg(friendId, singleEvent.getEventid(), addInviteEventVo.getUserId(), "想修改邀请事件", System.currentTimeMillis() / 1000);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return DtoUtil.getFalseDto("消息已发送", 100000);
    }

    /**
     * 回应邀请事件修改
     *
     * @param feedbackEventInviteVo
     * @param token
     * @return
     */
    @Override
    public synchronized Dto feedbackEventInvite(FeedbackEventInviteVo feedbackEventInviteVo, String token) {
        try {
            if (!token.equals(stringRedisTemplate.opsForValue().get(feedbackEventInviteVo.getUserId()))) {
                return DtoUtil.getFalseDto("请重新登录", 21014);
            }
            //不能重复点击按钮
            if (msgStatusMapper.queryMsg(feedbackEventInviteVo.getMsgId()).getStatus() != 2) {
                return DtoUtil.getFalseDto("你已经选择过了", 2333);
            }
            //拿到发起者的事件(先查最高权限表，然后再差事件临时表)
            SingleEventVice vice = new SingleEventVice();
            vice.setUserId(Long.parseLong(feedbackEventInviteVo.getUserId()));
            vice.setEventId(Long.parseLong(feedbackEventInviteVo.getEventId()));
            vice = eventViceMapper.queryEventVice(vice);
            SingleEvent singleEvent = tempEventMapper.queryTempEvent(feedbackEventInviteVo.getEventId(), vice.getCreateBy().toString());
            //判断修改是否过期
            if (ObjectUtils.isEmpty(singleEvent)) {
                //修改消息状态
                msgStatusMapper.updateMsgStatus("3", feedbackEventInviteVo.getMsgId());
                return DtoUtil.getFalseDto("该事件已修改成功，不能选择", 2333);
            }
            //如果同意修改
            if (YES.equals(feedbackEventInviteVo.getChoose())) {
                //判断事件冲突
                singleEvent.setUserid(Long.parseLong(feedbackEventInviteVo.getUserId()));
                if (singleEvent.getIsLoop() == 1) {
                    List<SingleEvent> loopEventList = eventMapper.queryClashLoopEventList(singleEvent);
                    if (!SingleEventUtil.loopEventTime(loopEventList, singleEvent)) {
                        return DtoUtil.getFalseDto("时间段冲突,无法修改", 21012);
                    }
                } else if (!SingleEventUtil.eventTime(eventMapper.queryClashEventList(singleEvent), Long.valueOf(singleEvent.getStarttime()), Long.valueOf(singleEvent.getEndtime()))) {
                    return DtoUtil.getFalseDto("时间段冲突,无法修改", 21012);
                }
                //修改统计表
                StatisticsTable statisticsTable = new StatisticsTable();
                statisticsTable.setUserId(Long.parseLong(feedbackEventInviteVo.getUserId()));
                statisticsTable.setEventId(singleEvent.getEventid());
                statisticsTable.setCreatorId(vice.getCreateBy());
                statisticsTable.setChoose(0L);
                statisticsTable.setModify(1L);
                logger.info("回应邀请事件修改时输出的统计表内容：" + statisticsTable.toString());
                statisticsMapper.updateStatistics(statisticsTable);
                //修改同步历史
                SynchronHistory syh = new SynchronHistory();
                syh.setCreaterId(vice.getCreateBy());
                syh.setEventId(Long.parseLong(feedbackEventInviteVo.getEventId()));
                syh.setReceiverId(Long.parseLong(feedbackEventInviteVo.getUserId()));
                syh.setStatus(1);
                syh.setIsSucceed(-1);
                synchronHistoryMapper.updSynchronHistory(syh);
                //查询统计表同意者是否达到50%（如果发起修改的人是创建者达到50%直接修改——修改后记得删除临时表和统计表）
                Map<String, Long> map = statisticsMapper.queryFeedbackStatistics(vice.getCreateBy().toString(), singleEvent.getEventid().toString());
                if (map.get("agree") / Double.valueOf(map.get("total")) >= 0.5) {
                    //如果发起修改的人是创建者达到50%直接修改——修改后记得删除临时表和统计表
                    if (feedbackEventInviteVo.getFromId().equals(vice.getCreateBy().toString())) {
                        String creatToken = stringRedisTemplate.opsForValue().get(feedbackEventInviteVo.getFromId());
                        EventCreatorChooseVo eventCreatorChooseVo = new EventCreatorChooseVo();
                        eventCreatorChooseVo.setAppType(feedbackEventInviteVo.getAppType());
                        eventCreatorChooseVo.setUserId(vice.getCreateBy().toString());
                        eventCreatorChooseVo.setEventId(singleEvent.getEventid().toString());
                        eventCreatorChooseVo.setChoose("1");
                        eventCreatorChoose(eventCreatorChooseVo, creatToken);
                        //修改消息状态
                        msgStatusMapper.updateMsgStatus(feedbackEventInviteVo.getChoose(), feedbackEventInviteVo.getMsgId());
                        return DtoUtil.getSuccessDto("修改结果发送成功", 100000);
                    }
                    //发送统计信息和修改详情给创建者
                    Map<String, String> m1 = SingleEvent.toMap(singleEvent);
                    //原来的信息
                    SingleEvent singleEventOld = eventMapper.queryEventOne(singleEvent.getUserid().toString(), singleEvent.getEventid().toString());
                    Map<String, String> m2 = SingleEvent.toMap(singleEventOld);
                    //比较差异
                    List<Map<String, String>> different = SingleEventUtil.eventDifferent(m1, m2);
                    RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                    MsgStatus msgStatus = new MsgStatus();
                    msgStatus.setType(1L);
                    msgStatus.setUserId(Long.valueOf(feedbackEventInviteVo.getUserId()));
                    msgStatus.setReceiverId(Long.valueOf(SYSTEMID));
                    if (msgStatusMapper.addNewMsg(msgStatus) == 0) {
                        logger.info("回应邀请事件修改时消息状态保存失败,id====>" + msgStatus.getId());
                    }
                    CreateInviteMessage createInviteMessage = new CreateInviteMessage(singleEvent.getEventid().toString(), singleEventOld.getEventname(), String.valueOf(different.size()), singleEventOld.getType().toString(), different, msgStatus.getId().toString(), "");
                    ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(SYSTEMID, new String[]{vice.getCreateBy().toString()}, 0, createInviteMessage);
                    if (result.getCode() != 200) {
                        logger.info("回应邀请事件修改时融云消息异常" + result.toString());
                        return DtoUtil.getFalseDto("消息发送失败", 21040);
                    }
                    msgStatusMapper.addNewEventMsg(vice.getCreateBy().toString(), singleEvent.getEventid(), SYSTEMID, ":超过50%的人接受修改你创建的邀请事件", System.currentTimeMillis() / 1000);
                }
                //修改消息状态
                msgStatusMapper.updateMsgStatus(feedbackEventInviteVo.getChoose(), feedbackEventInviteVo.getMsgId());
            } else {//如果选拒绝
                //修改统计表
                StatisticsTable statisticsTable = new StatisticsTable();
                statisticsTable.setUserId(Long.parseLong(feedbackEventInviteVo.getUserId()));
                statisticsTable.setEventId(singleEvent.getEventid());
                statisticsTable.setCreatorId(vice.getCreateBy());
                statisticsTable.setChoose(1L);
                statisticsTable.setModify(1L);
                logger.info("回应邀请事件修改时输出的统计表内容：" + statisticsTable.toString());
                statisticsMapper.updateStatistics(statisticsTable);
                //修改同步历史
                SynchronHistory syh = new SynchronHistory();
                syh.setCreaterId(vice.getCreateBy());
                syh.setEventId(Long.parseLong(feedbackEventInviteVo.getEventId()));
                syh.setReceiverId(Long.parseLong(feedbackEventInviteVo.getUserId()));
                syh.setStatus(0);
                syh.setIsSucceed(-1);
                synchronHistoryMapper.updSynchronHistory(syh);
                //查询统计表拒绝者是否超过50%，则修改失败
                Map<String, Long> map = statisticsMapper.queryFeedbackStatistics(vice.getCreateBy().toString(), singleEvent.getEventid().toString());
                if (map.get("refuse") / Double.valueOf(map.get("total")) > 0.5) {
                    //删除临时表的事件
                    tempEventMapper.deleteTempEvent(singleEvent.getEventid().toString(), vice.getCreateBy().toString());
                    //删除统计表
                    statisticsMapper.deleteStatistics(vice.getCreateBy().toString(), singleEvent.getEventid().toString());
                    //通知所有人事件修改失败
                    RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                    TxtMessage txtMessage = new TxtMessage("由于超过50%的人拒绝修改事件“" + singleEvent.getEventname() + "”，该事件修改失败", "");
                    EventPersons eventPersons = JSONObject.parseObject(singleEvent.getPerson(), EventPersons.class);
                    String inviteIds = eventPersons.getFriendsId().concat("," + singleEvent.getUserid().toString());
                    logger.info("回应邀请事件修改时给谁发了消息==>" + Arrays.toString(inviteIds.split(",")));
                    String[] inviters = inviteIds.split(",");
                    ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(SYSTEMID, inviters, 0, txtMessage);
                    if (result.getCode() != 200) {
                        logger.info("回应邀请事件修改时融云消息异常" + result.toString());
                        return DtoUtil.getFalseDto("消息发送失败", 21040);
                    }
                    for (String s : inviters) {
                        msgStatusMapper.addNewEventMsg(s, singleEvent.getEventid(), SYSTEMID, ":您的事件修改失败", System.currentTimeMillis() / 1000);
                    }
                }
                //修改消息状态
                msgStatusMapper.updateMsgStatus(feedbackEventInviteVo.getChoose(), feedbackEventInviteVo.getMsgId());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return DtoUtil.getSuccessDto("回应已发出", 100000);
    }

    /**
     * 修改事件时创建者选择
     *
     * @param eventCreatorChooseVo
     * @param token
     * @return
     */
    @Override
    public synchronized Dto eventCreatorChoose(EventCreatorChooseVo eventCreatorChooseVo, String token) {
        try {
            if (!token.equals(stringRedisTemplate.opsForValue().get(eventCreatorChooseVo.getUserId()))) {
                return DtoUtil.getFalseDto("请重新登录", 21014);
            }
            if (eventCreatorChooseVo.getMsgId() != null) {
                if (msgStatusMapper.queryMsg(eventCreatorChooseVo.getMsgId()).getStatus() != 2) {
                    return DtoUtil.getFalseDto("不能重复选择", 2333);
                }
            }
            //先拿到事件
            SingleEvent singleEvent = tempEventMapper.queryTempEvent(eventCreatorChooseVo.getEventId(), eventCreatorChooseVo.getUserId());
            if (ObjectUtils.isEmpty(singleEvent)) {
                //修改消息状态
                if (eventCreatorChooseVo.getMsgId() != null) {
                    msgStatusMapper.updateMsgStatus("3", eventCreatorChooseVo.getMsgId());
                }
                return DtoUtil.getFalseDto("该事件已修改成功，不能选择", 2333);
            }
            //存放需要修改事件的用户id
            List<String> list = new ArrayList();
            //如果同意修改
            if (YES.equals(eventCreatorChooseVo.getChoose())) {
                list.add(eventCreatorChooseVo.getUserId());
                //查询创建者时间冲突
                List<SingleEvent> singleEventList = new ArrayList<>();
                if (singleEvent.getIsLoop() == 1) {
                    List<SingleEvent> loopEventList = eventMapper.queryClashLoopEventList(singleEvent);
                    if (!SingleEventUtil.loopEventTime(loopEventList, singleEvent)) {
                        singleEventList.addAll(loopEventList);
                    }
                } else if (!SingleEventUtil.eventTime(eventMapper.queryClashEventList(singleEvent), Long.valueOf(singleEvent.getStarttime()), Long.valueOf(singleEvent.getEndtime()))) {
                    singleEventList.addAll(new ArrayList<>());
                }
                EventPersons eventPersons = JSONObject.parseObject(singleEvent.getPerson(), EventPersons.class);
                for (String inviterId : eventPersons.getFriendsId().split(",")) {
                    list.add(inviterId);
                    //查询参与者时间冲突
                    singleEvent.setUserid(Long.valueOf(inviterId));
                    if (singleEvent.getIsLoop() == 1) {
                        List<SingleEvent> loopEventList = eventMapper.queryClashLoopEventList(singleEvent);
                        if (!SingleEventUtil.loopEventTime(loopEventList, singleEvent)) {
                            singleEventList.addAll(loopEventList);
                        }
                    } else if (!SingleEventUtil.eventTime(eventMapper.queryClashEventList(singleEvent), Long.valueOf(singleEvent.getStarttime()), Long.valueOf(singleEvent.getEndtime()))) {
                        singleEventList.addAll(new ArrayList<>());
                    }
                }
                if (singleEventList.size() > 0) {
                    return DtoUtil.getFalseDto("该事件与你的或者其他成员的事件时间段冲突，不能修改", 21016);
                }
                //修改事件
                for (String userId : list) {
                    SingleEvent singleEvent1 = eventMapper.queryEventOne(userId, singleEvent.getEventid().toString());
                    singleEvent.setUserid(Long.valueOf(userId));
                    singleEvent.setPerson(singleEvent1.getPerson());
                    eventMapper.alterEventsByUserId(singleEvent);
                    //如果是IOS采用静默推送刷新事件详情     refreshType：1修改邀请事件刷新；2：实名认证
                    AppType appType=appTypeMapper.queryAppType(userId);
                    if (appType.getAppType()==2L){
                        List<String> deviceToken=new ArrayList<>();
                        Map<String,Object> map=new HashMap<>(1);
                        map.put("refreshType",1);
                        deviceToken.add(appType.getDeviceToken());
                        IOSPushUtil.push(deviceToken,null,null,true,map,0,false);
                    }
                }
                //删除临时表的事件
                tempEventMapper.deleteTempEvent(singleEvent.getEventid().toString(), eventCreatorChooseVo.getUserId());
                //删除统计表
                statisticsMapper.deleteStatistics(eventCreatorChooseVo.getUserId(), singleEvent.getEventid().toString());
                //修改同步历史
                SynchronHistory syh = new SynchronHistory();
                syh.setCreaterId(Long.parseLong(eventCreatorChooseVo.getUserId()));
                syh.setEventId(Long.parseLong(eventCreatorChooseVo.getEventId()));
                syh.setIsSucceed(1);
                syh.setStatus(-1);
                synchronHistoryMapper.updSynchronHistory(syh);
                //通知所有人事件修改成功
                RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                TxtMessage txtMessage = new TxtMessage("事件“" + singleEvent.getEventname() + "”已经修改成功", "");
                logger.info("修改事件时创建者选择时给谁发了消息==>" + Arrays.toString(list.toArray(new String[list.size()])));
                String[] userIds = list.toArray(new String[list.size()]);
                ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(SYSTEMID, userIds, 0, txtMessage);
                if (result.getCode() != 200) {
                    logger.info("修改事件时创建者选择时融云消息异常" + result.toString());
                    return DtoUtil.getFalseDto("消息发送失败", 21040);
                }
                for (String s : userIds) {
                    msgStatusMapper.addNewEventMsg(s, singleEvent.getEventid(), SYSTEMID, ":事件修改成功", System.currentTimeMillis() / 1000);
                }
                //修改消息状态
                if (eventCreatorChooseVo.getMsgId() != null) {
                    msgStatusMapper.updateMsgStatus(eventCreatorChooseVo.getChoose(), eventCreatorChooseVo.getMsgId());
                }
            } else {
                //如果不同意修改
                //删除临时表的事件
                tempEventMapper.deleteTempEvent(singleEvent.getEventid().toString(), eventCreatorChooseVo.getUserId());
                //删除统计表
                statisticsMapper.deleteStatistics(eventCreatorChooseVo.getUserId(), singleEvent.getEventid().toString());
                //通知所有人事件修改失败
                RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                TxtMessage txtMessage = new TxtMessage("事件“" + singleEvent.getEventname() + "”修改失败", "");
                EventPersons eventPersons = JSONObject.parseObject(singleEvent.getPerson(), EventPersons.class);
                String[] userIds = eventPersons.getFriendsId().split(",");
                logger.info("修改事件时创建者选择时给谁发了消息==>" + Arrays.toString(userIds));
                ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(SYSTEMID, userIds, 0, txtMessage);
                if (result.getCode() != 200) {
                    logger.info("修改事件时创建者选择时融云消息异常" + result.toString());
                    return DtoUtil.getFalseDto("消息发送失败", 21040);
                }
                for (String s : userIds) {
                    msgStatusMapper.addNewEventMsg(s, singleEvent.getEventid(), SYSTEMID, "", System.currentTimeMillis() / 1000);
                }
                //修改消息状态
                if (eventCreatorChooseVo.getMsgId() != null) {
                    msgStatusMapper.updateMsgStatus(eventCreatorChooseVo.getChoose(), eventCreatorChooseVo.getMsgId());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return DtoUtil.getSuccessDto("修改结果发送成功", 100000);
    }

    /**
     * 删除一条邀请事件
     *
     * @param receivedSearchOnce
     * @param token
     * @return
     */
    @Override
    public synchronized Dto delInviteEvent(ReceivedSearchOnce receivedSearchOnce, String token) {
        try {
            if (!token.equals(stringRedisTemplate.opsForValue().get(receivedSearchOnce.getUserId()))) {
                return DtoUtil.getFalseDto("请重新登录", 21014);
            }
            //找到该事件
            SingleEvent singleEvent = eventMapper.queryEventOne(receivedSearchOnce.getUserId(), receivedSearchOnce.getEventId());
            //查看该事件最高权限
            SingleEventVice singleEventVice = new SingleEventVice();
            singleEventVice.setUserId(Long.parseLong(receivedSearchOnce.getUserId()));
            singleEventVice.setEventId(Long.parseLong(receivedSearchOnce.getEventId()));
            singleEventVice = eventViceMapper.queryEventVice(singleEventVice);
            if (ObjectUtils.isEmpty(singleEvent) || ObjectUtils.isEmpty(singleEventVice)) {
                return DtoUtil.getFalseDto("要删除的事件未找到", 29001);
            }
            //如果是创建者删除
            if (singleEvent.getUserid().equals(singleEventVice.getCreateBy())) {
                //该事件从创建者时间轴删除
                eventMapper.deleteByDeleteType(singleEvent.getEventid(), "singleevent", receivedSearchOnce.getUserId(), String.valueOf(System.currentTimeMillis() / 1000));
                //其他参与者的事件里删除本参与者
                EventPersons eventPersons = JSONObject.parseObject(singleEvent.getPerson(), EventPersons.class);
                logger.info("删除邀请事件接口输出的person" + eventPersons.toString());
                String[] persons = eventPersons.getFriendsId().split(",");
                for (int j = 0; j < persons.length; j++) {
                    //其他参与者的事件
                    SingleEvent singleEvent1 = eventMapper.queryEventOne(persons[j], singleEvent.getEventid().toString());
                    //变更参与者
                    eventPersons = JSONObject.parseObject(singleEvent1.getPerson(), EventPersons.class);
                    String[] person = eventPersons.getFriendsId().split(",");
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < person.length; i++) {
                        if (!person[i].equals(receivedSearchOnce.getUserId())) {
                            list.add(person[i]);
                        }
                    }
                    String finalPerson = String.join(",", list);
                    eventPersons.setFriendsId(finalPerson);
                    singleEvent1.setPerson(JSON.toJSONString(eventPersons));
                    eventMapper.alterEventsByUserId(singleEvent1);
                }
                //删除最高权限表的自己
                eventViceMapper.deleteEventVice(singleEvent.getEventid().toString(), receivedSearchOnce.getUserId());
                //最高权限表更改
                Random random = new Random();
                int i = random.nextInt(persons.length);
                eventViceMapper.updateEventVice(singleEvent.getEventid().toString(), singleEventVice.getCreateBy().toString(), persons[i]);
                //给最高权限者发送信息
                String[] targetId = {persons[i]};
                RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                //发送消息给最高权限者
                Account account = accountMapper.queryAccount(receivedSearchOnce.getUserId());
                String content = account.getUserName() + "退出了事件：" + singleEvent.getEventname() + "，你已成为该事件的创建者，拥有该事件的决策权。";
                TxtMessage txtMessage = new TxtMessage(content, "");
                logger.info("创建事件时创建者选择输出的消息内容：" + txtMessage.getContent());
                ResponseResult result2 = rongCloudMethodUtil.sendPrivateMsg(SYSTEMID, targetId, 0, txtMessage);
                if (result2.getCode() != 200) {
                    logger.info("融云消息异常" + result2.toString());
                    return DtoUtil.getFalseDto("消息发送失败", 21040);
                }
                for (String s : targetId) {
                    msgStatusMapper.addNewEventMsg(s, singleEvent.getEventid(), SYSTEMID, ":您被分配为一条事件的创建者,快去看看吧", System.currentTimeMillis() / 1000);
                }
            } else {
                //如果不是创建者删除
                //从自己的事件表里移除
                //删除最高权限表的自己
                eventViceMapper.deleteEventVice(singleEvent.getEventid().toString(), receivedSearchOnce.getUserId());
                eventMapper.deleteByDeleteType(singleEvent.getEventid(), "singleevent", receivedSearchOnce.getUserId(), String.valueOf(System.currentTimeMillis() / 1000));
                //其他参与者的事件里删除本参与者
                EventPersons eventPersons = JSONObject.parseObject(singleEvent.getPerson(), EventPersons.class);
                String[] persons = eventPersons.getFriendsId().split(",");
                for (int j = 0; j < persons.length; j++) {
                    //其他参与者的事件
                    SingleEvent singleEvent1 = eventMapper.queryEventOne(persons[j], singleEvent.getEventid().toString());
                    //变更参与者
                    eventPersons = JSONObject.parseObject(singleEvent1.getPerson(), EventPersons.class);
                    String[] person = eventPersons.getFriendsId().split(",");
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < person.length; i++) {
                        if (!person[i].equals(receivedSearchOnce.getUserId())) {
                            list.add(person[i]);
                        }
                    }
                    String finalPerson = String.join(",", list);
                    eventPersons.setFriendsId(finalPerson);
                    singleEvent1.setPerson(JSON.toJSONString(eventPersons));
                    int delResult = eventMapper.alterEventsByUserId(singleEvent1);
                    if (delResult <= 0) {
                        //回滚
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return DtoUtil.getFalseDto("其他参与者的事件里删除本参与者失败", 29002);
                    }
                }
                //通知最高权限者
                Account account = accountMapper.queryAccount(receivedSearchOnce.getUserId());
                RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                String content = account.getUserName() + "退出了事件：" + singleEvent.getEventname() + "。";
                logger.info("消息内容" + content);
                TxtMessage txtMessage = new TxtMessage(content, "");
                ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(SYSTEMID, new String[]{singleEventVice.getCreateBy().toString()}, 0, txtMessage);
                if (result.getCode() != 200) {
                    logger.info("融云消息异常" + result.toString());
                    return DtoUtil.getFalseDto("消息发送失败", 21040);
                }
                msgStatusMapper.addNewEventMsg(singleEventVice.getCreateBy().toString(), singleEvent.getEventid(), SYSTEMID, ":" + account.getUserName() + "退出了您创建的事件", System.currentTimeMillis() / 1000);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return DtoUtil.getFalseDto("事件删除失败", 26002);
        }
        return DtoUtil.getSuccessDto("事件删除成功", 100000);
    }

    @Override
    public Dto getTodayPlans(ReceivedId receivedId, String token) {
        if (!ObjectUtils.isEmpty(receivedId)) {
            SearchEventVo searchEventVo = new SearchEventVo();
            searchEventVo.setUserId(receivedId.getUserId());
            searchEventVo.setDayEventId(DateUtil.getDay(0));
            searchEventVo.setFriendId("seaPlans");
            return searchByDayEventIds(searchEventVo, token);
        }
        return DtoUtil.getSuccesWithDataDto("未获取到今天的事件", null, 200000);
    }

    @Override
    public Dto searchByDayForIOS(SearchConditionsForIOS searchConditionsForIOS, String token) {
        if (StringUtils.isEmpty(searchConditionsForIOS.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("操作失败,token未获取到", 21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(searchConditionsForIOS.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        SingleEvent singleEvent = new SingleEvent();
        StringBuffer stringBuffer = new StringBuffer(searchConditionsForIOS.getDate());
        singleEvent.setUserid(Long.valueOf(searchConditionsForIOS.getUserId()));
        singleEvent.setIsOverdue((long) searchConditionsForIOS.getStatus());
        singleEvent.setIsLoop(searchConditionsForIOS.getIsLoop());
        singleEvent.setYear(Long.valueOf(stringBuffer.substring(0, 4)));
        singleEvent.setMonth(Long.valueOf(stringBuffer.substring(4, 6)));
        singleEvent.setDay(Long.valueOf(stringBuffer.substring(6, 8)));
        List<SingleEvent> singleEventList = eventMapper.queryEventsByDayForIOS(singleEvent);
        if (singleEventList.size() != 0) {
            return DtoUtil.getSuccesWithDataDto("查询成功", SingleEventUtil.getShowSingleEventList(singleEventList), 100000);
        }
        return DtoUtil.getFalseDto("未查询到数据", 100000);
    }

    @Override
    public Dto deleteInBatches(ReceivedDeleteEventIds receivedDeleteEventIds, String token) {
        try {
            if (StringUtils.isEmpty(receivedDeleteEventIds.getUserId())) {
                return DtoUtil.getFalseDto("请先登录", 21011);
            }
            if (!StringUtils.hasText(token)) {
                return DtoUtil.getFalseDto("token未获取到", 21013);
            }
            if (!token.equals(stringRedisTemplate.opsForValue().get(receivedDeleteEventIds.getUserId()))) {
                return DtoUtil.getFalseDto("请重新登录", 21014);
            }
            if ("0".equals(receivedDeleteEventIds.getDeleteType())) {
                receivedDeleteEventIds.setDeleteType("singleevent");
            } else if ("1".equals(receivedDeleteEventIds.getDeleteType())) {
                receivedDeleteEventIds.setDeleteType("draft");
            }
            for (Long eventId : receivedDeleteEventIds.getEventIds()) {
                SingleEvent singleEvent = eventMapper.getAEvent(receivedDeleteEventIds.getUserId(), eventId, receivedDeleteEventIds.getDeleteType());
                if (eventMapper.deleteByDeleteType(eventId, receivedDeleteEventIds.getDeleteType(), receivedDeleteEventIds.getUserId(), String.valueOf(System.currentTimeMillis() / 1000)) == 0) {
                    return DtoUtil.getFalseDto("删除失败", 21006);
                }
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getFalseDto("批量删除失败,请重新删除", 21015);
        }
        return DtoUtil.getSuccessDto("批量删除成功", 100000);
    }

    /**
     * 查询一个草稿事件
     *
     * @param receivedSearchOnce
     * @param token
     * @return
     */
    @Override
    public Dto searchDraftOnce(ReceivedSearchOnce receivedSearchOnce, String token) {
        if (StringUtils.isEmpty(receivedSearchOnce.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedSearchOnce.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        SingleEvent singleEvent = eventMapper.queryDraftOne(receivedSearchOnce.getUserId(), receivedSearchOnce.getEventId());
        Map<String, Object> maps = new HashMap<>();
        ArrayList<Map> list = new ArrayList<>();
        if (!StringUtils.isEmpty(singleEvent.getPerson())) {
            EventPersons eventPersons = JSONObject.parseObject(singleEvent.getPerson(), EventPersons.class);
            if (!StringUtils.isEmpty(eventPersons.getFriendsId())) {
                String[] friendsId = eventPersons.getFriendsId().split(",");
                for (String friendId : friendsId) {
                    Map map = new HashMap();
                    Account account = accountMapper.queryAccount(friendId);
                    map.put("friendId", account.getId().toString());
                    map.put("userCode", account.getUserCode());
                    map.put("userName", account.getUserName());
                    map.put("headImgUrl", account.getHeadImgUrl());
                    map.put("gender", account.getGender().toString());
                    list.add(map);
                }
            }
            maps.put("friendsId", list);
            maps.put("others", eventPersons.getOthers());
            singleEvent.setPerson(JSON.toJSONString(maps));
            System.out.println("显示一个事件详情时输出的" + singleEvent.getPerson());
            SingleEventAndBacklog singleEventAndBacklog=JSONObject.parseObject(JSON.toJSONString(singleEvent),SingleEventAndBacklog.class);
            //查询该事件的清单
            List<BacklogList> backlogLists=backlogMapper.queryDraftBacklogList(singleEvent.getId());
            //查询清单权限
            if (backlogLists.size()>0){
                singleEventAndBacklog.setIsSync(backlogLists.get(0).getIsSync().toString());
            }
            singleEventAndBacklog.setBacklogList(backlogLists);
            return DtoUtil.getSuccesWithDataDto("查询成功", SingleEventUtil.getShowSingleEvent(singleEventAndBacklog), 100000);
        } else {
            singleEvent.setPerson(JSON.toJSONString(new EventPersons()));
            System.out.println("显示一个事件详情时输出的" + singleEvent.getPerson());
            SingleEventAndBacklog singleEventAndBacklog=JSONObject.parseObject(JSON.toJSONString(singleEvent),SingleEventAndBacklog.class);
            //查询该事件的清单
            List<BacklogList> backlogLists=backlogMapper.queryDraftBacklogList(singleEvent.getId());
            //查询清单权限
            if (backlogLists.size()>0){
                singleEventAndBacklog.setIsSync(backlogLists.get(0).getIsSync().toString());
            }
            singleEventAndBacklog.setBacklogList(backlogLists);
            return DtoUtil.getSuccesWithDataDto("查询成功", SingleEventUtil.getShowSingleEvent(singleEventAndBacklog), 100000);
        }
    }

    /**
     * 将事件从事件表移除到草稿箱
     *
     * @param addInviteEventVo
     * @param token
     * @return
     */
    @Override
    public Dto eventRemoveDraft(AddInviteEventVo addInviteEventVo, String token) {
        if (StringUtils.isEmpty(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        if (ObjectUtils.isEmpty(addInviteEventVo)) {
            return DtoUtil.getFalseDto("创建者选择数据未获取到", 26001);
        }
        if (StringUtils.isEmpty(addInviteEventVo.getUserId())) {
            return DtoUtil.getFalseDto("userId不能为空", 21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(addInviteEventVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        SingleEvent singleEvent = JSONObject.parseObject(addInviteEventVo.getSingleEvent(), SingleEvent.class);
        if (ObjectUtils.isEmpty(singleEvent)) {
            return DtoUtil.getFalseDto("事件格式错误", 23001);
        }
        //将该事件放入草稿箱
        int i = eventMapper.uplDraft(singleEvent);
        //将该事件从事件表彻底删除
        int j = eventMapper.deleteSingleEvent(singleEvent.getUserid().toString(), singleEvent.getEventid().toString());
        if (i <= 0 || j <= 0) {
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getFalseDto("事件移动到草稿箱失败", 23002);
        }
        return DtoUtil.getSuccessDto("事件移动到草稿箱成功", 100000);
    }

    @Override
    public Dto queryMsgStatus(QueryMsgStatusVo queryMsgStatusVo, String token) {
        if (StringUtils.isEmpty(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        if (ObjectUtils.isEmpty(queryMsgStatusVo)) {
            return DtoUtil.getFalseDto("数据未获取到", 26001);
        }
        if (StringUtils.isEmpty(queryMsgStatusVo.getUserId())) {
            return DtoUtil.getFalseDto("userId不能为空", 21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(queryMsgStatusVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        MsgStatus msgStatus = msgStatusMapper.queryMsg(queryMsgStatusVo.getMsgId());
        if (!ObjectUtils.isEmpty(msgStatus)) {
            Map<String, String> map = new HashMap<>();
            map.put("id", msgStatus.getId().toString());
            map.put("userId", msgStatus.getUserId().toString());
            map.put("status", msgStatus.getStatus().toString());
            map.put("type", msgStatus.getType().toString());
            return DtoUtil.getSuccesWithDataDto("查询消息状态成功", map, 100000);
        }
        //查询群聊消息状态
        GroupInviteMsgStatus msgStatusList=groupMapper.queryGroupInviteMsgStatus(msgStatus.getId(),queryMsgStatusVo.getUserId());
        if (!ObjectUtils.isEmpty(msgStatusList)){
            Map<String, String> map = new HashMap<>();
            map.put("id", msgStatus.getId().toString());
            map.put("userId", msgStatusList.getUserId().toString());
            map.put("status", msgStatusList.getStatus().toString());
            map.put("type", msgStatus.getType().toString());
            return DtoUtil.getSuccesWithDataDto("查询消息状态成功", map, 100000);
        }
        return DtoUtil.getFalseDto("查询消息状态失败", 200000);
    }

    @Override
    public Dto searchFriendEventOnce(ReceivedFriendEventOnce receivedFriendEventOnce, String token) {
        if (!StringUtils.hasText(receivedFriendEventOnce.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("操作失败,token未获取到", 21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedFriendEventOnce.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        Map<String, Object> result = new HashMap<>();
        if (userSettingsMapper.getIsHideFromFriend(receivedFriendEventOnce.getUserId(), receivedFriendEventOnce.getFriendId()) != 2) {
            result.put("userPrivatePermission", 0);
            return DtoUtil.getSuccesWithDataDto("该用户设置了查看权限", result, 200000);
        } else {
            result.put("userPrivatePermission", 1);
        }
        SingleEvent singleEvent = eventMapper.queryEventOne(receivedFriendEventOnce.getFriendId(), receivedFriendEventOnce.getEventId());
        if (ObjectUtils.isEmpty(singleEvent)) {
            return DtoUtil.getSuccessDto("没有数据", 200000);
        }
        SingleEventAndBacklog singleEventAndBacklog=JSONObject.parseObject(JSON.toJSONString(singleEvent),SingleEventAndBacklog.class);
        //查询该事件的清单
        List<BacklogList> backlogLists=backlogMapper.queryBacklogList(singleEvent.getId());
        //查询清单权限
        if (backlogLists.size()>0){
            singleEventAndBacklog.setIsSync(backlogLists.get(0).getIsSync().toString());
        }
        singleEventAndBacklog.setBacklogList(backlogLists);
        result.put("event", SingleEventUtil.getShowSingleEvent(singleEventAndBacklog));
        return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
    }

    /**
     * 判断邀请事件最高权限
     *
     * @param receivedSearchOnce
     * @param token
     * @return
     */
    @Override
    public Dto inviteEventJudge(ReceivedSearchOnce receivedSearchOnce, String token) {
        Map<String, String> map = new HashMap<>();
        map.put("authority", "0");
        try {
            if (!token.equals(stringRedisTemplate.opsForValue().get(receivedSearchOnce.getUserId()))) {
                return DtoUtil.getFalseDto("请重新登录", 21014);
            }
            //判断是否有权限添加
            SingleEventVice singleEventVice = new SingleEventVice();
            singleEventVice.setUserId(Long.parseLong(receivedSearchOnce.getUserId()));
            singleEventVice.setEventId(Long.parseLong(receivedSearchOnce.getEventId()));
            singleEventVice = eventViceMapper.queryEventVice(singleEventVice);
            if (!ObjectUtils.isEmpty(singleEventVice)) {
                if (receivedSearchOnce.getUserId().equals(singleEventVice.getCreateBy().toString())) {
                    map.put("authority", "1");
                    return DtoUtil.getSuccesWithDataDto("请求成功", map, 100000);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return DtoUtil.getFalseDto("请求失败", 2333);
        }
        return DtoUtil.getSuccesWithDataDto("请求成功", map, 100000);
    }

    private List<List<ShowSingleEvent>> getShowSingleEventListList(List<SingleEvent> list) {
        List<List<ShowSingleEvent>> loopEventList = new ArrayList<>();
        for (int i = 0; i <= 6; i++) {
            loopEventList.add(new ArrayList<>());
        }
        for (SingleEvent singleEvent1 : list) {
            ShowSingleEvent showSingleEvent = SingleEventUtil.getShowSingleEvent1(singleEvent1);
            Boolean[] booleans = showSingleEvent.getRepeaTtime();
            //根据拆分出来的boolean数组进行判断并添加到一周的各个天数中
            for (int i = 0; i <= 6; i++) {
                if (booleans[i]) {
                    loopEventList.get(i).add(showSingleEvent);
                }
            }
        }
        return loopEventList;
    }

    private List<DayEvents> getDayEventsList(String userId, String condition, String date) {
        List<DayEvents> dayEventsList = new ArrayList<>();
        SingleEvent singleEvent;
        for (int i = 0; i <= 6; i++) {
            DayEvents<ShowSingleEvent> dayEvents = new DayEvents();
            String dayEventId = DateUtil.getDay(i, date);
            singleEvent = SingleEventUtil.getSingleEvent(userId, dayEventId);
            List<SingleEvent> singleEventList;
            if ("all".equals(condition)) {
                singleEventList = completedLoopEvent(eventMapper.queryEvents(singleEvent));
            } else if ("few".equals(condition)) {
                List<SingleEvent> singleEventList1 = eventMapper.queryEventsWithFewInfo(singleEvent);
                singleEventList = completedLoopEvent(singleEventList1);
            } else {
                singleEventList = new ArrayList<>();
            }

            ArrayList<ShowSingleEvent> showSingleEventList = (ArrayList<ShowSingleEvent>) SingleEventUtil.getShowSingleEventList(singleEventList);
            dayEvents.setMySingleEventList(showSingleEventList);
            dayEvents.setTotalNum((long) dayEvents.getMySingleEventList().size());
            dayEvents.setUserId(Long.valueOf(userId));
            dayEvents.setDayEventId(Long.valueOf(dayEventId));
            dayEventsList.add(dayEvents);
        }
        return dayEventsList;
    }

    /**
     * 给新邀请的成员发消息
     *
     * @param newFriends
     */
    private void toldNewInviter(String[] newFriends, SingleEvent singleEvent, String userId) throws Exception {
        logger.info("新成员" + Arrays.toString(newFriends));
        String[] persons = newFriends;
        //要发送信息的人员
        ArrayList<String> personList1 = new ArrayList<>();
        //屏蔽的人员
        ArrayList<String> personList2 = new ArrayList<>();
        //判断好友是否开启了邀请权限
        for (int i = 0; i < persons.length; i++) {
            Friendship friendship = accountMapper.queryFriendshipDetail(persons[i], userId);
            UserSettings settings = userSettingsMapper.queryAllSettings(persons[i]);
            if (friendship.getInvite() == 1 && settings.getFriendInvite() == 0) {
                //只添加满足条件的人
                personList1.add(persons[i]);
            } else {
                //查询屏蔽人的id
                personList2.add(persons[i]);
            }
        }
        //发送信息
        //给list1发送邀请信息
        for (int i = 0; i < personList1.size(); i++) {
            MsgStatus msgStatus = new MsgStatus();
            msgStatus.setType(1L);
            msgStatus.setUserId(Long.parseLong(userId));
            msgStatus.setReceiverId(Long.parseLong(personList1.get(i)));
            if (msgStatusMapper.addNewMsg(msgStatus) == 0) {
                logger.info("添加邀请事件时消息状态保存失败,id====>" + msgStatus.getId());
            }
            RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
            String date = singleEvent.getYear() + "/" + singleEvent.getMonth() + "/" + singleEvent.getDay();
            InviteMessage inviteMessage = new InviteMessage(singleEvent.getEventname(), date, JSON.toJSONString(SingleEventUtil.getShowSingleEvent1(singleEvent)), "2", msgStatus.getId().toString());
            ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(userId, new String[]{personList1.get(i)}, 0, inviteMessage);
            if (result.getCode() != 200) {
                logger.info("添加邀请事件时融云消息异常" + result.toString());
            }
        }
        //list2给创建者发送拒绝信息
        for (int i = 0; i < personList2.size(); i++) {
            RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
            TxtMessage txtMessage = new TxtMessage("我拒绝了你的事件：“" + singleEvent.getEventname() + "”的邀请", "");
            ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(personList2.get(i), new String[]{userId}, 0, txtMessage);
            if (result.getCode() != 200) {
                logger.info("新增邀请事件回应邀请时融云消息异常：" + result.toString());
            }
            msgStatusMapper.addNewEventMsg(userId, singleEvent.getEventid(), personList2.get(i), "拒绝了您的事件邀请", System.currentTimeMillis() / 1000);
        }
    }

    /**
     * 上传草稿为普通事件
     *
     * @param draftToEventVo
     * @param token
     * @return
     */
    @Override
    public synchronized Dto draftToSingleEvent(DraftToEventVo draftToEventVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(draftToEventVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        //解析草稿数据
        SingleEventAndBacklog singleEvent = JSONObject.parseObject(draftToEventVo.getDraft(), SingleEventAndBacklog.class);
        //这里开始判断是否是一个重复事件,如果状态值为真,则该事件为重复事件
        singleEvent.setIsLoop(SingleEventUtil.isLoopEvent(singleEvent.getRepeaTtime()) ? 1 : 0);
        if (singleEvent.getIsLoop() == 1) {
            List<SingleEvent> loopEventList = eventMapper.queryClashLoopEventList(singleEvent);
            if (!SingleEventUtil.loopEventTime(loopEventList, singleEvent)) {
                return DtoUtil.getFalseDto("时间段冲突,无法上传", 21012);
            }
        } else if (!SingleEventUtil.eventTime(eventMapper.queryClashEventList(singleEvent), Long.valueOf(singleEvent.getStarttime()), Long.valueOf(singleEvent.getEndtime()))) {
            return DtoUtil.getFalseDto("时间段冲突,无法上传", 21012);
        }
        //添加至事件表
        if (eventMapper.uploadingEvents(singleEvent) <= 0) {
            return DtoUtil.getFalseDto("保存失败", 21111);
        }
        //加入事件清单
        if (!ObjectUtils.isEmpty(singleEvent.getBacklogList()) && singleEvent.getBacklogList().size()>0){
            List<BacklogList> backlogLists=new ArrayList<>();
            for (BacklogList backlogList:singleEvent.getBacklogList()) {
                backlogList.setSingleEventId(singleEvent.getId());
                backlogLists.add(backlogList);
            }
            backlogMapper.insertBacklog(backlogLists);
        }
        //查询数据库是否有该草稿
        SingleEvent draft=eventMapper.queryDraftOne(draftToEventVo.getUserId(), singleEvent.getEventid().toString());
        if (!ObjectUtils.isEmpty(draft)) {
            //删除草稿箱事件
            if (eventMapper.deleteDraft(draftToEventVo.getUserId(), singleEvent.getEventid().toString()) <= 0) {
                return DtoUtil.getFalseDto("保存失败", 21112);
            }
            //删除草稿清单
            backlogMapper.deleteDraftBacklogList(draft.getId());
        }
        return DtoUtil.getSuccessDto("保存成功", 100000);
    }

    /**
     * 上传草稿为邀请事件
     *
     * @param draftToEventVo
     * @param token
     * @return
     */
    @Override
    public synchronized Dto draftToInviteEvent(DraftToEventVo draftToEventVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(draftToEventVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        //解析草稿数据
        SingleEventAndBacklog singleEvent = JSONObject.parseObject(draftToEventVo.getDraft(), SingleEventAndBacklog.class);
        //这里开始判断是否是一个重复事件,如果状态值为真,则该事件为重复事件
        singleEvent.setIsLoop(SingleEventUtil.isLoopEvent(singleEvent.getRepeaTtime()) ? 1 : 0);
        if (singleEvent.getIsLoop() == 1) {
            List<SingleEvent> loopEventList = eventMapper.queryClashLoopEventList(singleEvent);
            if (!SingleEventUtil.loopEventTime(loopEventList, singleEvent)) {
                return DtoUtil.getFalseDto("时间段冲突,无法上传", 21012);
            }
        } else if (!SingleEventUtil.eventTime(eventMapper.queryClashEventList(singleEvent), Long.valueOf(singleEvent.getStarttime()), Long.valueOf(singleEvent.getEndtime()))) {
            return DtoUtil.getFalseDto("时间段冲突,无法上传", 21012);
        }
        EventPersons eventPersons = JSONObject.parseObject(singleEvent.getPerson(), EventPersons.class);
        String friendsId = eventPersons.getFriendsId();
        //添加至事件表
        eventPersons.setFriendsId("");
        singleEvent.setPerson(JSON.toJSONString(eventPersons));
        if (eventMapper.uploadingEvents(singleEvent) <= 0) {
            return DtoUtil.getFalseDto("保存失败", 21111);
        }
        //加入事件清单
        if (!ObjectUtils.isEmpty(singleEvent.getBacklogList()) && singleEvent.getBacklogList().size()>0){
            List<BacklogList> backlogLists=new ArrayList<>();
            for (BacklogList backlogList:singleEvent.getBacklogList()) {
                backlogList.setSingleEventId(singleEvent.getId());
                //判断同步权限
                if (!StringUtils.isEmpty(singleEvent.getIsSync()) && "1".equals(singleEvent.getIsSync())){
                    backlogList.setIsSync(1L);
                }
                backlogLists.add(backlogList);
            }
            backlogMapper.insertBacklog(backlogLists);
        }
        //在事件副表插入创建者
        SingleEventVice singleEventVice = new SingleEventVice();
        singleEventVice.setCreateBy(Long.parseLong(draftToEventVo.getUserId()));
        singleEventVice.setUserId(singleEvent.getUserid());
        singleEventVice.setEventId(singleEvent.getEventid());
        eventViceMapper.createEventVice(singleEventVice);
        //查询数据库是否有该草稿
        SingleEvent draft=eventMapper.queryDraftOne(draftToEventVo.getUserId(), singleEvent.getEventid().toString());
        if (!ObjectUtils.isEmpty(draft)) {
            //删除草稿箱事件
            if (eventMapper.deleteDraft(draftToEventVo.getUserId(), singleEvent.getEventid().toString()) <= 0) {
                return DtoUtil.getFalseDto("保存失败", 21112);
            }
            //删除草稿清单
            backlogMapper.deleteDraftBacklogList(draft.getId());
        }
        //发送邀请信息
        try {
            if (!StringUtils.isEmpty(friendsId)) {
                logger.info("参与者Id:" + friendsId);
                String[] persons = friendsId.split(",");
                //要发送信息的人员
                ArrayList<String> personList1 = new ArrayList<>();
                //屏蔽的人员
                ArrayList<String> personList2 = new ArrayList<>();
                //判断好友是否开启了邀请权限
                for (int i = 0; i < persons.length; i++) {
                    Friendship friendship = accountMapper.queryFriendshipDetail(persons[i], draftToEventVo.getUserId());
                    UserSettings settings = userSettingsMapper.queryAllSettings(persons[i]);
                    if (friendship.getInvite() == 1 && settings.getFriendInvite() == 0) {
                        //只添加满足条件的人
                        personList1.add(persons[i]);
                    } else {
                        //查询屏蔽人的id
                        personList2.add(persons[i]);
                    }
                }
                //给list1发送邀请信息
                for (int i = 0; i < personList1.size(); i++) {
                    MsgStatus msgStatus = new MsgStatus();
                    msgStatus.setType(1L);
                    msgStatus.setUserId(Long.parseLong(draftToEventVo.getUserId()));
                    msgStatus.setReceiverId(Long.parseLong(personList1.get(i)));
                    if (msgStatusMapper.addNewMsg(msgStatus) == 0) {
                        logger.info("添加邀请事件时消息状态保存失败,id====>" + msgStatus.getId());
                    }
                    RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                    String date = singleEvent.getYear() + "/" + singleEvent.getMonth() + "/" + singleEvent.getDay();
                    InviteMessage inviteMessage = new InviteMessage(singleEvent.getEventname(), date, JSON.toJSONString(SingleEventUtil.getShowSingleEvent(singleEvent)), "2", msgStatus.getId().toString());
                    logger.info(JSON.toJSONString(SingleEventUtil.getShowSingleEvent(singleEvent)));
                    ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(draftToEventVo.getUserId(), new String[]{personList1.get(i)}, 0, inviteMessage);
                    logger.info("邀请融云消息:" + result.toString());
                    if (result.getCode() != 200) {
                        return DtoUtil.getFalseDto("消息发送失败", 21040);
                    }
                    msgStatusMapper.addNewEventMsg(personList1.get(i), singleEvent.getEventid(), draftToEventVo.getUserId(), "邀请你参与事件", System.currentTimeMillis() / 1000);
                }
                //list2给创建者发送拒绝信息
                for (int i = 0; i < personList2.size(); i++) {
                    RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                    TxtMessage txtMessage = new TxtMessage("我拒绝了你的事件：“" + singleEvent.getEventname() + "”的邀请", "");
                    ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(personList2.get(i), new String[]{draftToEventVo.getUserId()}, 0, txtMessage);
                    logger.info("拒绝融云消息：" + result.toString());
                    if (result.getCode() != 200) {
                        return DtoUtil.getFalseDto("消息发送失败", 21040);
                    }
                    msgStatusMapper.addNewEventMsg(draftToEventVo.getUserId(), singleEvent.getEventid(), personList2.get(i), "拒绝了你的事件", System.currentTimeMillis() / 1000);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return DtoUtil.getSuccessDto("保存成功", 100000);
    }

    @Override
    public Dto getAllDrafts(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        List<SingleEvent> singleEventList = eventMapper.queryAllDrafts(receivedId.getUserId());
        if (singleEventList.size() != 0) {
            return DtoUtil.getSuccesWithDataDto("查询成功", SingleEventUtil.getShowSingleEventList(singleEventList), 100000);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功", null, 100000);
    }

    @Override
    public Dto searchByWeekForIos(SeaByWeekForIOS seaByWeekForIOS, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(seaByWeekForIOS.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        ReceivedId receivedId = new ReceivedId();
        if ("seaF".equals(seaByWeekForIOS.getSeaType())){
            receivedId.setUserId(seaByWeekForIOS.getFriendId());
        }else {
            receivedId.setUserId(seaByWeekForIOS.getUserId());
        }
        List<String> eventIdsList = new ArrayList<>();
        for (int i = 0; i <= 6; i++) {
            String currentDay = DateUtil.getDay(i);
            StringBuffer stringBuffer = new StringBuffer(currentDay);
            StringBuffer eventIds = new StringBuffer("1");
            int currentWeek = DateUtil.stringToWeek(currentDay);
            currentWeek = currentWeek == 7 ? 0 : currentWeek;
                    NaturalWeek naturalWeek = new NaturalWeek();
            naturalWeek.setUserId(receivedId.getUserId());
            naturalWeek.setYear(stringBuffer.substring(0, 4));
            naturalWeek.setMonth(stringBuffer.substring(4, 6));
            naturalWeek.setDay(stringBuffer.substring(6));
            List<SingleEvent> singleList = eventMapper.getTodayEvents(naturalWeek);
            List<SingleEvent> loopList = eventMapper.getAllLoopEvents(receivedId.getUserId());
            for (SingleEvent loop : loopList) {
                Boolean[] repeatTime = SingleEventUtil.getRepeatTime(loop);
                if (repeatTime[currentWeek]) {
                    if (!SingleEventUtil.eventTime(singleList,Long.valueOf(loop.getStarttime()), Long.valueOf(loop.getEndtime()))){
                        eventIds.append(",").append(loop.getEventid());
                    }
                }
            }
            for (SingleEvent singleEvent : singleList){
                eventIds.append(",").append(singleEvent.getEventid());
            }
            eventIdsList.add(eventIds.toString());
        }
        List<List<ShowSingleEvent>> result = new ArrayList<>();
        for (String s : eventIdsList) {
            List<ShowSingleEvent> singleEventList = new ArrayList<>();
            for (String s1 : s.split(",")) {
                SingleEvent singleEvent = eventMapper.getAEvent(receivedId.getUserId(),Long.valueOf(s1),"singleevent");
                if (ObjectUtils.isEmpty(singleEvent)){
                    continue;
                }
                singleEventList.add(SingleEventUtil.getShowSingleEvent1(singleEvent));
            }
            result.add(singleEventList);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",result,100000);
    }

    @Override
    public Dto earlyEventTermination(EarlyEventTermination earlyEventTermination, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(earlyEventTermination.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        SingleEvent event = eventMapper.getEarlyEventTermination(earlyEventTermination.getEventId(),earlyEventTermination.getUserId());
        if (ObjectUtils.isEmpty(event)){
            return DtoUtil.getFalseDto("当前事件无法提前完成",22001);
        }
        if (Long.valueOf(event.getStarttime()) > DateUtil.getCurrentMinutes()){
            return DtoUtil.getFalseDto("当前事件未开始,无法提前完成",22003);
        }
        if (event.getIsLoop() == 0){
            if (eventMapper.earlySingleEventTermination(earlyEventTermination.getEventId(),earlyEventTermination.getUserId(),DateUtil.getCurrentMinutes()) <= 0){
                return DtoUtil.getFalseDto("提前完成失败",22002);
            }
        }else {
            int week = DateUtil.stringToWeek(null);
            week = week == 7 ? 0 : week;
            if (!SingleEventUtil.getRepeatTime(event)[week]){
                return DtoUtil.getFalseDto("当前事件未开始,无法提前完成",22003);
            }
            event.setYear(Long.valueOf(DateUtil.getCurrentYear()));
            event.setMonth(Long.valueOf(DateUtil.getCurrentMonth()));
            event.setDay(Long.valueOf(DateUtil.getCurrentDay()));
            event.setEventid(System.currentTimeMillis() / 1000);
            event.setIsOverdue(3L);
            event.setFlag(5L);
            event.setIsLoop(0);
            event.setRepeaTtime("[false,false,false,false,false,false,false]");
            if (eventMapper.uploadingEvents1(event) <= 0){
                return DtoUtil.getFalseDto("提前完成失败",22002);
            }
        }
        return DtoUtil.getSuccessDto("操作成功",100000);
    }

    /**
     * 修改事件清单状态
     * @param backlogListVo
     * @return
     */
    @Override
    public Dto updateBacklogList(BacklogListVo backlogListVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(backlogListVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        BacklogList backlogList1=backlogMapper.queryBacklogListById(Long.valueOf(backlogListVo.getId()));
        if (ObjectUtils.isEmpty(backlogList1)){
            return DtoUtil.getFalseDto("清单不存在，可能已被删除",200000);
        }
        //判断同步权限是否开启
        if (backlogList1.getIsSync() == 1L){
            //查询我的清单列表
            List<BacklogList> backlogListsMe=backlogMapper.queryBacklogList(backlogList1.getSingleEventId());
            //查询该清单是第几个元素
            int index=-1;
            for (int i = 0; i < backlogListsMe.size(); i++) {
                if (backlogListsMe.get(i).getId().equals(backlogList1.getId())){
                    index=i;
                }
            }
            //修改其他成员
            SingleEvent singleEvent=eventMapper.queryEventBySingleEventId(backlogList1.getSingleEventId());
            //查询其他成员
            EventPersons eventPersons=JSONObject.parseObject(singleEvent.getPerson(),EventPersons.class);
            String[] friendsId=eventPersons.getFriendsId().split(",");
            for (String userId:friendsId) {
                //查询其他成员的事件
                SingleEvent friendEvent=eventMapper.queryEventOne(userId,singleEvent.getEventid().toString());
                //查询其他成员的清单
                List<BacklogList> backlogLists=backlogMapper.queryBacklogList(friendEvent.getId());
                //修改其他成员的清单状态
                BacklogList friendBacklogList=backlogLists.get(index);
                friendBacklogList.setBacklogStatus(Long.valueOf(backlogListVo.getBacklogStatus()));
                if ("1".equals(backlogListVo.getBacklogStatus())){
                    friendBacklogList.setFinishTime(System.currentTimeMillis()/1000);
                }else {
                    friendBacklogList.setFinishTime(-1L);
                }
                backlogMapper.updateBacklog(friendBacklogList);
            }
        }
        BacklogList backlogList=new BacklogList();
        backlogList.setBacklogStatus(Long.valueOf(backlogListVo.getBacklogStatus()));
        backlogList.setId(Long.valueOf(backlogListVo.getId()));
        if ("1".equals(backlogListVo.getBacklogStatus())){
            backlogList.setFinishTime(System.currentTimeMillis()/1000);
        }else {
            backlogList.setFinishTime(-1L);
        }
        if (backlogMapper.updateBacklog(backlogList)<1){
            return DtoUtil.getFalseDto("修改失败",22004);
        }
        return DtoUtil.getSuccessDto("修改成功",100000);
    }

    /**
     * 修改事件清单内容
     * @param backlogListVo
     * @return
     */
    @Override
    public Dto updateBacklogListDetail(BacklogListVo backlogListVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(backlogListVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        BacklogList backlogList1=backlogMapper.queryBacklogListById(Long.valueOf(backlogListVo.getId()));
        if (ObjectUtils.isEmpty(backlogList1)){
            return DtoUtil.getFalseDto("清单不存在，可能已被删除",200000);
        }
        //判断同步权限是否开启
        if (backlogList1.getIsSync() == 1L){
            //查询我的清单列表
            List<BacklogList> backlogListsMe=backlogMapper.queryBacklogList(backlogList1.getSingleEventId());
            //查询该清单是第几个元素
            int index=-1;
            for (int i = 0; i < backlogListsMe.size(); i++) {
                if (backlogListsMe.get(i).getId().equals(backlogList1.getId())){
                    index=i;
                }
            }
            //修改其他成员
            SingleEvent singleEvent=eventMapper.queryEventBySingleEventId(backlogList1.getSingleEventId());
            //查询是否是创建者
            SingleEventVice singleEventVice=new SingleEventVice();
            singleEventVice.setEventId(singleEvent.getEventid());
            singleEventVice.setUserId(singleEvent.getUserid());
            if (singleEvent.getUserid().equals(eventViceMapper.queryEventVice(singleEventVice).getCreateBy())){
                //查询其他成员
                EventPersons eventPersons=JSONObject.parseObject(singleEvent.getPerson(),EventPersons.class);
                String[] friendsId=eventPersons.getFriendsId().split(",");
                for (String userId:friendsId) {
                    //查询其他成员的事件
                    SingleEvent friendEvent=eventMapper.queryEventOne(userId,singleEvent.getEventid().toString());
                    //查询其他成员的清单
                    List<BacklogList> backlogLists=backlogMapper.queryBacklogList(friendEvent.getId());
                    BacklogList friendBacklogList=backlogLists.get(index);
                    //修改其他成员的清单内容
                    friendBacklogList.setBacklogName(backlogListVo.getBacklogName());
                    backlogMapper.updateBacklog(friendBacklogList);
                }
            }else {
                return DtoUtil.getFalseDto("事件发起者已开启权限，您没有权限",22104);
            }
        }
        BacklogList backlogList=new BacklogList();
        backlogList.setBacklogName(backlogListVo.getBacklogName());
        backlogList.setId(Long.valueOf(backlogListVo.getId()));
        if (backlogMapper.updateBacklog(backlogList)<1){
            return DtoUtil.getFalseDto("修改失败",22004);
        }
        return DtoUtil.getSuccessDto("修改成功",100000);
    }

    /**
     * 添加事件清单
     * @param backlogListVo
     * @return
     */
    @Override
    public Dto addBacklogList(BacklogListVo backlogListVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(backlogListVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        SingleEvent singleEvent1=eventMapper.queryEventOne(backlogListVo.getUserId(),backlogListVo.getEventId());
        List<BacklogList> backlogList2=backlogMapper.queryBacklogList(singleEvent1.getId());
        BacklogList backlogList1=null;
        if (backlogList2.size()>0){
            backlogList1=backlogList2.get(0);
        //判断同步权限是否开启
            if (backlogList1.getIsSync() == 1L){
                //修改其他成员
                SingleEvent singleEvent=eventMapper.queryEventBySingleEventId(backlogList1.getSingleEventId());
                //查询是否是创建者
                SingleEventVice singleEventVice=new SingleEventVice();
                singleEventVice.setEventId(singleEvent.getEventid());
                singleEventVice.setUserId(singleEvent.getUserid());
                if (singleEvent.getUserid().equals(eventViceMapper.queryEventVice(singleEventVice).getCreateBy())){
                    //查询其他成员
                    EventPersons eventPersons=JSONObject.parseObject(singleEvent.getPerson(),EventPersons.class);
                    String[] friendsId=eventPersons.getFriendsId().split(",");
                    for (String userId:friendsId) {
                        //查询其他成员的事件
                        SingleEvent friendEvent=eventMapper.queryEventOne(userId,singleEvent.getEventid().toString());
                        //添加其他成员的清单
                        BacklogList backlogList=new BacklogList();
                        backlogList.setSingleEventId(friendEvent.getId());
                        backlogList.setBacklogName(backlogListVo.getBacklogName());
                        backlogList.setIsSync(backlogList1.getIsSync());
                        backlogMapper.addBacklog(backlogList);
                    }
                }else {
                    return DtoUtil.getFalseDto("事件发起者已开启权限，您没有权限",22104);
                }
            }
        }
        BacklogList backlogList=new BacklogList();
        backlogList.setSingleEventId(singleEvent1.getId());
        backlogList.setBacklogName(backlogListVo.getBacklogName());
        backlogList.setIsSync(ObjectUtils.isEmpty(backlogList1) ? 0 : backlogList1.getIsSync());
        if (backlogMapper.addBacklog(backlogList)<1){
            return DtoUtil.getFalseDto("添加清单失败",21009);
        }
        return DtoUtil.getSuccessDto("添加成功",100000);
    }

    /**
     * 删除事件清单
     *
     * @param backlogListVo
     * @return
     */
    @Override
    public Dto deleteBacklogList(BacklogListVo backlogListVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(backlogListVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        BacklogList backlogList1=backlogMapper.queryBacklogListById(Long.valueOf(backlogListVo.getId()));
        if (ObjectUtils.isEmpty(backlogList1)){
            return DtoUtil.getFalseDto("清单不存在，可能已被删除",200000);
        }
        //判断同步权限是否开启
        if (backlogList1.getIsSync() == 1L){
            //查询我的清单列表
            List<BacklogList> backlogListsMe=backlogMapper.queryBacklogList(backlogList1.getSingleEventId());
            //查询该清单是第几个元素
            int index=-1;
            for (int i = 0; i < backlogListsMe.size(); i++) {
                if (backlogListsMe.get(i).getId().equals(backlogList1.getId())){
                    index=i;
                }
            }
            //修改其他成员
            SingleEvent singleEvent=eventMapper.queryEventBySingleEventId(backlogList1.getSingleEventId());
            //查询是否是创建者
            SingleEventVice singleEventVice=new SingleEventVice();
            singleEventVice.setEventId(singleEvent.getEventid());
            singleEventVice.setUserId(singleEvent.getUserid());
            if (singleEvent.getUserid().equals(eventViceMapper.queryEventVice(singleEventVice).getCreateBy())){
                //查询其他成员
                EventPersons eventPersons=JSONObject.parseObject(singleEvent.getPerson(),EventPersons.class);
                String[] friendsId=eventPersons.getFriendsId().split(",");
                for (String userId:friendsId) {
                    //查询其他成员的事件
                    SingleEvent friendEvent=eventMapper.queryEventOne(userId,singleEvent.getEventid().toString());
                    //查询其他成员的清单
                    List<BacklogList> backlogLists=backlogMapper.queryBacklogList(friendEvent.getId());
                    BacklogList friendBacklogList=backlogLists.get(index);
                    //删除其他成员的清单
                    backlogMapper.deleteBacklog(friendBacklogList.getId());
                }
            }else {
                return DtoUtil.getFalseDto("事件发起者已开启权限，您没有权限",22104);
            }
        }
        if (backlogMapper.deleteBacklog(Long.valueOf(backlogListVo.getId()))<1){
            return DtoUtil.getFalseDto("删除清单失败",21010);
        }
        return DtoUtil.getSuccessDto("删除成功",100000);
    }

    /**
     * 将传进来的事件集合中的已完成的重复事件(冲突的)移除
     *
     * @param singleEventList
     * @return
     */
    private List<SingleEvent> completedLoopEvent(List<SingleEvent> singleEventList) {
        if (singleEventList.size() == 0) {
            return singleEventList;
        }
        List<SingleEvent> ssevent = new ArrayList<>(singleEventList);
        Iterator<SingleEvent> iterator = ssevent.iterator();
        while (iterator.hasNext()) {
            SingleEvent singleEvent1 = iterator.next();
            if (ObjectUtils.isEmpty(singleEvent1.getFlag())){
                continue;
            }
            if (singleEvent1.getFlag() == 5) {
                iterator.remove();
                if (!SingleEventUtil.eventTime(ssevent, Long.valueOf(singleEvent1.getStarttime()), Long.valueOf(singleEvent1.getEndtime()))) {
                    singleEventList.remove(singleEvent1);
                }
            }
        }
        return singleEventList;
    }

    /**
     * 如果普通事件集合中有flag为5的事件,
     * 并且该事件与该事件的父事件(重复事件)产生时间冲突,
     * 则将该子事件从普通事件集合中移除
     *
     * @param dayEventsList
     * @param loopEventList
     * @param dayEventId
     */
    private void removeFlag5ClashSingleEvent(List<DayEvents> dayEventsList, List<List<ShowSingleEvent>> loopEventList, String dayEventId) {
        ArrayList<ShowSingleEvent> singleEventList = dayEventsList.get(0).getMySingleEventList();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int week = DateUtil.stringToWeek(dayEventId);
        calendar.get(Calendar.DAY_OF_WEEK);
        week = week == 7 ? 0 : week;
        for (Iterator<ShowSingleEvent> iterator = singleEventList.iterator(); iterator.hasNext(); ) {
            ShowSingleEvent singleEvent1 = iterator.next();
            if (ObjectUtils.isEmpty(singleEvent1.getFlag())){
                continue;
            }
            for (ShowSingleEvent showSingleEvent : loopEventList.get(week)) {
                logger.info(singleEvent1.toString());
                if (singleEvent1.getFlag() == 5) {
                    if (SingleEventUtil.getClashTime(singleEvent1.getStarttime(), singleEvent1.getEndtime(), showSingleEvent.getStarttime(), showSingleEvent.getEndtime())) {
                        try {
                            iterator.remove();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}

