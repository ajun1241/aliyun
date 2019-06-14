package com.modcreater.tmbiz.service.impl;

import com.alibaba.fastjson.JSON;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.dto.EventPersons;
import com.modcreater.tmbeans.pojo.*;
import com.modcreater.tmbeans.show.ShowSingleEvent;
import com.modcreater.tmbeans.vo.QueryMsgStatusVo;
import com.modcreater.tmbeans.vo.eventvo.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedDeleteEventIds;
import com.modcreater.tmbiz.service.EventService;
import com.modcreater.tmbiz.config.EventUtil;
import com.modcreater.tmdao.mapper.*;
import com.modcreater.tmutils.*;
import com.modcreater.tmutils.messageutil.FeedbackInviteMessage;
import com.modcreater.tmutils.messageutil.InviteMessage;
import com.modcreater.tmutils.messageutil.UpdateInviteMessage;
import io.rong.messages.TxtMessage;
import io.rong.models.response.ResponseResult;
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

    @Resource
    private EventMapper eventMapper;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private StatisticsMapper statisticsMapper;

    @Resource
    private AchievementMapper achievementMapper;

    @Resource
    private UserSettingsMapper userSettingsMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private EventViceMapper eventViceMapper;

    @Resource
    private BackerMapper backerMapper;

    @Resource
    private TimedTaskMapper timedTaskMapper;

    @Resource
    private UserServiceMapper userServiceMapper;

    @Resource
    private MsgStatusMapper msgStatusMapper;

    @Resource
    private EventUtil eventUtil;

    @Override
    public Dto addNewEvents(UploadingEventVo uploadingEventVo, String token) {
        if (!StringUtils.hasText(uploadingEventVo.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("操作失败,token未获取到", 21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(uploadingEventVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!StringUtils.hasText(uploadingEventVo.getSingleEvent())) {
            return DtoUtil.getFalseDto("没有可上传的事件", 21002);
        }
        SingleEvent singleEvent = JSONObject.parseObject(uploadingEventVo.getSingleEvent(), SingleEvent.class);
        singleEvent.setUserid(Long.valueOf(uploadingEventVo.getUserId()));
        //这里开始判断是否是一个重复事件,如果状态值为真,则该事件为重复事件
        singleEvent.setIsLoop(SingleEventUtil.isLoopEvent(singleEvent.getRepeaTtime()) ? 1 : 0);
        if (!SingleEventUtil.eventTime(eventMapper.queryClashEventList(singleEvent), Long.valueOf(singleEvent.getStarttime()), Long.valueOf(singleEvent.getEndtime()))) {
            return DtoUtil.getFalseDto("时间段冲突,无法修改", 21012);
        }
        if (!ObjectUtils.isEmpty(singleEvent) && eventMapper.uploadingEvents(singleEvent) > 0) {
            //未完成+1
            achievementMapper.updateUserStatistics(changeUnfinished(new UserStatistics(), 1L), uploadingEventVo.getUserId());
            return DtoUtil.getSuccessDto("事件上传成功", 100000);
        }
        return DtoUtil.getFalseDto("事件上传失败", 21001);
    }

    @Override
    public Dto deleteEvents(DeleteEventVo deleteEventVo, String token) {
        if (!StringUtils.hasText(deleteEventVo.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("操作失败,token未获取到", 21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(deleteEventVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!ObjectUtils.isEmpty(eventMapper.getChangingEventStatus(deleteEventVo))){
            return DtoUtil.getFalseDto("重复操作:已经操作过了",21003);
        }
        System.out.println(deleteEventVo.getUserId()+"操作删除");
        if (eventMapper.withdrawEventsByUserId(deleteEventVo) > 0) {
            if (deleteEventVo.getEventStatus().equals("1")) {
                //已完成+1,未完成-1
                achievementMapper.updateUserStatistics(changeUnfinished(changeCompleted(new UserStatistics(), 1L), -1L), deleteEventVo.getUserId());
            } else if (deleteEventVo.getEventStatus().equals("2")) {
                //未完成-1
                achievementMapper.updateUserStatistics(changeUnfinished(new UserStatistics(), -1L), deleteEventVo.getUserId());
            }
            return DtoUtil.getSuccessDto("修改事件状态成功", 100000);
        }
        return DtoUtil.getFalseDto("修改事件状态失败", 21005);
    }


    @Override
    public Dto updateEvents(UpdateEventVo updateEventVo, String token) {
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
        SingleEvent singleEvent = JSONObject.parseObject(updateEventVo.getSingleEvent(), SingleEvent.class);
        singleEvent.setUserid(Long.valueOf(updateEventVo.getUserId()));
        SingleEvent result = eventMapper.querySingleEventTime(singleEvent);
        if (!(singleEvent.getStarttime().equals(result.getStarttime()) && singleEvent.getEndtime().equals(result.getEndtime()))) {
            if (!SingleEventUtil.eventTime(eventMapper.queryClashEventList(singleEvent), Long.valueOf(singleEvent.getStarttime()), Long.valueOf(singleEvent.getEndtime()))) {
                return DtoUtil.getFalseDto("时间段冲突,无法修改", 21012);
            }
        }
        //这里开始判断是否是一个重复事件,如果状态值为真,则该事件为重复事件
        singleEvent.setIsLoop(SingleEventUtil.isLoopEvent(singleEvent.getRepeaTtime()) ? 1 : 0);
        if (eventMapper.alterEventsByUserId(singleEvent) > 0) {
            return DtoUtil.getSuccessDto("修改成功", 100000);
        }
        return DtoUtil.getFalseDto("修改事件失败", 21007);
    }

    @Override
    public Dto firstUplEvent(SynchronousUpdateVo synchronousUpdateVo, String token) {
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
                    SingleEvent singleEvent1 = JSONObject.parseObject(singleEvent.toString(), SingleEvent.class);
                    //插入用户id
                    singleEvent1.setUserid(Long.parseLong(synchronousUpdateVo.getUserId()));
                    singleEvent1.setIsLoop(0);
                    //上传
                    if (eventMapper.uploadingEvents(singleEvent1) <= 0) {
                        return DtoUtil.getFalseDto("上传事件" + singleEvent1.getEventid() + "失败", 25005);
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
                //第二层转换
                List<SingleEvent> singleEventList = JSONObject.parseObject(singleEvents.toString(), ArrayList.class);
                for (Object loopEvent : singleEventList) {
                    //第三层转换
                    SingleEvent singleEvent = JSONObject.parseObject(loopEvent.toString(), SingleEvent.class);
                    singleEvent.setUserid(Long.parseLong(synchronousUpdateVo.getUserId()));
                    singleEvent.setIsLoop(1);
                    if (eventMapper.uploadingEvents(singleEvent) <= 0) {
                        return DtoUtil.getFalseDto("上传重复事件" + singleEvent.getEventid() + "失败", 25006);
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
    public Dto uplDraft(DraftVo draftVo, String token) {
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
            return DtoUtil.getFalseDto("请重新登录", 21013);
        }
        //判断是否已开通或者服务时间未消耗完
        ServiceRemainingTime time = userServiceMapper.getServiceRemainingTime(draftVo.getUserId(), "4");
        //用户未开通
        if (ObjectUtils.isEmpty(time)) {
            return DtoUtil.getSuccessDto("该用户尚未开通备份功能", 20000);
        }
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
            //如果剩余次数为0,判断库存时间是否为0
            if (time.getResidueDegree() == 0 && time.getStorageTime() != 0) {
                //如果有库存时间,将这个时间加入用户有效的剩余时间中
                time.setTimeRemaining(System.currentTimeMillis() / 1000 + time.getStorageTime());
                time.setStorageTime(0L);
            }
        }
        ArrayList<Object> drafts = JSONObject.parseObject(draftVo.getSingleEvents(), ArrayList.class);
        for (Object draft : drafts) {
            System.out.println(draft);
            SingleEvent draft1 = JSONObject.parseObject(draft.toString(), SingleEvent.class);
            //查看草稿是否已存在
            if (eventMapper.queryDraftCount(draftVo.getUserId(), draft1.getEventid().toString()) == 0) {
                //上传
                draft1.setUserid(Long.parseLong(draftVo.getUserId()));
                if (SingleEventUtil.isLoopEvent(draft1.getRepeaTtime())) {
                    draft1.setIsLoop(1);
                } else {
                    draft1.setIsLoop(0);
                }
                if (eventMapper.uplDraft(draft1) == 0) {
                    return DtoUtil.getFalseDto("上传草稿失败", 27002);
                }
            } else {
                return DtoUtil.getFalseDto("该草稿已存在", 27003);
            }
        }

        UserStatistics userStatistics = new UserStatistics();
        StringBuffer dataNum = new StringBuffer(draftVo.getSingleEvents());
        Long times = 0L;
        String condition = "eventid";
        for (int i = 0; i < dataNum.length(); i++) {
            if (dataNum.indexOf(condition, i) != -1) {
                i = dataNum.indexOf(condition, i);
                times++;
            }
        }
        userStatistics.setUserId(Long.valueOf(draftVo.getUserId()));
        userStatistics.setDrafts(times);
        if (achievementMapper.updateUserStatistics(userStatistics, userStatistics.getUserId().toString()) == 0) {
            return DtoUtil.getFalseDto("草稿箱数据计数失败", 27004);
        }
        //修改用户服务剩余时间
        if (userServiceMapper.updateServiceRemainingTime(time) == 0) {
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getFalseDto("修改用户服务剩余时间失败", 27004);
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
    public Dto updDraft(AddInviteEventVo addInviteEventVo, String token) {
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
            return DtoUtil.getFalseDto("请重新登录", 21013);
        }
        SingleEvent singleEvent = JSONObject.parseObject(addInviteEventVo.getSingleEvent(), SingleEvent.class);
        System.out.println("修改草稿:" + singleEvent.toString());
        if (ObjectUtils.isEmpty(singleEvent)) {
            return DtoUtil.getFalseDto("获取草稿失败", 21111);
        }
        if (eventMapper.updateDraft(singleEvent) == 0) {
            return DtoUtil.getFalseDto("修改草稿失败", 21112);
        }
        return DtoUtil.getSuccessDto("修改草稿成功", 10000);
    }


    @Override
    public Dto searchByDayEventIds(SearchEventVo searchEventVo, String token) {
        if (!StringUtils.hasText(searchEventVo.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("操作失败,token未获取到", 21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(searchEventVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!StringUtils.hasText(searchEventVo.getDayEventId())) {
            return DtoUtil.getFalseDto("查询条件接收失败", 21004);
        }
        //拆分dayEventId并将查询条件逐一添加到对象中
        SingleEvent singleEvent = SingleEventUtil.getSingleEvent(searchEventVo.getUserId(), searchEventVo.getDayEventId());
        //只根据level升序
        List<SingleEvent> singleEventListOrderByLevel = eventMapper.queryByDayOrderByLevel(singleEvent);
        List<ShowSingleEvent> showSingleEventListOrderByLevel = new ArrayList<>();
        //根据level和开始时间升序
        List<SingleEvent> singleEventListOrderByLevelAndDate = eventMapper.queryByDayOrderByLevelAndDate(singleEvent);
        List<ShowSingleEvent> showSingleEventListOrderByLevelAndDate = new ArrayList<>();
        //添加一个未排序的结果集到dayEvents中
        DayEvents<ShowSingleEvent> dayEvents = new DayEvents<>();
        ArrayList<SingleEvent> singleEventList = eventMapper.queryEvents(singleEvent);
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
        //根据用户ID查询重复事件
        List<SingleEvent> loopEventListInDataBase = eventMapper.queryLoopEvents(searchEventVo.getUserId());
        //判断上一条查询结果是否有数据
        if (loopEventListInDataBase.size() != 0) {
            //遍历集合并将符合repeatTime = 星期 的对象分别添加到集合中
            for (SingleEvent singleEvent1 : loopEventListInDataBase) {
                ShowSingleEvent showSingleEvent = SingleEventUtil.getShowSingleEvent(singleEvent1);
                if (showSingleEvent.getRepeaTtime()[week]) {
                    showSingleEventListOrderByLevel.add(showSingleEvent);
                    showSingleEventListOrderByLevelAndDate.add(showSingleEvent);
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
        if (!StringUtils.hasText(searchEventVo.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("操作失败,token未获取到", 21013);
        }
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
            ArrayList<ShowSingleEvent> showSingleEventList = (ArrayList<ShowSingleEvent>) SingleEventUtil.getShowSingleEventList(singleEventList);
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
        List<DayEvents> dayEventsList = getDayEventsList(searchEventVo.getUserId(),"all",searchEventVo.getDayEventId());
        //按周查询重复事件
        List<SingleEvent> loopEventListInDataBase = eventMapper.queryLoopEvents(searchEventVo.getUserId());
        List<List<ShowSingleEvent>> loopEventList = getShowSingleEventListList(loopEventListInDataBase);
        if ((dayEventsList.size() + loopEventList.size()) == 0) {
            return DtoUtil.getSuccessDto("没有数据", 200000);
        }
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
        //总权限 + 单一权限(这里的逻辑为在mybatis中userId和friendId值相反)
        try {
            if (userSettingsMapper.getFriendHide(searchEventVo.getFriendId()) == 0 && userSettingsMapper.getIsHideFromFriend(searchEventVo.getUserId(), searchEventVo.getFriendId()) == 1) {
                result.put("userPrivatePermission", "1");
            } else if (userSettingsMapper.getFriendHide(searchEventVo.getFriendId()) == 0 || userSettingsMapper.getIsHideFromFriend(searchEventVo.getUserId(), searchEventVo.getFriendId()) == 2) {
                result.put("userPrivatePermission", "2");
            } else {
                result.put("userPrivatePermission", "0");
                return DtoUtil.getSuccesWithDataDto("该用户设置了查看权限", result, 100000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("查询权限报错", 23335);
        }
        List<List<ShowSingleEvent>> loopEventList = new ArrayList<>();
        List<DayEvents> dayEventsList = new ArrayList<>();
        if (result.get("userPrivatePermission").equals("1")) {
            //按周查询单一事件
            dayEventsList = getDayEventsList(searchEventVo.getUserId(),"all",searchEventVo.getDayEventId());
            //按周查询重复事件
            List<SingleEvent> loopEventListInDataBase = eventMapper.queryLoopEvents(searchEventVo.getFriendId());
            if (loopEventListInDataBase.size() != 0) {
                loopEventList = getShowSingleEventListList(loopEventListInDataBase);
            }
        } else if (result.get("userPrivatePermission").equals("2")) {
            //按周查询单一事件
            dayEventsList = getDayEventsList(searchEventVo.getUserId(),"few",searchEventVo.getDayEventId());
            //按周查询重复事件
            List<SingleEvent> loopEventListInDataBase = eventMapper.queryLoopEventsWithFewInfo(searchEventVo.getFriendId());
            if (loopEventListInDataBase.size() != 0) {
                loopEventList = getShowSingleEventListList(loopEventListInDataBase);
            }
        }
        if ((dayEventsList.size() + loopEventList.size()) == 0) {
            return DtoUtil.getSuccessDto("没有数据", 200000);
        }
        result.put("loopEventList", loopEventList);
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
            if (ObjectUtils.isEmpty(singleEvent)){
                return DtoUtil.getFalseDto("事件已过期或者不存在",200000);
            }
            Map<String,Object> maps = new HashMap<>();
            ArrayList<Map> list = new ArrayList<>();
            if (!StringUtils.isEmpty(singleEvent.getPerson())) {
                EventPersons eventPersons = JSONObject.parseObject(singleEvent.getPerson(), EventPersons.class);
                if (!StringUtils.isEmpty(eventPersons.getFriendsId())){
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
                maps.put("friendsId",list);
                maps.put("others",eventPersons.getOthers());
            }
            if (singleEvent != null) {
                singleEvent.setPerson(JSON.toJSONString(maps));
                System.out.println("显示一个事件详情时输出的"+singleEvent.getPerson());
                return DtoUtil.getSuccesWithDataDto("查询成功", SingleEventUtil.getShowSingleEvent(singleEvent), 100000);
            }
            return DtoUtil.getSuccessDto("未查询到事件", 200000);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("查询事件出错", 2333);
        }
    }

    /**
     * 添加事件支持者
     *
     * @param addbackerVo
     * @param token
     * @return
     */
    @Override
    public Dto addEventBacker(AddBackerVo addbackerVo, String token) {
        if (StringUtils.isEmpty(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        if (ObjectUtils.isEmpty(addbackerVo)) {
            return DtoUtil.getFalseDto("添加邀请事件数据未获取到", 26001);
        }
        if (StringUtils.isEmpty(addbackerVo.getUserId())) {
            return DtoUtil.getFalseDto("userId不能为空", 21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(addbackerVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21013);
        }
        if (StringUtils.isEmpty(addbackerVo.getFriendIds())) {
            return DtoUtil.getFalseDto("支持者不能空", 25002);
        }
        try {
            String[] backers = addbackerVo.getFriendIds().split(",");
            //判断好友是否开启了权限
            List<String> personList=new ArrayList<>();
            for (int i = 0; i < backers.length; i++) {
                Friendship friendship=accountMapper.queryFriendshipDetail(backers[i],addbackerVo.getUserId());
                if (friendship.getInvite()==1){
                    //只添加满足条件的人
                    personList.add(backers[i]);
                }
            }
            //添加事件进数据库
            SingleEvent singleEvent = JSONObject.parseObject(addbackerVo.getSingleEvent(), SingleEvent.class);
            singleEvent.setUserid(Long.parseLong(addbackerVo.getUserId()));
            //事件时间冲突判断
            if (!SingleEventUtil.eventTime(eventMapper.queryClashEventList(singleEvent), Long.valueOf(singleEvent.getStarttime()), Long.valueOf(singleEvent.getEndtime()))) {
                return DtoUtil.getFalseDto("时间段冲突,无法修改", 21012);
            }
            if (eventMapper.uploadingEvents(singleEvent) == 0) {
                return DtoUtil.getFalseDto("事件添加失败", 25001);
            }
            UserStatistics statistics = new UserStatistics();
            statistics.setUserId(Long.valueOf(addbackerVo.getUserId()));
            //用户新增一条事件,未完成+1
            statistics.setUnfinished(1L);
            achievementMapper.updateUserStatistics(statistics, addbackerVo.getUserId());
            //添加支持者状态
            backerMapper.addBackers(addbackerVo.getUserId(), (String[]) personList.toArray((new String[]{})), singleEvent.getEventid().toString());
            //发送信息给被邀请者
//            Account account=accountMapper.queryAccount(addbackerVo.getUserId());
            RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
            String date = singleEvent.getYear() + "/" + singleEvent.getMonth() + "/" + singleEvent.getDay();
            InviteMessage inviteMessage = new InviteMessage(singleEvent.getEventname(), date, singleEvent.toString(), "", "");
            ResponseResult result = rongCloudMethodUtil.sendSystemMessage(addbackerVo.getUserId(), (String[]) personList.toArray((new String[]{})), inviteMessage, "", "");
            if (result.getCode() != 200) {
                return DtoUtil.getFalseDto("发送消息失败", 17002);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("消息发送出错", 26002);
        }
        return DtoUtil.getSuccessDto("消息发送成功", 100000);
    }

    /**
     * 回应事件支持
     *
     * @return
     */
    @Override
    public Dto feedbackEventBacker(FeedbackEventBackerVo feedbackEventBackerVo, String token) {
        try {
            if (StringUtils.isEmpty(token)) {
                return DtoUtil.getFalseDto("token未获取到", 21013);
            }
            if (ObjectUtils.isEmpty(feedbackEventBackerVo)) {
                return DtoUtil.getFalseDto("添加邀请事件数据未获取到", 26001);
            }
            if (StringUtils.isEmpty(feedbackEventBackerVo.getUserId())) {
                return DtoUtil.getFalseDto("userId不能为空", 21011);
            }
            if (!token.equals(stringRedisTemplate.opsForValue().get(feedbackEventBackerVo.getUserId()))) {
                return DtoUtil.getFalseDto("请重新登录", 21013);
            }
            //拿到发起者的事件
            SingleEvent singleEvent = JSONObject.parseObject(feedbackEventBackerVo.getExtraData(), SingleEvent.class);
            Backers backers = new Backers();
            //判断事件是否过期
            SingleEvent singleEventOld = eventMapper.queryEventOne(singleEvent.getUserid().toString(), singleEvent.getEventid().toString());
            if (ObjectUtils.isEmpty(singleEventOld)) {
                return DtoUtil.getFalseDto("该事件已过期", 29003);
            }
            //不能重复回应
            Backers backers1 = backerMapper.queryBackerDetail(singleEvent.getUserid().toString(), singleEvent.getEventid().toString(), feedbackEventBackerVo.getUserId());
            if (backers1.getStatus() != 0) {
                return DtoUtil.getFalseDto("你已经回复过了", 29004);
            }
            //同意
            if (Long.parseLong(feedbackEventBackerVo.getChoose()) == 1) {
                //更改backer表状态
                backers.setUserId(singleEvent.getUserid());
                backers.setEventId(singleEvent.getEventid());
                backers.setBackerId(Long.parseLong(feedbackEventBackerVo.getUserId()));
                backers.setStatus(1L);
                if (backerMapper.updateBackerStatus(backers) == 0) {
                    return DtoUtil.getFalseDto("回应状态修改失败", 29001);
                }
                //发送消息给发起者
                RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                Account account = accountMapper.queryAccount(feedbackEventBackerVo.getUserId());
                if (ObjectUtils.isEmpty(account)) {
                    return DtoUtil.getFalseDto("查询用户失败", 29002);
                }
               /* InviteMessage inviteMessage=new InviteMessage(account.getUserName()+"同意了你的邀请，成为"+singleEvent.getEventname()+"事件的支持者。","",feedbackEventBackerVo.getExtraData());
                System.out.println("消息内容："+inviteMessage.getContent());
                String[] targetId={singleEvent.getUserid().toString()};
                ResponseResult result=rongCloudMethodUtil.sendSystemMessage(feedbackEventBackerVo.getUserId(),targetId,inviteMessage,"","");
                if (result.getCode()!=200){
                    return DtoUtil.getFalseDto("发送消息失败",17002);
                }*/

                //设置定时给支持者发信息
                String dateFormat = singleEvent.getYear() + "-" + singleEvent.getMonth() + "-" + singleEvent.getDay() + " " + Long.parseLong(singleEvent.getStarttime()) / 60 + "-" + (Long.parseLong(singleEvent.getStarttime()) % 60L + Long.parseLong(singleEvent.getRemindTime())) + "-00";
                System.out.println("提醒时间：" + dateFormat);
                TimedTask timedTask = new TimedTask();
                //userId, eventId, backerId, timer, content
                timedTask.setUserId(singleEvent.getUserid());
                timedTask.setEventId(singleEvent.getEventid());
                timedTask.setBackerId(Long.parseLong(feedbackEventBackerVo.getUserId()));
                timedTask.setTimer(dateFormat);
                account = accountMapper.queryAccount(singleEvent.getUserid().toString());
                timedTask.setContent(account.getUserName() + "的事件：" + singleEvent.getEventname() + "将于" + singleEvent.getMonth() + "月" + singleEvent.getDay() + "日 " + Long.parseLong(singleEvent.getStarttime()) / 60 + "：" + Long.parseLong(singleEvent.getStarttime()) % 60 + " 开始。");
                if (timedTaskMapper.addTimedTask(timedTask) == 0) {
                    return DtoUtil.getFalseDto("设置定时失败", 29004);
                }
            } else if (Long.parseLong(feedbackEventBackerVo.getChoose()) == 2) {
                //拒绝
                //更改backer表状态
                backers.setUserId(singleEvent.getUserid());
                backers.setEventId(singleEvent.getEventid());
                backers.setBackerId(Long.parseLong(feedbackEventBackerVo.getUserId()));
                backers.setStatus(2L);
                if (backerMapper.updateBackerStatus(backers) == 0) {
                    return DtoUtil.getFalseDto("回应状态修改失败", 29001);
                }
                //发送消息给发起者
                RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                Account account = accountMapper.queryAccount(feedbackEventBackerVo.getUserId());
                if (ObjectUtils.isEmpty(account)) {
                    return DtoUtil.getFalseDto("查询用户失败", 29002);
                }
                /*InviteMessage inviteMessage=new InviteMessage(account.getUserName()+"拒绝了你"+singleEvent.getEventname()+"事件的邀请。","",feedbackEventBackerVo.getExtraData());
                System.out.println("消息内容："+inviteMessage.getContent());
                String[] targetId={singleEvent.getUserid().toString()};
                ResponseResult result=rongCloudMethodUtil.sendSystemMessage(feedbackEventBackerVo.getUserId(),targetId,inviteMessage,"","");
                if (result.getCode()!=200){
                    return DtoUtil.getFalseDto("发送消息失败",17002);
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getFalseDto("消息发送失败", 26002);
        }
        return DtoUtil.getSuccessDto("消息发送成功", 100000);
    }

    /**
     * 修改支持事件
     *
     * @param addbackerVo
     * @param token
     * @return
     */
    @Override
    public Dto updBackerEvent(AddBackerVo addbackerVo, String token) {
        try {
            if (StringUtils.isEmpty(token)) {
                return DtoUtil.getFalseDto("token未获取到", 21013);
            }
            if (ObjectUtils.isEmpty(addbackerVo)) {
                return DtoUtil.getFalseDto("添加邀请事件数据未获取到", 26001);
            }
            if (StringUtils.isEmpty(addbackerVo.getUserId())) {
                return DtoUtil.getFalseDto("userId不能为空", 21011);
            }
            if (!token.equals(stringRedisTemplate.opsForValue().get(addbackerVo.getUserId()))) {
                return DtoUtil.getFalseDto("请重新登录", 21013);
            }
            //拿到新事件和支持者列表
            SingleEvent singleEvent = JSONObject.parseObject(addbackerVo.getSingleEvent(), SingleEvent.class);
            //拿到旧事件和支持者列表
            SingleEvent singleEventOld = eventMapper.queryEventOne(singleEvent.getUserid().toString(), singleEvent.getEventid().toString());
            //修改事件
            if (eventMapper.alterEventsByUserId(singleEvent) <= 0) {
                return DtoUtil.getFalseDto("修改事件失败", 211111);
            }
            //把修改内容发送给支持者
            String[] backers = backerMapper.queryBackers(singleEventOld.getUserid().toString(), singleEventOld.getEventid().toString()).toString().replace("[", "").replace("]", "").split(",");
            System.out.println("支持者：" + backers);
            StringBuffer different = SingleEventUtil.eventDifferent(SingleEvent.toMap(singleEvent), SingleEvent.toMap(singleEventOld));
            //发送消息给支持者
            RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
            Account account = accountMapper.queryAccount(addbackerVo.getUserId());
            if (ObjectUtils.isEmpty(account)) {
                return DtoUtil.getFalseDto("查询用户失败", 29002);
            }
        /*InviteMessage inviteMessage=new InviteMessage(account.getUserName()+"修改了事件"+singleEvent.getEventname()+"，修改内容为："+different.replace(different.length()-1,different.length(),"。"),"","");
        System.out.println("消息内容："+inviteMessage.getContent());
        ResponseResult result= null;
        try {
            result = rongCloudMethodUtil.sendSystemMessage(addbackerVo.getUserId(),backers,inviteMessage,"","");
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("发送消息失败",17002);
        }
        if (result.getCode()!=200){
            return DtoUtil.getFalseDto("发送消息失败",17002);
        }*/
            //定时器修改
            String dateFormat = singleEvent.getYear() + "-" + singleEvent.getMonth() + "-" + singleEvent.getDay() + " " + Long.parseLong(singleEvent.getStarttime()) / 60 + "-" + (Long.parseLong(singleEvent.getStarttime()) % 60L + Long.parseLong(singleEvent.getRemindTime())) + "-00";
            System.out.println("提醒时间：" + dateFormat);
            TimedTask timedTask = new TimedTask();
            //userId, eventId, backerId, timer, content
            timedTask.setUserId(singleEvent.getUserid());
            timedTask.setEventId(singleEvent.getEventid());
            timedTask.setTimer(dateFormat);
            timedTask.setTaskStatus(0L);
            account = accountMapper.queryAccount(singleEvent.getUserid().toString());
            timedTask.setContent(account.getUserName() + "的事件：" + singleEvent.getEventname() + "将于" + singleEvent.getMonth() + "月" + singleEvent.getDay() + "日 " + Long.parseLong(singleEvent.getStarttime()) / 60 + "：" + Long.parseLong(singleEvent.getStarttime()) % 60 + " 开始。");
            if (timedTaskMapper.updateTimedTask(timedTask) == 0) {
                return DtoUtil.getFalseDto("修改定时失败", 29004);
            }
            return DtoUtil.getSuccessDto("修改事件成功", 100000);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("修改支持事件出错", 2333);
        }
    }

    /**
     * 删除支持事件
     *
     * @param deleteEventVo
     * @param token
     * @return
     */
    @Override
    public Dto delBackerEvent(DeleteEventVo deleteEventVo, String token) {
        try {
            if (StringUtils.isEmpty(token)) {
                return DtoUtil.getFalseDto("token未获取到", 21013);
            }
            if (ObjectUtils.isEmpty(deleteEventVo)) {
                return DtoUtil.getFalseDto("添加邀请事件数据未获取到", 26001);
            }
            if (StringUtils.isEmpty(deleteEventVo.getUserId())) {
                return DtoUtil.getFalseDto("userId不能为空", 21011);
            }
            if (!token.equals(stringRedisTemplate.opsForValue().get(deleteEventVo.getUserId()))) {
                return DtoUtil.getFalseDto("请重新登录", 21013);
            }
            SingleEvent singleEvent = eventMapper.queryEventOne(deleteEventVo.getUserId(), deleteEventVo.getEventId());
            //删除事件表
            if (eventMapper.withdrawEventsByUserId(deleteEventVo) <= 0) {
                return DtoUtil.getFalseDto("删除事件失败", 22222);
            }
            if (deleteEventVo.getEventStatus().equals("1")) {
                UserStatistics userStatistics = new UserStatistics();
                userStatistics.setCompleted(1L);
                userStatistics.setUnfinished(-1L);
                achievementMapper.updateUserStatistics(userStatistics, deleteEventVo.getUserId());
            } else if (deleteEventVo.getEventStatus().equals("2")) {
                UserStatistics userStatistics = new UserStatistics();
                userStatistics.setUnfinished(-1L);
                achievementMapper.updateUserStatistics(userStatistics, deleteEventVo.getUserId());
            }
            //告知支持者
            String[] backers = backerMapper.queryBackers(deleteEventVo.getUserId(), deleteEventVo.getEventId()).toString().replace("[", "").replace("]", "").split(",");
            System.out.println("支持者：" + backers);
            //发送消息给支持者
            RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
            Account account = accountMapper.queryAccount(deleteEventVo.getUserId());
            if (ObjectUtils.isEmpty(account)) {
                return DtoUtil.getFalseDto("查询用户失败", 29002);
            }
        /*InviteMessage inviteMessage=new InviteMessage(account.getUserName()+"删除了事件"+singleEvent.getEventname(),"","");
        System.out.println("消息内容："+inviteMessage.getContent());
        ResponseResult result= null;
        try {
            result = rongCloudMethodUtil.sendSystemMessage(deleteEventVo.getUserId(),backers,inviteMessage,"","");
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("发送消息失败",17002);
        }
        if (result.getCode()!=200){
            return DtoUtil.getFalseDto("发送消息失败",17002);
        }*/
            return DtoUtil.getSuccessDto("删除事件成功", 100000);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("删除支持事件出错", 2333);
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
    public Dto addInviteEvent(AddInviteEventVo addInviteEventVo, String token) {
        if (StringUtils.isEmpty(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        if (ObjectUtils.isEmpty(addInviteEventVo)) {
            return DtoUtil.getFalseDto("添加邀请事件数据未获取到", 26001);
        }
        if (StringUtils.isEmpty(addInviteEventVo.getUserId())) {
            return DtoUtil.getFalseDto("userId不能为空", 21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(addInviteEventVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21013);
        }
        try {
            //保存这条事件
            SingleEvent singleEvent = JSONObject.parseObject(addInviteEventVo.getSingleEvent(), SingleEvent.class);
            singleEvent.setUserid(Long.parseLong(addInviteEventVo.getUserId()));
            //判断是否有冲突事件
            boolean y = SingleEventUtil.eventTime(eventMapper.queryClashEventList(singleEvent), Long.valueOf(singleEvent.getStarttime()), Long.valueOf(singleEvent.getEndtime()));
            boolean m = stringRedisTemplate.hasKey(addInviteEventVo.getUserId() + singleEvent.getEventid().toString());
            if (!y || m) {
                return DtoUtil.getFalseDto("该时间段内已有事件不能添加", 21012);
            }
            //好友列的数据
            System.out.println("参与人员：" + singleEvent.getPerson());
            EventPersons eventPersons = JSONObject.parseObject(singleEvent.getPerson(), EventPersons.class);
            String[] persons = eventPersons.getFriendsId().split(",");
            ArrayList<String> personList=new ArrayList<>();
            //判断好友是否开启了邀请权限
            for (int i = 0; i < persons.length; i++) {
                Friendship friendship=accountMapper.queryFriendshipDetail(persons[i],addInviteEventVo.getUserId());
                if (friendship.getInvite()==1){
                    //只添加满足条件的人
                    personList.add(persons[i]);
                }
            }
            String redisKey = addInviteEventVo.getUserId() + singleEvent.getEventid();
            stringRedisTemplate.opsForValue().set(redisKey, JSON.toJSONString(singleEvent));
            //生成统计表
            List<StatisticsTable> tables = new ArrayList<>();
            for (String userId : personList) {
                StatisticsTable statisticsTable = new StatisticsTable();
                statisticsTable.setCreatorId(Long.parseLong(addInviteEventVo.getUserId()));
                statisticsTable.setEventId(singleEvent.getEventid());
                statisticsTable.setUserId(Long.parseLong(userId));
                tables.add(statisticsTable);
            }
            int i = statisticsMapper.createStatistics(tables);
            if (i <= 0) {
                return DtoUtil.getFalseDto("生成统计表失败", 26003);
            }
            //消息状态保存在数据库
            MsgStatus msgStatus = new MsgStatus();
            msgStatus.setType(1L);
            System.out.println("这是啥啊"+addInviteEventVo.getUserId());
            msgStatus.setUserId(Long.parseLong(addInviteEventVo.getUserId()));
            if (msgStatusMapper.addNewMsg(msgStatus) == 0) {
                return DtoUtil.getFalseDto("消息状态保存失败", 26010);
            }
            System.out.println("添加事件邀请输出的消息Id==>  " + msgStatus.getId());
            //向被邀请者发送信息
            RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
            String date = singleEvent.getYear() + "/" + singleEvent.getMonth() + "/" + singleEvent.getDay();
            InviteMessage inviteMessage = new InviteMessage(singleEvent.getEventname(), date, JSON.toJSONString(SingleEventUtil.getShowSingleEvent(singleEvent)), "2", msgStatus.getId().toString());
            ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(addInviteEventVo.getUserId(), personList.toArray(new String[]{}), 0, inviteMessage);
            if (result.getCode() != 200) {
                return DtoUtil.getFalseDto("发送消息失败", 17002);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("消息发送出错", 26002);
        }
        return DtoUtil.getSuccessDto("消息发送成功", 100000);
    }

    /**
     * 修改一条邀请事件
     *
     * @param addInviteEventVo
     * @param token
     * @return
     */
    @Override
    public Dto updInviteEvent(AddInviteEventVo addInviteEventVo, String token) {
        if (StringUtils.isEmpty(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        if (ObjectUtils.isEmpty(addInviteEventVo)) {
            return DtoUtil.getFalseDto("添加邀请事件数据未获取到", 26001);
        }
        if (StringUtils.isEmpty(addInviteEventVo.getUserId())) {
            return DtoUtil.getFalseDto("userId不能为空", 21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(addInviteEventVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21013);
        }
        try {
            System.out.println("修改一条邀请事件时输出的接收数据"+addInviteEventVo.toString());
            //接收到的修改信息
            SingleEvent singleEvent = JSONObject.parseObject(addInviteEventVo.getSingleEvent(), SingleEvent.class);
            Map<String, Object> m1 = SingleEvent.toMap(singleEvent);
            //原来的信息
            SingleEvent singleEventOld = eventMapper.queryEventOne(singleEvent.getUserid().toString(), singleEvent.getEventid().toString());
            System.out.println("修改一条邀请事件时输出的原来的信息"+singleEventOld.toString());
            Map<String, Object> m2 = SingleEvent.toMap(singleEventOld);
            //比较差异
            StringBuffer different = SingleEventUtil.eventDifferent(m1, m2);

            if (StringUtils.isEmpty(different)) {
                return DtoUtil.getFalseDto("没有任何更改", 29102);
            }
            System.out.println("修改事件邀请" + singleEvent.toString());
            String redisKey = singleEvent.getUserid().toString() + singleEvent.getEventid();
            stringRedisTemplate.opsForValue().set(redisKey, JSON.toJSONString(singleEvent));
            EventPersons eventPersons=JSONObject.parseObject(singleEvent.getPerson(),EventPersons.class);
            String[] persons = eventPersons.getFriendsId().split(",");
            //生成投票
            List<StatisticsTable> tables = new ArrayList<>();
            for (String userId : persons) {
                StatisticsTable statisticsTable = new StatisticsTable();
                statisticsTable.setCreatorId(Long.parseLong(addInviteEventVo.getUserId()));
                statisticsTable.setEventId(singleEvent.getEventid());
                statisticsTable.setUserId(Long.parseLong(userId));
                tables.add(statisticsTable);
            }
            int a = statisticsMapper.createStatistics(tables);
            if (a <= 0) {
                return DtoUtil.getFalseDto("生成统计表失败", 26003);
            }
            //发送信息
            Account account = accountMapper.queryAccount(addInviteEventVo.getUserId());
            RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
            //内容修改
            String content = account.getUserName() + "请求修改事件" + singleEventOld.getEventname() + "：" + different.replace(different.length() - 1, different.length(), "。");
            System.out.println("消息内容==>" + content);
            //消息状态保存在数据库
            MsgStatus msgStatus = new MsgStatus();
            msgStatus.setType(1L);
            msgStatus.setUserId(Long.parseLong(addInviteEventVo.getUserId()));
            if (msgStatusMapper.addNewMsg(msgStatus) == 0) {
                return DtoUtil.getFalseDto("消息状态保存失败", 26010);
            }
            System.out.println("修改事件邀请输出的消息Id==>  " + msgStatus.getId());
            UpdateInviteMessage updateInviteMessage=new UpdateInviteMessage(content,"", JSON.toJSONString(singleEvent),"2",msgStatus.getId().toString());
            //接收人员变动
            for (int i = 0; i < persons.length; i++) {
                if (persons[i].equals(addInviteEventVo.getUserId())){
                    persons[i]=singleEvent.getUserid().toString();
                }
            }
            ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(addInviteEventVo.getUserId(), persons, 0, updateInviteMessage);
            if (result.getCode()!=200){
                return DtoUtil.getFalseDto("发送消息失败",17002);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("消息发送出错", 26002);
        }

        return DtoUtil.getFalseDto("发送成功", 100000);
    }

    /**
     * 删除一条邀请事件
     *
     * @param receivedSearchOnce
     * @param token
     * @return
     */
    @Override
    public Dto delInviteEvent(ReceivedSearchOnce receivedSearchOnce, String token) {
        if (StringUtils.isEmpty(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        if (ObjectUtils.isEmpty(receivedSearchOnce)) {
            return DtoUtil.getFalseDto("添加邀请事件数据未获取到", 26001);
        }
        if (StringUtils.isEmpty(receivedSearchOnce.getUserId())) {
            return DtoUtil.getFalseDto("userId不能为空", 21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedSearchOnce.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21013);
        }
        try {
            //找到该事件
            SingleEvent singleEvent = eventMapper.queryEventOne(receivedSearchOnce.getUserId(), receivedSearchOnce.getEventId());
            System.out.println("删除邀请事件接口"+singleEvent.toString());
            //查看该事件最高权限
            SingleEventVice singleEventVice = new SingleEventVice();
            singleEventVice.setUserId(Long.parseLong(receivedSearchOnce.getUserId()));
            singleEventVice.setEventId(Long.parseLong(receivedSearchOnce.getEventId()));
            singleEventVice = eventViceMapper.queryEventVice(singleEventVice);
            if (ObjectUtils.isEmpty(singleEvent) || ObjectUtils.isEmpty(singleEventVice)) {
                return DtoUtil.getFalseDto("要删除的事件未找到", 29001);
            }
            //如果是创建者删除
            int b=-1;
            if (singleEvent.getUserid().equals(singleEventVice.getCreateBy())) {
                //该事件从创建者时间轴删除
                int a=eventMapper.deleteByDeleteType(singleEvent.getEventid(), "singleevent", receivedSearchOnce.getUserId());
                //其他参与者的事件里删除本参与者
                EventPersons eventPersons=JSONObject.parseObject(singleEvent.getPerson(),EventPersons.class);
                System.out.println("删除邀请事件接口输出的person"+eventPersons.toString());
                String[] persons = eventPersons.getFriendsId().split(",");
                for (int j = 0; j < persons.length; j++) {
                    //其他参与者的事件
                    SingleEvent singleEvent1 = eventMapper.queryEventOne(persons[j], singleEvent.getEventid().toString());
                    //变更参与者
                    eventPersons=JSONObject.parseObject(singleEvent1.getPerson(),EventPersons.class);
                    eventPersons.setFriendsId(eventPersons.getFriendsId().indexOf(receivedSearchOnce.getUserId()) != -1 ? eventPersons.getFriendsId().replace(receivedSearchOnce.getUserId(), "") : eventPersons.getFriendsId());
                    singleEvent1.setPerson(JSON.toJSONString(eventPersons));
                    b= eventMapper.alterEventsByUserId(singleEvent1);
                }
                //最高权限表更改
                Random random=new Random();
                int i=random.nextInt(persons.length);
                int c=eventViceMapper.updateEventVice(singleEvent.getEventid().toString(),singleEventVice.getCreateBy().toString(),persons[i]);
                //给最高权限者发送信息
                TxtMessage txtMessage = new TxtMessage("你已成为"+singleEvent.getEventname()+"事件的创建者，拥有该事件决策权", "");
                System.out.println("创建事件时创建者选择输出的消息内容：" + txtMessage.getContent());
                try {
                    String[] targetId = {persons[i]};
                    RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
                    ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(SYSTEMID, targetId,0, txtMessage);
                    if (result.getCode() != 200) {
                        return DtoUtil.getFalseDto("发送消息失败", 17002);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return DtoUtil.getFalseDto("消息发送失败", 26002);
                }
                if (a <= 0 || b <= 0 || c <= 0) {
                    //回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return DtoUtil.getFalseDto("删除事件失败", 29002);
                }
                //完成时记得找孔庆一(整个方法)

            } else {
                //如果不是创建者删除
                //从自己的事件表里移除
                int i = eventMapper.deleteByDeleteType(singleEvent.getEventid(), "singleevent", receivedSearchOnce.getUserId());
                //其他参与者的事件里删除本参与者
                EventPersons eventPersons=JSONObject.parseObject(singleEvent.getPerson(),EventPersons.class);
                String[] persons = eventPersons.getFriendsId().split(",");
                for (int j = 0; j < persons.length; j++) {
                    //其他参与者的事件
                    SingleEvent singleEvent1 = eventMapper.queryEventOne(persons[j], singleEvent.getEventid().toString());
                    //变更参与者
                    eventPersons=JSONObject.parseObject(singleEvent1.getPerson(),EventPersons.class);
                    eventPersons.setFriendsId(eventPersons.getFriendsId().indexOf(receivedSearchOnce.getUserId()) != -1 ? eventPersons.getFriendsId().replace(receivedSearchOnce.getUserId(), "") : eventPersons.getFriendsId());
                    singleEvent1.setPerson(JSON.toJSONString(eventPersons));
                    int delResult = eventMapper.alterEventsByUserId(singleEvent1);
                    if (delResult <= 0) {
                        //回滚
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return DtoUtil.getFalseDto("其他参与者的事件里删除本参与者失败", 29002);
                    }
                }
                //通知其他参与者
                Account account = accountMapper.queryAccount(receivedSearchOnce.getUserId());
                RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                String content = account.getUserName() + "退出了事件：" + singleEvent.getEventname() + "。";
                System.out.println("消息内容" + content);
                TxtMessage txtMessage = new TxtMessage(content, "");
                ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(receivedSearchOnce.getUserId(),persons, 0, txtMessage);
                if (result.getCode()!=200){
                    return DtoUtil.getFalseDto("发送消息失败",17002);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("消息发送出错", 26002);
        }
        return DtoUtil.getSuccessDto("事件删除成功", 100000);
    }

    /**
     * 回应事件邀请
     *
     * @param feedbackEventInviteVo
     * @param token
     * @return
     */
    @Override
    public Dto  feedbackEventInvite(FeedbackEventInviteVo feedbackEventInviteVo, String token) {
        try {
            if (StringUtils.isEmpty(token)) {
                return DtoUtil.getFalseDto("token未获取到", 21013);
            }
            if (ObjectUtils.isEmpty(feedbackEventInviteVo)) {
                return DtoUtil.getFalseDto("添加邀请事件数据未获取到", 26001);
            }
            if (StringUtils.isEmpty(feedbackEventInviteVo.getUserId())) {
                return DtoUtil.getFalseDto("userId不能为空", 21011);
            }
            if (!token.equals(stringRedisTemplate.opsForValue().get(feedbackEventInviteVo.getUserId()))) {
                return DtoUtil.getFalseDto("请重新登录", 21013);
            }
            //拿到发起者的事件
            System.out.println("回应事件邀请时输出的事件内容：" + feedbackEventInviteVo.getExtraData());
            System.out.println("***********************************************************");
            System.out.println("回应事件邀请时输出的所有内容：" + feedbackEventInviteVo.toString());
            SingleEvent singleEvent = JSONObject.parseObject(feedbackEventInviteVo.getExtraData(), SingleEvent.class);
            Long creatId=singleEvent.getUserid();
            //判断该事件的统计表是否已过期
            if ("1".equals(statisticsMapper.selectStaIsOverdue(singleEvent.getUserid().toString(), singleEvent.getEventid().toString()))) {
                return DtoUtil.getFalseDto("投票已过期", 21013);
            }
            EventPersons eventPersons = JSONObject.parseObject(singleEvent.getPerson(), EventPersons.class);
            String[] persons = eventPersons.getFriendsId().split(",");
            StatisticsTable statisticsTable = new StatisticsTable();

            //通过判断所有用户是否都答复决定是否发送消息给事件发起者
            //如果同意
            if (Integer.parseInt(feedbackEventInviteVo.getChoose()) == 0) {
                //判断接受者的事件列表是否有冲突
                //查到冲突的事件集合
                singleEvent.setUserid(Long.parseLong(feedbackEventInviteVo.getUserId()));
                List<SingleEvent> singleEvents = eventUtil.eventClashUtil(singleEvent);
                System.out.println("回应事件邀请时的冲突事件集合"+singleEvents.toString());
                //判断是否忽略冲突任然添加
                if (Integer.parseInt(feedbackEventInviteVo.getIsHold()) == 0) {
                    //如果有冲突反馈给该用户
                    if (singleEvents.size() > 0) {
                        return DtoUtil.getFalseDto("当前时间段已有事件", 21016);
                    }
                }
                //更改反馈统计表
                statisticsTable.setUserId(Long.parseLong(feedbackEventInviteVo.getUserId()));
                statisticsTable.setEventId(singleEvent.getEventid());
                statisticsTable.setCreatorId(creatId);
                statisticsTable.setChoose(Long.parseLong(feedbackEventInviteVo.getChoose()));
                statisticsTable.setModify(1L);
                System.out.println("回应事件邀请时输出的统计表内容：" + statisticsTable.toString());
                statisticsMapper.updateStatistics(statisticsTable);
                //查询是否所有人都给了反馈
                int i = statisticsMapper.queryStatisticsCount(statisticsTable);
                //所有的反馈都收到了 或者  通过判断timeUp字段决定是否发送消息给事件发起者
                if (i >= persons.length || Long.parseLong(feedbackEventInviteVo.getTimeUp()) == 1) {
                    //设置统计表为已过期
                    statisticsMapper.updStaIsOverdue(creatId.toString(), singleEvent.getEventid().toString());
                    //发送统计结果给事件发起者
                    RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                    //查询统计结果
                    Map<String, Long> map = statisticsMapper.queryFeedbackStatistics(creatId.toString(), singleEvent.getEventid().toString());
                    //发送统计结果
                    FeedbackInviteMessage feedbackInviteMessage = new FeedbackInviteMessage(map.get("agree").toString(), map.get("refuse").toString(), map.get("noReply").toString(), map.get("total").toString(), feedbackEventInviteVo.getExtraData(), "2");
                    String[] targetId = {creatId.toString()};
                    try {
                        //发送者为系统
                        ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(SYSTEMID, targetId, 0, feedbackInviteMessage);
                        System.out.println("回应事件邀请时输出的消息回调" + result.toString());
                        if (result.getCode() != 200) {
                            return DtoUtil.getFalseDto("发送消息失败", 17002);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return DtoUtil.getFalseDto("消息发送失败", 26002);
                    }
                }
                //修改消息状态
                if (msgStatusMapper.updateMsgStatus(feedbackEventInviteVo.getChoose(), feedbackEventInviteVo.getMsgId()) == 0) {
                    System.out.println("=================================修改消息状态失败=================================================");
                    return DtoUtil.getFalseDto("修改消息状态失败", 21020);
                }
                return DtoUtil.getSuccessDto("信息已发出", 100000);
                //如果拒绝
            } else if (Integer.parseInt(feedbackEventInviteVo.getChoose()) == 1) {
                //更改反馈统计表
                statisticsTable.setUserId(Long.parseLong(feedbackEventInviteVo.getUserId()));
                statisticsTable.setEventId(singleEvent.getEventid());
                statisticsTable.setCreatorId(singleEvent.getUserid());
                statisticsTable.setChoose(Long.parseLong(feedbackEventInviteVo.getChoose()));
                statisticsTable.setModify(1L);
                System.out.println("回应事件邀请时输出的统计表内容：" + statisticsTable.toString());
                statisticsMapper.updateStatistics(statisticsTable);
                //查询是否所有人都给了反馈
                int i = statisticsMapper.queryStatisticsCount(statisticsTable);
                //所有的反馈都收到了 或者  通过判断timeUp字段决定是否发送消息给事件发起者
                if (i >= persons.length || Long.parseLong(feedbackEventInviteVo.getTimeUp()) == 1) {
                    //设置统计表为已过期
                    statisticsMapper.updStaIsOverdue(singleEvent.getUserid().toString(), singleEvent.getEventid().toString());
                    //发送统计结果给事件发起者
                    RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                    //查询统计结果
                    Map<String, Long> map = statisticsMapper.queryFeedbackStatistics(singleEvent.getUserid().toString(), singleEvent.getEventid().toString());
                    //发送统计结果
                    FeedbackInviteMessage feedbackInviteMessage = new FeedbackInviteMessage(map.get("agree").toString(), map.get("refuse").toString(), map.get("noReply").toString(), map.get("total").toString(), feedbackEventInviteVo.getExtraData(), "2");
                    String[] targetId = {singleEvent.getUserid().toString()};
                    try {
                        ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(SYSTEMID, targetId, 0, feedbackInviteMessage);
                        System.out.println("回应事件邀请时输出的消息回调" + result.toString());
                        if (result.getCode() != 200) {
                            return DtoUtil.getFalseDto("发送消息失败", 17002);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return DtoUtil.getFalseDto("消息发送失败", 26002);
                    }
                }
                //修改消息状态
                if (msgStatusMapper.updateMsgStatus(feedbackEventInviteVo.getChoose(), feedbackEventInviteVo.getMsgId()) == 0) {
                    System.out.println("=================================修改消息状态失败=================================================");
                    return DtoUtil.getFalseDto("修改消息状态失败", 21020);
                }
                return DtoUtil.getSuccessDto("信息已发出", 100000);
            } else if (Integer.parseInt(feedbackEventInviteVo.getChoose()) == 2 && Integer.parseInt(feedbackEventInviteVo.getTimeUp()) == 1) {
                //通过判断timeUp字段决定是否发送消息给事件发起者
                //设置统计表为已过期
                statisticsMapper.updStaIsOverdue(singleEvent.getUserid().toString(), singleEvent.getEventid().toString());
                //发送统计结果给事件发起者
                RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
                //查询统计结果
                Map<String, Long> map = statisticsMapper.queryFeedbackStatistics(singleEvent.getUserid().toString(), singleEvent.getEventid().toString());
                //发送统计结果
                FeedbackInviteMessage feedbackInviteMessage = new FeedbackInviteMessage(map.get("agree").toString(), map.get("refuse").toString(), map.get("noReply").toString(), map.get("total").toString(), feedbackEventInviteVo.getExtraData(), "2");
                String[] targetId = {singleEvent.getUserid().toString()};
                try {
                    ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(SYSTEMID, targetId, 0, feedbackInviteMessage);
                    System.out.println("回应事件邀请时输出的消息回调" + result.toString());
                    if (result.getCode() != 200) {
                        return DtoUtil.getFalseDto("发送消息失败", 17002);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return DtoUtil.getFalseDto("消息发送失败", 26002);
                }
                //修改消息状态
                if (msgStatusMapper.updateMsgStatus("3", feedbackEventInviteVo.getMsgId()) == 0) {
                    System.out.println("=================================修改消息状态失败=================================================");
                    return DtoUtil.getFalseDto("修改消息状态失败", 21020);
                }
                return DtoUtil.getSuccessDto("信息已发出", 100000);
            }
            return DtoUtil.getFalseDto("反馈内容未识别", 26010);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("回应事件邀请出错", 2333);
        }
    }


    /**
     * 创建事件时创建者选择
     *
     * @param eventCreatorChooseVo
     * @param token
     * @return
     */
    @Override
    public Dto eventCreatorChoose(EventCreatorChooseVo eventCreatorChooseVo, String token) {
        try {
            if (StringUtils.isEmpty(token)) {
                return DtoUtil.getFalseDto("token未获取到", 21013);
            }
            if (ObjectUtils.isEmpty(eventCreatorChooseVo)) {
                return DtoUtil.getFalseDto("创建者选择数据未获取到", 26001);
            }
            if (StringUtils.isEmpty(eventCreatorChooseVo.getUserId())) {
                return DtoUtil.getFalseDto("userId不能为空", 21011);
            }
            if (!token.equals(stringRedisTemplate.opsForValue().get(eventCreatorChooseVo.getUserId()))) {
                return DtoUtil.getFalseDto("请重新登录", 21013);
            }
            //先拿到事件
            SingleEvent singleEvent = JSONObject.parseObject(eventCreatorChooseVo.getExtraData(), SingleEvent.class);


            //这里少了个判断状态

            if (!ObjectUtils.isEmpty(eventMapper.queryEventOne(singleEvent.getUserid().toString(),singleEvent.getEventid().toString()))){
                return DtoUtil.getFalseDto("该事件已经添加成功不能重复选择",2333);
            }

            System.out.println("创建事件时创建者选择输出的事件内容：" + eventCreatorChooseVo.getExtraData());
            RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
            EventPersons eventPersons = JSONObject.parseObject(singleEvent.getPerson(), EventPersons.class);
            String[] persons = eventPersons.getFriendsId().split(",");
            Account account = accountMapper.queryAccount(eventCreatorChooseVo.getUserId());
            //判断是修改还是新增
            boolean flag=false;
            SingleEvent sEvent = eventMapper.queryEventOne(singleEvent.getUserid().toString(), singleEvent.getEventid().toString());
            if (!ObjectUtils.isEmpty(sEvent)){
                flag=true;
            }
            //判断选择
            if (Integer.parseInt(eventCreatorChooseVo.getChoose()) == 1) {
                //保留
                List<String> agrees = statisticsMapper.queryChooser("0", singleEvent.getUserid().toString(), singleEvent.getEventid().toString());
                String finalPerson = String.join(",", agrees);
                System.out.println("创建事件时创建者选择输出的最终参与者" + finalPerson);
                eventPersons.setFriendsId(finalPerson);
                singleEvent.setPerson(JSON.toJSONString(eventPersons));
                //事件时间冲突判断
                if (!SingleEventUtil.eventTime(eventMapper.queryClashEventList(singleEvent), Long.valueOf(singleEvent.getStarttime()), Long.valueOf(singleEvent.getEndtime()))) {
                    return DtoUtil.getFalseDto("时间段冲突,无法修改", 21012);
                }
                //把该事件添加进发起者事件列表(修改这件事)
                if (flag) {
                    //修改
                    eventMapper.alterEventsByUserId(singleEvent);
                }else {
                    //上传
                    eventMapper.uploadingEvents(singleEvent);
                    UserStatistics statistics = new UserStatistics();
                    statistics.setUserId(Long.valueOf(eventCreatorChooseVo.getUserId()));
                    //用户新增一条事件,未完成+1
                    statistics.setUnfinished(1L);
                    achievementMapper.updateUserStatistics(statistics, eventCreatorChooseVo.getUserId());
                    //在事件副表插入创建者
                    SingleEventVice singleEventVice = new SingleEventVice();
                    singleEventVice.setCreateBy(Long.parseLong(eventCreatorChooseVo.getUserId()));
                    singleEventVice.setUserId(singleEvent.getUserid());
                    singleEventVice.setEventId(singleEvent.getEventid());
                    eventViceMapper.createEventVice(singleEventVice);
                }
                //判断同意该事件的人，他们的事件表是否有冲突事件
                for (String userId : agrees) {
                    singleEvent.setUserid(Long.parseLong(userId));
                    List<SingleEvent> singleEvents = eventUtil.eventClashUtil(singleEvent);
                    System.out.println("创建事件时创建者选择时的冲突事件集合"+singleEvents.toString());
                    if (singleEvents.size() > 0) {
                        for (SingleEvent se : singleEvents) {
                            //把冲突事件移入草稿箱
                            eventMapper.uplDraft(se);
                            //从事件表删除
                            DeleteEventVo deleteEventVo = new DeleteEventVo();
                            deleteEventVo.setEventId(se.getEventid().toString());
                            deleteEventVo.setUserId(se.getUserid().toString());
                            deleteEventVo.setEventStatus("2");
                            eventMapper.withdrawEventsByUserId(deleteEventVo);
                        }
                        //把该事件添加到该好友的事件表
                        //参与者变更(把参与者里的自己替换成创建者)
                        eventPersons.setFriendsId(eventPersons.getFriendsId().replace(userId, eventCreatorChooseVo.getUserId()));
                        singleEvent.setPerson(JSON.toJSONString(eventPersons));
                        if (flag) {
                            //修改
                            eventMapper.alterEventsByUserId(singleEvent);
                        }else {
                            //上传
                            eventMapper.uploadingEvents(singleEvent);
                            UserStatistics statistics = new UserStatistics();
                            statistics.setUserId(Long.valueOf(singleEvent.getUserid()));
                            //用户新增一条事件,未完成+1
                            statistics.setUnfinished(1L);
                            achievementMapper.updateUserStatistics(statistics, eventCreatorChooseVo.getUserId());
                            //在事件副表插入创建者
                            SingleEventVice singleEventVice1 = new SingleEventVice();
                            singleEventVice1.setCreateBy(Long.parseLong(eventCreatorChooseVo.getUserId()));
                            singleEventVice1.setUserId(singleEvent.getUserid());
                            singleEventVice1.setEventId(singleEvent.getEventid());
                            eventViceMapper.createEventVice(singleEventVice1);
                        }
                        //通知该好友事件已添加
                        String content=account.getUserName() + "发起的事件" + singleEvent.getEventname() + "已添至你的事件表";
                        TxtMessage txtMessage = new TxtMessage(content, "");
                        System.out.println("创建事件时创建者选择输出的消息内容：" + txtMessage.getContent());
                        try {
                            String[] targetId = {userId};
                            ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(SYSTEMID, targetId,0, txtMessage);
                            if (result.getCode() != 200) {
                                return DtoUtil.getFalseDto("发送消息失败", 17002);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            return DtoUtil.getFalseDto("消息发送失败", 26002);
                        }
                    }else {
                        //不冲突直接添加
                        //参与者变更(把参与者里的自己替换成创建者)
                        eventPersons.setFriendsId(eventPersons.getFriendsId().replace(userId, eventCreatorChooseVo.getUserId()));
                        singleEvent.setPerson(JSON.toJSONString(eventPersons));
                        if (flag) {
                            //修改
                            eventMapper.alterEventsByUserId(singleEvent);
                        }else {
                            //上传
                            eventMapper.uploadingEvents(singleEvent);
                            UserStatistics statistics = new UserStatistics();
                            statistics.setUserId(Long.valueOf(singleEvent.getUserid()));
                            //用户新增一条事件,未完成+1
                            statistics.setUnfinished(1L);
                            achievementMapper.updateUserStatistics(statistics, eventCreatorChooseVo.getUserId());
                            //在事件副表插入创建者
                            SingleEventVice singleEventVice1 = new SingleEventVice();
                            singleEventVice1.setCreateBy(Long.parseLong(eventCreatorChooseVo.getUserId()));
                            singleEventVice1.setUserId(singleEvent.getUserid());
                            singleEventVice1.setEventId(singleEvent.getEventid());
                            eventViceMapper.createEventVice(singleEventVice1);
                        }
                        //通知该好友事件已修改
                        TxtMessage txtMessage = new TxtMessage(account.getUserName() + "发起的事件" + singleEvent.getEventname() + "已添至你的事件表", "");
                        System.out.println("创建事件时创建者选择输出的消息内容：" + txtMessage.getContent());
                        try {
                            String[] targetId = {userId};
                            ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(SYSTEMID, targetId,0, txtMessage);
                            if (result.getCode() != 200) {
                                return DtoUtil.getFalseDto("发送消息失败", 17002);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            return DtoUtil.getFalseDto("消息发送失败", 26002);
                        }
                    }
                }
                //删除统计表
                int a=statisticsMapper.deleteStatistics(eventCreatorChooseVo.getUserId(),singleEvent.getEventid().toString());
                if (a<=0){
                    return DtoUtil.getFalseDto("删除统计表失败",2333);
                }
                return DtoUtil.getSuccessDto("消息发送成功", 100000);
            } else {
                //不保留
                //删除该事件
                boolean result = stringRedisTemplate.delete(singleEvent.getUserid().toString() + singleEvent.getEventid().toString());
                if (!result) {
                    return DtoUtil.getFalseDto("删除失败", 21014);
                }
                //通知被邀请者
                TxtMessage txtMessage = new TxtMessage("由" + account.getUserName() + "发起的事件" + singleEvent.getEventname() + "已被取消", "");
                System.out.println("创建事件时创建者选择输出的消息内容：" + txtMessage.getContent());
                try {
                    ResponseResult result1 = rongCloudMethodUtil.sendSystemMessage(eventCreatorChooseVo.getUserId(), persons, txtMessage, "", "");
                    if (result1.getCode() != 200) {
                        return DtoUtil.getFalseDto("发送消息失败", 17002);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return DtoUtil.getFalseDto("消息发送失败", 26002);
                }
            }
            //删除统计表
            //删除统计表
            int a=statisticsMapper.deleteStatistics(eventCreatorChooseVo.getUserId(),singleEvent.getEventid().toString());
            if (a<=0){
                return DtoUtil.getFalseDto("删除统计表失败",2333);
            }
            return DtoUtil.getSuccessDto("消息发送成功", 100000);
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("创建者选择出错", 2333);
        }
    }

    /**
     * 修改事件时创建者选择
     *
     * @param eventCreatorChooseVo
     * @param token
     * @return
     */
    @Override
    public Dto eventUpdChoose(EventCreatorChooseVo eventCreatorChooseVo, String token) {
        if (StringUtils.isEmpty(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        if (ObjectUtils.isEmpty(eventCreatorChooseVo)) {
            return DtoUtil.getFalseDto("创建者选择数据未获取到", 26001);
        }
        if (StringUtils.isEmpty(eventCreatorChooseVo.getUserId())) {
            return DtoUtil.getFalseDto("userId不能为空", 21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(eventCreatorChooseVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21013);
        }
        //先拿到事件
        SingleEvent singleEvent = JSONObject.parseObject(eventCreatorChooseVo.getExtraData(), SingleEvent.class);
        Map<String, Object> m1 = SingleEvent.toMap(singleEvent);
        //原来的信息
        SingleEvent singleEventOld = eventMapper.queryEventOne(singleEvent.getUserid().toString(), singleEvent.getEventid().toString());
        Map<String, Object> m2 = SingleEvent.toMap(singleEventOld);
        //比较差异
        StringBuffer different = SingleEventUtil.eventDifferent(m1, m2);

        System.out.println(eventCreatorChooseVo.getExtraData());
        System.out.println("22222223333333==" + singleEvent.toString());
        RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
        String[] persons = singleEvent.getPerson().split(",");
        Account account = accountMapper.queryAccount(eventCreatorChooseVo.getUserId());
        //判断选择
        if (Integer.parseInt(eventCreatorChooseVo.getChoose()) == 1) {
            //保留
            List<String> agrees = statisticsMapper.queryChooser("0", singleEvent.getUserid().toString(), singleEvent.getEventid().toString());

            //把拒绝的和未回应的人的该条事件从事件表移入草稿箱
            List<String> reject = statisticsMapper.queryChooser("1", singleEvent.getUserid().toString(), singleEvent.getEventid().toString());
            reject.addAll(statisticsMapper.queryChooser("2", singleEvent.getUserid().toString(), singleEvent.getEventid().toString()));
            for (String rejectId : reject) {
                singleEventOld.setUserid(Long.parseLong(rejectId));
                //将该事件放入草稿箱
                int i = eventMapper.uplDraft(singleEventOld);
                //将该事件从事件表彻底删除
                int j = eventMapper.deleteSingleEvent(singleEventOld.getUserid().toString(), singleEventOld.getEventid().toString());
                if (i <= 0 || j <= 0) {
                    //回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return DtoUtil.getFalseDto("事件移动到草稿箱失败", 23002);
                }
            }

            String finalPerson = String.join(",", agrees);
            System.out.println("最终参与者" + finalPerson);
            singleEvent.setPerson(finalPerson);
            //事件时间冲突判断
            if (!SingleEventUtil.eventTime(eventMapper.queryClashEventList(singleEvent), Long.valueOf(singleEvent.getStarttime()), Long.valueOf(singleEvent.getEndtime()))) {
                return DtoUtil.getFalseDto("时间段冲突,无法修改", 21012);
            }
            //修改
            eventMapper.alterEventsByUserId(singleEvent);
            //判断同意该事件的人，他们的事件表是否有冲突事件
            for (String userId : agrees) {
                singleEvent.setUserid(Long.parseLong(userId));
                List<SingleEvent> singleEvents = eventUtil.eventClashUtil(singleEvent);
                if (singleEvents.size() > 0) {
                    for (SingleEvent se : singleEvents) {
                        //把冲突事件移入草稿箱
                        eventMapper.uplDraft(se);
                        //从事件表删除
                        DeleteEventVo deleteEventVo = new DeleteEventVo();
                        deleteEventVo.setEventId(se.getEventid().toString());
                        deleteEventVo.setUserId(se.getUserid().toString());
                        deleteEventVo.setEventStatus("2");
                        eventMapper.withdrawEventsByUserId(deleteEventVo);
                    }
                    //把该事件添加到该好友的事件表
                    //参与者变更(把参与者里的自己替换成创建者)
                    singleEvent.setPerson(singleEvent.getPerson().replace(userId, eventCreatorChooseVo.getUserId()));
                    eventMapper.uploadingEvents(singleEvent);
                    //通知该好友事件已修改
                    TxtMessage txtMessage = new TxtMessage(singleEvent.getEventname() + "事件已修改为：" + "：" + different.replace(different.length() - 1, different.length(), "。"), "");
                    System.out.println("消息内容：" + txtMessage.getContent());
                    try {
                        String[] targetId = {userId};
                        ResponseResult result = rongCloudMethodUtil.sendSystemMessage(eventCreatorChooseVo.getUserId(), targetId, txtMessage, "", "");
                        if (result.getCode() != 200) {
                            return DtoUtil.getFalseDto("发送消息失败", 17002);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return DtoUtil.getFalseDto("消息发送失败", 26002);
                    }
                }
                //不冲突直接添加
                //参与者变更(把参与者里的自己替换成创建者)
                singleEvent.setPerson(singleEvent.getPerson().replace(userId, eventCreatorChooseVo.getUserId()));
                eventMapper.uploadingEvents(singleEvent);
                //在事件副表插入创建者
                SingleEventVice singleEventVice1 = new SingleEventVice();
                singleEventVice1.setCreateBy(Long.parseLong(eventCreatorChooseVo.getUserId()));
                singleEventVice1.setUserId(singleEvent.getUserid());
                singleEventVice1.setEventId(singleEvent.getEventid());
                eventViceMapper.createEventVice(singleEventVice1);
                //通知该好友事件已修改
                TxtMessage txtMessage = new TxtMessage(singleEvent.getEventname() + "事件已修改为：" + "：" + different.replace(different.length() - 1, different.length(), "。"), "");
                System.out.println("消息内容：" + txtMessage.getContent());
                try {
                    String[] targetId = {userId};
                    ResponseResult result = rongCloudMethodUtil.sendSystemMessage(eventCreatorChooseVo.getUserId(), targetId, txtMessage, "", "");
                    if (result.getCode() != 200) {
                        return DtoUtil.getFalseDto("发送消息失败", 17002);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return DtoUtil.getFalseDto("消息发送失败", 26002);
                }
            }
            return DtoUtil.getSuccessDto("消息发送成功", 100000);
        } else {
            //不保留
            //删除该事件
            boolean result = stringRedisTemplate.delete(singleEvent.getUserid().toString() + singleEvent.getEventid().toString());
            if (!result) {
                return DtoUtil.getFalseDto("删除失败", 21014);
            }
            //通知被邀请者
            TxtMessage txtMessage = new TxtMessage("修改事件" + singleEventOld.getEventname() + "已被取消", "");
            System.out.println("创建者选择消息内容：" + txtMessage.getContent());
            try {
                ResponseResult result1 = rongCloudMethodUtil.sendSystemMessage(eventCreatorChooseVo.getUserId(), persons, txtMessage, "", "");
                if (result1.getCode() != 200) {
                    return DtoUtil.getFalseDto("发送消息失败", 17002);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return DtoUtil.getFalseDto("消息发送失败", 26002);
            }
        }
        return DtoUtil.getSuccessDto("消息发送成功", 100000);
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
            if (receivedDeleteEventIds.getDeleteType().equals("0")) {
                receivedDeleteEventIds.setDeleteType("singleevent");
            }
            if (receivedDeleteEventIds.getDeleteType().equals("1")) {
                receivedDeleteEventIds.setDeleteType("draft");
            }
            for (Long l : receivedDeleteEventIds.getEventIds()) {
                if (eventMapper.deleteByDeleteType(l, receivedDeleteEventIds.getDeleteType(), receivedDeleteEventIds.getUserId()) == 0) {
                    return DtoUtil.getFalseDto("删除失败", 21016);
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
        if (singleEvent != null) {
            return DtoUtil.getSuccesWithDataDto("查询成功", SingleEventUtil.getShowSingleEvent(singleEvent), 100000);
        }
        return DtoUtil.getSuccessDto("未查询到事件", 200000);
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
            return DtoUtil.getFalseDto("请重新登录", 21013);
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

    /**
     * 变更邀请事件成员
     *
     * @param updatePersonsVo
     * @param token
     * @return
     */
    @Override
    public Dto updateInvitePerson(UpdatePersonsVo updatePersonsVo, String token) {
        if (StringUtils.isEmpty(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        if (ObjectUtils.isEmpty(updatePersonsVo)) {
            return DtoUtil.getFalseDto("数据未获取到", 26001);
        }
        if (StringUtils.isEmpty(updatePersonsVo.getUserId())) {
            return DtoUtil.getFalseDto("userId不能为空", 21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(updatePersonsVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21013);
        }
        SingleEvent singleEvent = eventMapper.queryEventOne(updatePersonsVo.getUserId(), updatePersonsVo.getEventId());
        if (ObjectUtils.isEmpty(singleEvent)) {
            return DtoUtil.getFalseDto("事件不存在", 23333);
        }
        //修改后的成员
        String[] newPersons = updatePersonsVo.getUpdPersons().split(",");
        //修改前的成员
        String[] oldPersons = singleEvent.getPerson().split(",");
        List<String> list1 = Arrays.asList(newPersons);
        List<String> list2 = Arrays.asList(oldPersons);
        //被移除的成员的集合
        List<String> result1 = new ArrayList<String>();
        //新添加的成员集合
        List<String> result2 = new ArrayList<String>();
        for (String old : oldPersons) {
            //判断是否包含
            if (!list1.contains(old)) {
                result1.add(old);
            }
        }
        for (String newP : newPersons) {
            //判断是否包含
            if (!list2.contains(newP)) {
                result2.add(newP);
            }
        }
        String[] targetId;
        if (result2.size() > 0) {
            //给新添加的成员发送邀请消息
            targetId = (String[]) result2.toArray();

        }

        if (result1.size() > 0) {
            //给被移除的成员发送提醒消息
            targetId = (String[]) result1.toArray();
        }
        return DtoUtil.getFalseDto("没有成员发生变动", 23334);
    }

    /**
     * 变更支持事件成员
     *
     * @param updatePersonsVo
     * @param token
     * @return
     */
    @Override
    public Dto updateBackers(UpdatePersonsVo updatePersonsVo, String token) {
        if (StringUtils.isEmpty(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        if (ObjectUtils.isEmpty(updatePersonsVo)) {
            return DtoUtil.getFalseDto("数据未获取到", 26001);
        }
        if (StringUtils.isEmpty(updatePersonsVo.getUserId())) {
            return DtoUtil.getFalseDto("userId不能为空", 21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(updatePersonsVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21013);
        }
        //给新添加的成员发送邀请消息

        //给被移除的成员发送提醒消息
        return null;
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
            return DtoUtil.getFalseDto("请重新登录", 21013);
        }
        MsgStatus msgStatus = msgStatusMapper.queryMsg(queryMsgStatusVo.getMsgId());
        Map<String,String> map=new HashMap<>();
        map.put("id",msgStatus.getId().toString());
        map.put("userId",msgStatus.getUserId().toString());
        map.put("status",msgStatus.getStatus().toString());
        map.put("type",msgStatus.getType().toString());
        if (ObjectUtils.isEmpty(msgStatus)) {
            return DtoUtil.getFalseDto("查询消息状态失败", 200000);
        }
        return DtoUtil.getSuccesWithDataDto("查询消息状态成功", map, 100000);
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
        if (userSettingsMapper.getIsHideFromFriend(receivedFriendEventOnce.getUserId(),receivedFriendEventOnce.getFriendId()) != 2){
            result.put("userPrivatePermission",0);
            return DtoUtil.getSuccesWithDataDto("该用户设置了查看权限",result,200000);
        }else {
            result.put("userPrivatePermission",1);
        }
        SingleEvent singleEvent = eventMapper.queryEventOne(receivedFriendEventOnce.getFriendId(),receivedFriendEventOnce.getEventId());
        if (ObjectUtils.isEmpty(singleEvent)){
            return DtoUtil.getSuccessDto("没有数据",200000);
        }

        result.put("event",SingleEventUtil.getShowSingleEvent(singleEvent));
        return DtoUtil.getSuccesWithDataDto("查询成功",result,100000);
    }

    /**
     * 未完成
     *
     * @return
     */
    private static UserStatistics changeUnfinished(UserStatistics userStatistics, Long num) {
        userStatistics.setUnfinished(num);
        return userStatistics;
    }

    /**
     * 已完成
     *
     * @return
     */
    private static UserStatistics changeCompleted(UserStatistics userStatistics, Long num) {
        userStatistics.setCompleted(num);
        return userStatistics;
    }

    private List<List<ShowSingleEvent>> getShowSingleEventListList(List<SingleEvent> list){
        List<List<ShowSingleEvent>> loopEventList = new ArrayList<>();
        List<ShowSingleEvent> sunShowLoopEventList = new ArrayList<>();
        List<ShowSingleEvent> monShowLoopEventList = new ArrayList<>();
        List<ShowSingleEvent> tueShowLoopEventList = new ArrayList<>();
        List<ShowSingleEvent> wedShowLoopEventList = new ArrayList<>();
        List<ShowSingleEvent> thuShowLoopEventList = new ArrayList<>();
        List<ShowSingleEvent> friShowLoopEventList = new ArrayList<>();
        List<ShowSingleEvent> satShowLoopEventList = new ArrayList<>();
        for (SingleEvent singleEvent1 : list) {
            ShowSingleEvent showSingleEvent = SingleEventUtil.getShowSingleEvent(singleEvent1);
            Boolean[] booleans = showSingleEvent.getRepeaTtime();
            //根据拆分出来的boolean数组进行判断并添加到一周的各个天数中
            for (int i = 0; i <= 6; i++) {
                if (i == 0 && booleans[i]) {
                    sunShowLoopEventList.add(showSingleEvent);
                }
                if (i == 1 && booleans[i]) {
                    monShowLoopEventList.add(showSingleEvent);
                }
                if (i == 2 && booleans[i]) {
                    tueShowLoopEventList.add(showSingleEvent);
                }
                if (i == 3 && booleans[i]) {
                    wedShowLoopEventList.add(showSingleEvent);
                }
                if (i == 4 && booleans[i]) {
                    thuShowLoopEventList.add(showSingleEvent);
                }
                if (i == 5 && booleans[i]) {
                    friShowLoopEventList.add(showSingleEvent);
                }
                if (i == 6 && booleans[i]) {
                    satShowLoopEventList.add(showSingleEvent);
                }
            }
        }
        loopEventList.add(sunShowLoopEventList);
        loopEventList.add(monShowLoopEventList);
        loopEventList.add(tueShowLoopEventList);
        loopEventList.add(wedShowLoopEventList);
        loopEventList.add(thuShowLoopEventList);
        loopEventList.add(friShowLoopEventList);
        loopEventList.add(satShowLoopEventList);
        return loopEventList;
    }

    private List<DayEvents> getDayEventsList(String userId,String condition,String date){
        List<DayEvents> dayEventsList = new ArrayList<>();
        SingleEvent singleEvent;
        for (int i = 0; i <= 6; i++) {
            DayEvents<ShowSingleEvent> dayEvents = new DayEvents();
            String dayEventId = DateUtil.getDay(i,date);
            singleEvent = SingleEventUtil.getSingleEvent(userId, dayEventId);
            List<SingleEvent> singleEventList;
            if ("all".equals(condition)){
                singleEventList = eventMapper.queryEvents(singleEvent);
            }else {
                singleEventList = eventMapper.queryEventsWithFewInfo(singleEvent);
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
}
