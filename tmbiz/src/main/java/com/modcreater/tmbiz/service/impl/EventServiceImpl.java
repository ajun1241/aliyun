package com.modcreater.tmbiz.service.impl;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.pojo.UserStatistics;
import com.modcreater.tmbeans.show.ShowSingleEvent;
import com.modcreater.tmbeans.vo.eventvo.*;
import com.modcreater.tmbeans.vo.uservo.SendInviteMsgVo;
import com.modcreater.tmbiz.service.EventService;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmdao.mapper.AchievementMapper;
import com.modcreater.tmdao.mapper.EventMapper;
import com.modcreater.tmutils.DateUtil;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmutils.SingleEventUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Resource
    private EventMapper eventMapper;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private AchievementMapper achievementMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Dto addNewEvents(UploadingEventVo uploadingEventVo,String token) {
        if (StringUtils.hasText(uploadingEventVo.getUserId())) {
            if (!StringUtils.hasText(token)){
                return DtoUtil.getFalseDto("操作失败,token未获取到",21013);
            }
            System.out.println("我的token"+stringRedisTemplate.opsForValue().get(uploadingEventVo.getUserId()));
            if (!token.equals(stringRedisTemplate.opsForValue().get(uploadingEventVo.getUserId()))){
                return DtoUtil.getFalseDto("token过期请先登录",21014);
            }
            if (StringUtils.hasText(uploadingEventVo.getSingleEvent())) {
                System.out.println("上传" + uploadingEventVo.toString());
                SingleEvent singleEvent = JSONObject.parseObject(uploadingEventVo.getSingleEvent(), SingleEvent.class);
                //对解析得到的SingleEvent进行检测
                Dto dto = SingleEventUtil.isSingleEventStandard(singleEvent);
                if (!ObjectUtils.isEmpty(dto)){
                    return dto;
                }
                singleEvent.setUserid(Long.valueOf(uploadingEventVo.getUserId()));
                //这里开始判断是否是一个重复事件,如果状态值为真,则该事件为重复事件
                if (SingleEventUtil.isLoopEvent(singleEvent.getRepeaTtime())) {
                    singleEvent.setIsLoop(1);
                } else {
                    singleEvent.setIsLoop(0);
                }
                //如果查询Id的数量为0才能继续添加的操作(单一事件)
                System.out.println("singleEvent=======>"+singleEvent.toString());
                if (eventMapper.countIdByDate(singleEvent) != 0){
                    return DtoUtil.getFalseDto("时间段冲突,无法添加",21012);
                }
                if (!ObjectUtils.isEmpty(singleEvent) && eventMapper.uploadingEvents(singleEvent) > 0) {
                    return DtoUtil.getSuccessDto("事件上传成功", 100000);
                }
                return DtoUtil.getFalseDto("事件上传失败", 21001);
            }
            return DtoUtil.getFalseDto("没有可上传的事件", 21002);
        }
        return DtoUtil.getFalseDto("请先登录", 21011);
    }

    @Override
    public Dto deleteEvents(DeleteEventVo deleteEventVo, String token) {
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("操作失败,token未获取到", 21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(deleteEventVo.getUserId()))) {
            return DtoUtil.getFalseDto("token过期请先登录", 21014);
        }
        System.out.println("删除" + deleteEventVo.toString());
        if ("1".equals(deleteEventVo.getEventStatus())) {
            UserStatistics userStatistics = new UserStatistics();
            userStatistics.setCompleted(1L);
            achievementMapper.updateUserStatistics(userStatistics, deleteEventVo.getUserId());
        }
        if (eventMapper.withdrawEventsByUserId(deleteEventVo) > 0) {
            return DtoUtil.getSuccessDto("修改事件状态成功", 100000);
        }
        return DtoUtil.getFalseDto("修改事件状态失败", 21005);
    }


    @Override
    public Dto updateEvents(UpdateEventVo updateEventVo,String token) {
        if (StringUtils.hasText(updateEventVo.getUserId())) {
            if (!StringUtils.hasText(token)){
                return DtoUtil.getFalseDto("操作失败,token未获取到",21013);
            }
            if (!token.equals(stringRedisTemplate.opsForValue().get(updateEventVo.getUserId()))){
                return DtoUtil.getFalseDto("token过期请先登录",21014);
            }
            if (!ObjectUtils.isEmpty(updateEventVo)) {
                System.out.println("修改" + updateEventVo.toString());
                SingleEvent singleEvent = JSONObject.parseObject(updateEventVo.getSingleEvent(), SingleEvent.class);
                //对解析得到的SingleEvent进行检测
                Dto dto = SingleEventUtil.isSingleEventStandard(singleEvent);
                if (!ObjectUtils.isEmpty(dto)){
                    return dto;
                }
                singleEvent.setUserid(Long.valueOf(updateEventVo.getUserId()));
                SingleEvent result = eventMapper.querySingleEventTime(singleEvent);
                if (!(singleEvent.getStarttime().equals(result.getStarttime()) && singleEvent.getEndtime().equals(result.getEndtime()))){
                    if (eventMapper.countIdByDate(singleEvent) != 0){
                        return DtoUtil.getFalseDto("时间段冲突,无法修改",21012);
                    }
                }
                //这里开始判断是否是一个重复事件,如果状态值为真,则该事件为重复事件
                if (SingleEventUtil.isLoopEvent(singleEvent.getRepeaTtime())) {
                    singleEvent.setIsLoop(1);
                    if (eventMapper.alterEventsByUserId(singleEvent) > 0) {
                        return DtoUtil.getSuccessDto("修改成功", 100000);
                    }
                } else {
                    singleEvent.setIsLoop(0);
                    if (eventMapper.alterEventsByUserId(singleEvent) > 0) {
                        return DtoUtil.getSuccessDto("修改成功", 100000);
                    }
                }
                return DtoUtil.getFalseDto("修改事件失败", 21007);
            }
            return DtoUtil.getFalseDto("修改条件接收失败", 21008);
        }
        return DtoUtil.getFalseDto("请先登录", 21011);
    }

    @Override
    public Dto firstUplEvent(SynchronousUpdateVo synchronousUpdateVo,String token) {
        if (ObjectUtils.isEmpty(synchronousUpdateVo)) {
            return DtoUtil.getFalseDto("同步数据未获取到", 26001);
        }
        System.out.println("第一次上传" + synchronousUpdateVo.toString());
        if (StringUtils.isEmpty(synchronousUpdateVo.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("操作失败,token未获取到",21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(synchronousUpdateVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        //判断是否第一次上传
        int ie=eventMapper.queryEventByUserId(synchronousUpdateVo.getUserId());
        List list=eventMapper.queryLoopEvents(synchronousUpdateVo.getUserId());
       /* System.out.println("222222222222222222222222====>"+ie);
        System.out.println("2222222222222222222222222==》"+list.size());*/
        if ( ie> 0 || (list.size()>0)) {
            return DtoUtil.getFalseDto("该用户已经上传过了", 26003);
        }
        boolean flag=false;
        if (!StringUtils.isEmpty(synchronousUpdateVo.getDayEventList())){
            //转换集合
            List<ArrayList> dayEvents=JSONObject.parseObject(synchronousUpdateVo.getDayEventList(),ArrayList.class);
            //上传普通事件
            for (Object dayEventsList:dayEvents) {
                //转换成DayEvents
                DayEvents dayEvents1=JSONObject.parseObject(dayEventsList.toString(),DayEvents.class);
                //把getMySingleEventList()转换成集合
                ArrayList<SingleEvent> singleEventList= JSONObject.parseObject(dayEvents1.getMySingleEventList().toString(),ArrayList.class);
                for (Object singleEvent:singleEventList) {
                    //把遍历出的元素转换成对象
                    SingleEvent singleEvent1=JSONObject.parseObject(singleEvent.toString(),SingleEvent.class);
                    //对解析得到的SingleEvent进行检测
                    Dto dto = SingleEventUtil.isSingleEventStandard(singleEvent1);
                    if (!ObjectUtils.isEmpty(dto)){
                        return dto;
                    }
                    //插入用户id
                    singleEvent1.setUserid(Long.parseLong(synchronousUpdateVo.getUserId()));
                    //上传
                    int uplResult = eventMapper.uploadingEvents(singleEvent1);
                    if (uplResult <= 0) {
                        return DtoUtil.getFalseDto("上传事件"+singleEvent1.getEventid()+"失败", 25005);
                    }
                }
            }
            flag=true;
        }
        if (!StringUtils.isEmpty(synchronousUpdateVo.getLoopEventList())) {
            //外层集合转换
            List<ArrayList> loopEvents = JSONObject.parseObject(synchronousUpdateVo.getLoopEventList(), ArrayList.class);
            //上传重复事件
            for (List<SingleEvent> singleEvents : loopEvents) {
                //第二层转换
                List<SingleEvent> singleEventList=JSONObject.parseObject(singleEvents.toString(),ArrayList.class);
                for (Object loopEvent : singleEventList) {
                    //第三层转换
                    SingleEvent singleEvent=JSONObject.parseObject(loopEvent.toString(),SingleEvent.class);
                    singleEvent.setUserid(Long.parseLong(synchronousUpdateVo.getUserId()));
                    int i = eventMapper.uploadingLoopEvents(singleEvent);
                    if (i <= 0) {
                        return DtoUtil.getFalseDto("上传重复事件" + singleEvent.getEventid() + "失败", 25006);
                    }
                }
            }
            flag=true;
        }
        if (flag){
            return DtoUtil.getSuccessDto("数据同步成功", 100000);
        }else {
            return DtoUtil.getSuccessDto("数据同步失败", 25008);
        }
    }

    @Override
    public Dto uplDraft(DraftVo draftVo, String token) {
        if (ObjectUtils.isEmpty(draftVo)) {
            return DtoUtil.getFalseDto("上传草稿未获取到", 27001);
        }
        //查看草稿是否已存在
        String data = eventMapper.queryDraftByPhone(draftVo.getPhoneNum());
        UserStatistics userStatistics = new UserStatistics();
        StringBuffer dataNum = new StringBuffer(draftVo.getData());
        Long times =0L;
        String condition = "eventid";
        for(int i=0;i<dataNum.length();i++) {
            if(dataNum.indexOf(condition, i)!=-1){
                i=dataNum.indexOf(condition, i);
                times++;
            }
        }
        userStatistics.setUserId(Long.valueOf(draftVo.getUserId()));
        userStatistics.setDrafts(times);
        if (achievementMapper.updateUserStatistics(userStatistics,userStatistics.getUserId().toString()) == 0){
            return DtoUtil.getFalseDto("草稿箱数据计数失败",27004);
        }
        if (StringUtils.isEmpty(data)) {
            //第一次上传草稿
            if (eventMapper.uplDraft(draftVo) <= 0) {
                return DtoUtil.getFalseDto("第一次上传草稿失败", 27002);
            }
        } else {
            //不是第一次上传
            if (eventMapper.updateDraft(draftVo) <= 0) {
                return DtoUtil.getFalseDto("非第一次上传草稿失败", 27003);
            }
        }
        return DtoUtil.getSuccessDto("上传草稿成功", 100000);
    }

    @Override
    public Dto searchByDayEventIds(SearchEventVo searchEventVo, String token) {
        if (StringUtils.hasText(searchEventVo.getUserId())) {
            if (!StringUtils.hasText(token)){
                return DtoUtil.getFalseDto("操作失败,token未获取到",21013);
            }
            if (!token.equals(stringRedisTemplate.opsForValue().get(searchEventVo.getUserId()))){
                return DtoUtil.getFalseDto("token过期请先登录",21014);
            }
            if (StringUtils.hasText(searchEventVo.getDayEventId())) {
                System.out.println("按天查" + searchEventVo.toString());
                /*boolean singleResult = false;
                boolean loopResult = false;*/
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
//                    singleResult = true;
                }
                dayEvents.setUserId(singleEvent.getUserid().intValue());
                dayEvents.setTotalNum(singleEventList.size());
                dayEvents.setDayEventId(Integer.valueOf(searchEventVo.getDayEventId()));
                dayEvents.setMySingleEventList(showSingleEventList);

                /**
                 * 查询重复事件
                 */
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
//                            loopResult = true;
                        }
                    }
                }
                /**
                 * 将得到的数据封装到map作为返回
                 */
                Map<String, Object> result = new HashMap<>(3);
                result.put("ShowSingleEventListOrderByLevel", showSingleEventListOrderByLevel);
                result.put("ShowSingleEventListOrderByLevelAndDate", showSingleEventListOrderByLevelAndDate);
                result.put("dayEvents", dayEvents);
                if (!ObjectUtils.isEmpty(dayEvents) ) {
                    return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
                }
                return DtoUtil.getFalseDto("查询失败,没有数据", 200000);
            }
            return DtoUtil.getFalseDto("查询条件接收失败", 21004);
        }
        return DtoUtil.getFalseDto("请先登录", 21011);
    }

    @Override
    public Dto searchByDayEventIdsInMonth(SearchEventVo searchEventVo,String token) {
        if (StringUtils.hasText(searchEventVo.getUserId())) {
            if (!StringUtils.hasText(token)){
                return DtoUtil.getFalseDto("操作失败,token未获取到",21013);
            }
            if (!token.equals(stringRedisTemplate.opsForValue().get(searchEventVo.getUserId()))){
                return DtoUtil.getFalseDto("token过期请先登录",21014);
            }
            if (StringUtils.hasText(searchEventVo.getDayEventId())) {
                System.out.println("按月查" + searchEventVo.toString());
                //用户操作界面,记录时间
                //查询上一次用户操作过的时间
                Long lastUserStatisticsDate = achievementMapper.queryUserStatisticsDate(searchEventVo.getUserId());
                //生成系统时间
                Long now = System.currentTimeMillis();
                UserStatistics userStatistics = new UserStatistics();
                userStatistics.setLastOperatedTime(now);
                System.out.println("()()()()()()("+now.toString());
                //将本次操作的事件更新到用户统计表
                achievementMapper.updateUserStatistics(userStatistics,searchEventVo.getUserId());
                //如果当前操作时间与用户上一次操作的时间的差值大于一天则更改用户统计表中的登录天数
                if ((now - lastUserStatisticsDate) >= 86400000){
                    UserStatistics userStatisticsForLogin = new UserStatistics();
                    userStatisticsForLogin.setLoggedDays(1L);
                    achievementMapper.updateUserStatistics(userStatisticsForLogin,searchEventVo.getUserId());
                }

                SingleEvent singleEvent = SingleEventUtil.getSingleEvent(searchEventVo.getUserId(), searchEventVo.getDayEventId());
                //查询在该月内存在事件的日的集合
                List<Integer> days = eventMapper.queryDays(singleEvent);
                List<DayEvents<ShowSingleEvent>> dayEventsList = new ArrayList<>();
                if (days.size() != 0) {
                    for (Integer day : days) {
                        singleEvent.setDay(day.longValue());
                        ArrayList<SingleEvent> singleEventList = eventMapper.queryEvents(singleEvent);
                        if (singleEventList.size() != 0) {
                            ArrayList<ShowSingleEvent> showSingleEventList = (ArrayList<ShowSingleEvent>) SingleEventUtil.getShowSingleEventList(singleEventList);
                            DayEvents<ShowSingleEvent> dayEvents = new DayEvents<>();
                            dayEvents.setUserId(singleEvent.getUserid().intValue());
                            dayEvents.setTotalNum(singleEventList.size());
                            dayEvents.setDayEventId(Integer.valueOf(singleEvent.getYear().toString() + singleEvent.getMonth().toString() + singleEvent.getDay().toString()));
                            dayEvents.setMySingleEventList(showSingleEventList);
                            dayEventsList.add(dayEvents);
                        }
                    }
                    return DtoUtil.getSuccesWithDataDto("查询成功", dayEventsList, 100000);
                }
                return DtoUtil.getFalseDto("查询失败,没有数据", 200000);
            }
            return DtoUtil.getFalseDto("查询条件接收失败", 21004);
        }
        return DtoUtil.getFalseDto("请先登录", 21011);
    }

    @Override
    public Dto searchByDayEventIdsInWeek(SearchEventVo searchEventVo,String token) {
        if (StringUtils.hasText(searchEventVo.getUserId())) {
            if (!StringUtils.hasText(token)){
                return DtoUtil.getFalseDto("操作失败,token未获取到",21013);
            }
            if (!token.equals(stringRedisTemplate.opsForValue().get(searchEventVo.getUserId()))){
                return DtoUtil.getFalseDto("token过期请先登录",21014);
            }
            if (!ObjectUtils.isEmpty(searchEventVo)) {
                System.out.println("按周查" + searchEventVo.toString());
                SingleEvent singleEvent;
                //按周查询单一事件
                List<DayEvents> dayEventsList = new ArrayList<>();
                for (int i = 0; i <= 6; i++) {
                    DayEvents<ShowSingleEvent> dayEvents = new DayEvents();
                    String dayEventId = String.valueOf(Integer.valueOf(searchEventVo.getDayEventId()) + i);
                    singleEvent = SingleEventUtil.getSingleEvent(searchEventVo.getUserId(), dayEventId);
                    List<SingleEvent> singleEventList = eventMapper.queryByWeekOrderByStartTime(singleEvent);
                    ArrayList<ShowSingleEvent> showSingleEventList = (ArrayList<ShowSingleEvent>) SingleEventUtil.getShowSingleEventList(singleEventList);
                    dayEvents.setMySingleEventList(showSingleEventList);
                    dayEvents.setTotalNum(dayEvents.getMySingleEventList().size());
                    dayEvents.setUserId(Integer.valueOf(searchEventVo.getUserId()));
                    dayEvents.setDayEventId(Integer.valueOf(dayEventId));
                    dayEventsList.add(dayEvents);
                }
                //按周查询重复事件
                List<SingleEvent> loopEventListInDataBase = eventMapper.queryLoopEvents(searchEventVo.getUserId());
                List<List<ShowSingleEvent>> loopEventList = new ArrayList<>();
                //创建七个几个代表一周七天
                List<ShowSingleEvent> sunShowLoopEventList = new ArrayList<>();
                List<ShowSingleEvent> monShowLoopEventList = new ArrayList<>();
                List<ShowSingleEvent> tueShowLoopEventList = new ArrayList<>();
                List<ShowSingleEvent> wedShowLoopEventList = new ArrayList<>();
                List<ShowSingleEvent> thuShowLoopEventList = new ArrayList<>();
                List<ShowSingleEvent> friShowLoopEventList = new ArrayList<>();
                List<ShowSingleEvent> satShowLoopEventList = new ArrayList<>();
                for (SingleEvent singleEvent1 : loopEventListInDataBase) {
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
                Map<String,Object> result = new HashMap<>(2);
                result.put("dayEventsList", dayEventsList);
                result.put("loopEventList", loopEventList);
                return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
            }
            return DtoUtil.getFalseDto("查询条件接收失败", 21004);
        }
        return DtoUtil.getFalseDto("请先登录", 21011);
    }


    /**
     * 发送好友邀请消息
     * @param sendInviteMsgVo
     * @param token
     * @return
     */
    @Override
    public Dto sendInviteMsg(SendInviteMsgVo sendInviteMsgVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        System.out.println("接受请求"+sendInviteMsgVo.toString());
        if (StringUtils.isEmpty(sendInviteMsgVo.getUserId())|| (sendInviteMsgVo.getFriendIds().length>0)){
            return DtoUtil.getFalseDto("userId和friendId不能为空",17001);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(sendInviteMsgVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        //发送邀请消息给好友

        return null;
    }

    /**
     * 发送好友接受邀请消息
     * @param sendInviteMsgVo
     * @param token
     * @return
     */
    @Override
    public Dto sendInviteAcceptMsg(SendInviteMsgVo sendInviteMsgVo, String token) {
        //计入统计
        //
        return null;
    }

}
