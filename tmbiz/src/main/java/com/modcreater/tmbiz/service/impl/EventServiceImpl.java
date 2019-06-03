package com.modcreater.tmbiz.service.impl;

import com.alibaba.fastjson.JSON;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.*;
import com.modcreater.tmbeans.show.ShowSingleEvent;
import com.modcreater.tmbeans.vo.eventvo.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedDeleteEventIds;
import com.modcreater.tmbiz.config.TimerConfig;
import com.modcreater.tmbiz.service.EventService;
import com.modcreater.tmdao.mapper.*;
import com.modcreater.tmutils.*;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

/*    @Resource
    private TimerConfig timerConfig;*/

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
        //判断上传有没有数据



        //判断是否第一次上传
        int ie=eventMapper.queryEventByUserId(synchronousUpdateVo.getUserId());
        if ( ie> 0 ) {
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
                    singleEvent1.setIsLoop(0);
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
                    singleEvent.setIsLoop(1);
                    int i = eventMapper.uploadingEvents(singleEvent);
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
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (StringUtils.isEmpty(draftVo.getUserId())){
            return DtoUtil.getFalseDto("userId不能为空",21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(draftVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21013);
        }
        ArrayList<Object> drafts=JSONObject.parseObject(draftVo.getSingleEvents(),ArrayList.class);
        for (Object draft:drafts) {
            System.out.println(draft);
            SingleEvent draft1=JSONObject.parseObject(draft.toString(),SingleEvent.class);
            //查看草稿是否已存在
            if (eventMapper.queryDraftCount(draftVo.getUserId(),draft1.getEventid().toString())==0){
                //上传
                draft1.setUserid(Long.parseLong(draftVo.getUserId()));
                if (SingleEventUtil.isLoopEvent(draft1.getRepeaTtime())) {
                    draft1.setIsLoop(1);
                } else {
                    draft1.setIsLoop(0);
                }
                if(eventMapper.uplDraft(draft1)==0){
                    return DtoUtil.getFalseDto("上传草稿失败",27002);
                }
            }else {
                return DtoUtil.getFalseDto("该草稿已存在",27003);
            }
        }

        UserStatistics userStatistics = new UserStatistics();
        StringBuffer dataNum = new StringBuffer(draftVo.getSingleEvents());
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
        return DtoUtil.getSuccessDto("上传草稿成功", 100000);
    }

    /**
     * 修改一个草稿
     * @param addInviteEventVo
     * @param token
     * @return
     */
    @Override
    public Dto updDraft(AddInviteEventVo addInviteEventVo, String token) {
        if (ObjectUtils.isEmpty(addInviteEventVo)) {
            return DtoUtil.getFalseDto("修改草稿未获取到", 27001);
        }
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (StringUtils.isEmpty(addInviteEventVo.getUserId())){
            return DtoUtil.getFalseDto("userId不能为空",21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(addInviteEventVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21013);
        }
        SingleEvent singleEvent=JSONObject.parseObject(addInviteEventVo.getSingleEvent(),SingleEvent.class);
        System.out.println("修改草稿:"+singleEvent.toString());
        if (ObjectUtils.isEmpty(singleEvent)){
            return DtoUtil.getFalseDto("获取草稿失败",21111);
        }
        if (eventMapper.updateDraft(singleEvent)==0){
            return DtoUtil.getFalseDto("修改草稿失败",21112);
        }
        return DtoUtil.getSuccessDto("修改草稿成功",10000);
    }


    @Override
    public Dto searchByDayEventIds(SearchEventVo searchEventVo, String token) {
        if (StringUtils.hasText(searchEventVo.getUserId())) {
            if (!StringUtils.hasText(token)){
                return DtoUtil.getFalseDto("操作失败,token未获取到",21013);
            }
            System.out.println("收到的数据"+searchEventVo.toString());
            System.out.println("这是我收到的token*****************************>"+token);
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
                return DtoUtil.getSuccessDto("没有数据", 200000);
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
                            String month = singleEvent.getMonth().toString();
                            String day1 = singleEvent.getDay().toString();
                            if (month.length() == 1){
                                month = "0" + month;
                            }
                            if (day1.length() == 1){
                                day1 = "0" + day1;
                            }
                            dayEvents.setDayEventId(Integer.valueOf(singleEvent.getYear().toString() + month +day1));
                            dayEvents.setMySingleEventList(showSingleEventList);
                            dayEventsList.add(dayEvents);
                        }
                    }
                    return DtoUtil.getSuccesWithDataDto("查询成功", dayEventsList, 100000);
                }
                return DtoUtil.getSuccessDto("没有数据", 200000);
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
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(simpleDateFormat.parse(searchEventVo.getDayEventId()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i <= 6; i++) {
                    DayEvents<ShowSingleEvent> dayEvents = new DayEvents();
                    if (i != 0) {
                        calendar.add(Calendar.DATE, 1);
                    }
                    String dayEventId = simpleDateFormat.format(calendar.getTime());
                    singleEvent = SingleEventUtil.getSingleEvent(searchEventVo.getUserId(), dayEventId);
                    List<SingleEvent> singleEventList = eventMapper.queryEvents(singleEvent);
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
                if ((dayEventsList.size() + loopEventList.size()) == 0){
                    return DtoUtil.getSuccessDto("没有数据", 200000);
                }
                Map<String,Object> result = new HashMap<>(2);
                result.put("dayEventsList", dayEventsList);
                result.put("loopEventList", loopEventList);
                return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
            }
            return DtoUtil.getFalseDto("查询条件接收失败", 21004);
        }
        return DtoUtil.getFalseDto("请先登录", 21011);
    }

    @Override
    public Dto seaByWeekWithPrivatePermission(SearchEventVo searchEventVo,String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("操作失败,token未获取到",21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(searchEventVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        if (!ObjectUtils.isEmpty(searchEventVo)) {
            System.out.println("按周查" + searchEventVo.toString());
            Map<String,Object> result = new HashMap<>(3);
            if (userSettingsMapper.getFriendHide(searchEventVo.getFriendId()) == 0 || userSettingsMapper.getIsHideFromFriend(searchEventVo.getUserId(),searchEventVo.getFriendId()) == 1){
                result.put("userPrivatePermission",1);
            }else {
                result.put("userPrivatePermission",0);
                return DtoUtil.getSuccesWithDataDto("该用户设置了查看权限",result,100000);
            }
            SingleEvent singleEvent;
            //按周查询单一事件
            List<DayEvents> dayEventsList = new ArrayList<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(simpleDateFormat.parse(searchEventVo.getDayEventId()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for (int i = 0; i <= 6; i++) {
                DayEvents<ShowSingleEvent> dayEvents = new DayEvents();
                if (i != 0) {
                    calendar.add(Calendar.DATE, 1);
                }
                String dayEventId = simpleDateFormat.format(calendar.getTime());
                singleEvent = SingleEventUtil.getSingleEvent(searchEventVo.getUserId(), dayEventId);
                singleEvent.setUserid(Long.parseLong(searchEventVo.getFriendId()));
                List<SingleEvent> singleEventList = eventMapper.queryEvents(singleEvent);
                ArrayList<ShowSingleEvent> showSingleEventList = (ArrayList<ShowSingleEvent>) SingleEventUtil.getShowSingleEventList(singleEventList);
                dayEvents.setMySingleEventList(showSingleEventList);
                dayEvents.setTotalNum(dayEvents.getMySingleEventList().size());
                dayEvents.setUserId(Integer.valueOf(searchEventVo.getUserId()));
                dayEvents.setDayEventId(Integer.valueOf(dayEventId));
                dayEventsList.add(dayEvents);
            }
            //按周查询重复事件
            List<SingleEvent> loopEventListInDataBase = eventMapper.queryLoopEvents(searchEventVo.getFriendId());
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
            if ((dayEventsList.size() + loopEventList.size()) == 0){
                return DtoUtil.getSuccessDto("没有数据", 200000);
            }
            result.put("dayEventsList", dayEventsList);
            result.put("loopEventList", loopEventList);
            return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
        }
        return DtoUtil.getFalseDto("查询条件接收失败", 21004);
    }

    @Override
    public Dto searchOnce(ReceivedSearchOnce receivedSearchOnce, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("操作失败,token未获取到",21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedSearchOnce.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        SingleEvent singleEvent = eventMapper.queryEventOne(receivedSearchOnce.getUserId(),receivedSearchOnce.getEventId());
        if (singleEvent != null){
            return DtoUtil.getSuccesWithDataDto("查询成功",SingleEventUtil.getShowSingleEvent(singleEvent),100000);
        }
        return DtoUtil.getSuccessDto("未查询到事件",200000);
    }

    /**
     * 添加事件支持者
     * @param addbackerVo
     * @param token
     * @return
     */
    @Override
    public Dto addEventBacker(AddBackerVo addbackerVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(addbackerVo)){
            return DtoUtil.getFalseDto("添加邀请事件数据未获取到",26001);
        }
        if (StringUtils.isEmpty(addbackerVo.getUserId())){
            return DtoUtil.getFalseDto("userId不能为空",21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(addbackerVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21013);
        }
        if (StringUtils.isEmpty(addbackerVo.getFriendIds())){
            return DtoUtil.getFalseDto("支持者不能空",25002);
        }
        String[] backers=addbackerVo.getFriendIds().split(",");
        //添加事件进数据库
        SingleEvent singleEvent=JSONObject.parseObject(addbackerVo.getSingleEvent(),SingleEvent.class);
        singleEvent.setUserid(Long.parseLong(addbackerVo.getUserId()));
        //事件时间冲突判断
        if (eventMapper.countIdByDate(singleEvent) != 0){
            return DtoUtil.getFalseDto("时间段冲突,无法添加",21012);
        }
        if (eventMapper.uploadingEvents(singleEvent)==0){
            return DtoUtil.getFalseDto("事件添加失败",25001);
        }
        //添加支持者状态
        backerMapper.addBackers(addbackerVo.getUserId(),backers,singleEvent.getEventid().toString());
        //发送信息给被邀请者
        try {
            Account account=accountMapper.queryAccount(addbackerVo.getUserId());
            RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
            String content=account.getUserName()+"邀请你支持他在"+singleEvent.getMonth()+"月"+singleEvent.getDay()+"日"+"的"+singleEvent.getEventname()+"活动;"+"时间"+Integer.parseInt(singleEvent.getStarttime())/60+":"+Integer.parseInt(singleEvent.getStarttime())%60+"至"+Integer.parseInt(singleEvent.getEndtime())/60+":"+Integer.parseInt(singleEvent.getEndtime())%60+"；你将在事件开始前"+singleEvent.getRemindTime()+"收到提醒。";
            System.out.println("消息内容"+content);
            InviteMessage inviteMessage=new InviteMessage(content,"",addbackerVo.getSingleEvent());
            ResponseResult result=rongCloudMethodUtil.sendSystemMessage(addbackerVo.getUserId(),backers,inviteMessage,"","");
            if (result.getCode()!=200){
                return DtoUtil.getFalseDto("发送消息失败",17002);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("消息发送出错",26002);
        }
        return DtoUtil.getSuccessDto("消息发送成功",100000);
    }

    /**
     * 回应事件支持
     * @return
     */
    @Override
    public Dto feedbackEventBacker(FeedbackEventBackerVo feedbackEventBackerVo,String token) {
        try {
            if (StringUtils.isEmpty(token)){
                return DtoUtil.getFalseDto("token未获取到",21013);
            }
            if (ObjectUtils.isEmpty(feedbackEventBackerVo)){
                return DtoUtil.getFalseDto("添加邀请事件数据未获取到",26001);
            }
            if (StringUtils.isEmpty(feedbackEventBackerVo.getUserId())){
                return DtoUtil.getFalseDto("userId不能为空",21011);
            }
            if (!token.equals(stringRedisTemplate.opsForValue().get(feedbackEventBackerVo.getUserId()))){
                return DtoUtil.getFalseDto("token过期请先登录",21013);
            }
            //拿到发起者的事件
            SingleEvent singleEvent=JSONObject.parseObject(feedbackEventBackerVo.getExtraData(),SingleEvent.class);
            Backers backers=new Backers();
            //判断事件是否过期
            SingleEvent singleEventOld=eventMapper.queryEventOne(singleEvent.getUserid().toString(),singleEvent.getEventid().toString());
            if (ObjectUtils.isEmpty(singleEventOld)){
                return DtoUtil.getFalseDto("该事件已过期",29003);
            }
            //同意
            if (Long.parseLong(feedbackEventBackerVo.getChoose())==1){
                //更改backer表状态
                backers.setUserId(singleEvent.getUserid());
                backers.setEventId(singleEvent.getEventid());
                backers.setBackerId(Long.parseLong(feedbackEventBackerVo.getUserId()));
                backers.setStatus(1L);
                if (backerMapper.updateBackerStatus(backers)==0){
                    return DtoUtil.getFalseDto("回应状态修改失败",29001);
                }
                //发送消息给发起者
                RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
                Account account=accountMapper.queryAccount(feedbackEventBackerVo.getUserId());
                if (ObjectUtils.isEmpty(account)){
                    return DtoUtil.getFalseDto("查询用户失败",29002);
                }
                InviteMessage inviteMessage=new InviteMessage(account.getUserName()+"同意了你的邀请，成为"+singleEvent.getEventname()+"事件的支持者。","",feedbackEventBackerVo.getExtraData());
                System.out.println("消息内容："+inviteMessage.getContent());
                String[] targetId={singleEvent.getUserid().toString()};
                ResponseResult result=rongCloudMethodUtil.sendSystemMessage(feedbackEventBackerVo.getUserId(),targetId,inviteMessage,"","");
                if (result.getCode()!=200){
                    return DtoUtil.getFalseDto("发送消息失败",17002);
                }

                //设置定时给支持者发信息
                String dateFormat=singleEvent.getYear()+"-"+singleEvent.getMonth()+"-"+singleEvent.getDay()+" "+Long.parseLong(singleEvent.getStarttime())/60+"-"+(Long.parseLong(singleEvent.getStarttime())%60L+Long.parseLong(singleEvent.getRemindTime()))+"-00";
                System.out.println("提醒时间："+dateFormat);
                /*timerConfig.setDate(dateFormat);
                timerConfig.setUserId(feedbackEventBackerVo.getUserId());
                timerConfig.setFriendId(feedbackEventBackerVo.getUserId());
                timerConfig.setContent("你支持的事件"+singleEvent.getEventname()+"将在"+singleEvent.getMonth()+"月"+singleEvent.getDay()+"日"+Long.parseLong(singleEvent.getStarttime())/60+"："+Long.parseLong(singleEvent.getStarttime())%60+"开始");*/
//                TimerUtil timerUtil=new TimerUtil();
//                timerUtil.setTimer(/*sdf.parse(dateFormat)*/);


            }else if (Long.parseLong(feedbackEventBackerVo.getChoose())==2){
                //拒绝
                //更改backer表状态
                backers.setUserId(singleEvent.getUserid());
                backers.setEventId(singleEvent.getEventid());
                backers.setBackerId(Long.parseLong(feedbackEventBackerVo.getUserId()));
                backers.setStatus(2L);
                if (backerMapper.updateBackerStatus(backers)==0){
                    return DtoUtil.getFalseDto("回应状态修改失败",29001);
                }
                //发送消息给发起者
                RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
                Account account=accountMapper.queryAccount(feedbackEventBackerVo.getUserId());
                if (ObjectUtils.isEmpty(account)){
                    return DtoUtil.getFalseDto("查询用户失败",29002);
                }
                InviteMessage inviteMessage=new InviteMessage(account.getUserName()+"拒绝了你"+singleEvent.getEventname()+"事件的邀请。","",feedbackEventBackerVo.getExtraData());
                System.out.println("消息内容："+inviteMessage.getContent());
                String[] targetId={singleEvent.getUserid().toString()};
                ResponseResult result=rongCloudMethodUtil.sendSystemMessage(feedbackEventBackerVo.getUserId(),targetId,inviteMessage,"","");
                if (result.getCode()!=200){
                    return DtoUtil.getFalseDto("发送消息失败",17002);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getFalseDto("消息发送失败",26002);
        }
        return DtoUtil.getSuccessDto("消息发送成功",100000);
    }

    /**
     * 修改支持事件
     * @param addbackerVo
     * @param token
     * @return
     */
    @Override
    public Dto updBackerEvent(AddBackerVo addbackerVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(addbackerVo)){
            return DtoUtil.getFalseDto("添加邀请事件数据未获取到",26001);
        }
        if (StringUtils.isEmpty(addbackerVo.getUserId())){
            return DtoUtil.getFalseDto("userId不能为空",21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(addbackerVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21013);
        }
        //拿到新事件和支持者列表
        SingleEvent singleEvent=JSONObject.parseObject(addbackerVo.getSingleEvent(),SingleEvent.class);
        //拿到旧事件和支持者列表
        SingleEvent singleEventOld=eventMapper.queryEventOne(singleEvent.getUserid().toString(),singleEvent.getEventid().toString());
        //修改事件
        if (eventMapper.alterEventsByUserId(singleEvent)<=0){
            return DtoUtil.getFalseDto("修改事件失败",211111);
        }
        //把修改内容发送给支持者
        String[] backers=backerMapper.queryBackers(singleEventOld.getUserid().toString(),singleEventOld.getEventid().toString()).toString().replace("[","").replace("]","").split(",");
        System.out.println("支持者："+backers);
        StringBuffer different=SingleEventUtil.eventDifferent(SingleEvent.toMap(singleEvent),SingleEvent.toMap(singleEventOld));
        //发送消息给支持者
        RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
        Account account=accountMapper.queryAccount(addbackerVo.getUserId());
        if (ObjectUtils.isEmpty(account)){
            return DtoUtil.getFalseDto("查询用户失败",29002);
        }
        InviteMessage inviteMessage=new InviteMessage(account.getUserName()+"修改了事件"+singleEvent.getEventname()+"，修改内容为："+different.replace(different.length()-1,different.length(),"。"),"","");
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
        }
        //定时器修改




        return DtoUtil.getSuccessDto("修改事件成功",100000);
    }

    /**
     * 删除支持事件
     * @param deleteEventVo
     * @param token
     * @return
     */
    @Override
    public Dto delBackerEvent(DeleteEventVo deleteEventVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(deleteEventVo)){
            return DtoUtil.getFalseDto("添加邀请事件数据未获取到",26001);
        }
        if (StringUtils.isEmpty(deleteEventVo.getUserId())){
            return DtoUtil.getFalseDto("userId不能为空",21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(deleteEventVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21013);
        }
        SingleEvent singleEvent=eventMapper.queryEventOne(deleteEventVo.getUserId(),deleteEventVo.getEventId());
        //删除事件表
        if (eventMapper.deleteByDeleteType(Long.parseLong(deleteEventVo.getEventId()),"singleevent",deleteEventVo.getUserId())<=0){
            return DtoUtil.getFalseDto("删除事件失败",22222);
        }
        //告知支持者
        String[] backers=backerMapper.queryBackers(deleteEventVo.getUserId(),deleteEventVo.getEventId()).toString().replace("[","").replace("]","").split(",");
        System.out.println("支持者："+backers);
        //发送消息给支持者
        RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
        Account account=accountMapper.queryAccount(deleteEventVo.getUserId());
        if (ObjectUtils.isEmpty(account)){
            return DtoUtil.getFalseDto("查询用户失败",29002);
        }
        InviteMessage inviteMessage=new InviteMessage(account.getUserName()+"删除了事件"+singleEvent.getEventname(),"","");
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
        }
        return DtoUtil.getSuccessDto("删除事件成功",100000);
    }

    /**
     * 添加一条邀请事件
     * @param addInviteEventVo
     * @param token
     * @return
     */
    @Override
    public Dto addInviteEvent(AddInviteEventVo addInviteEventVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(addInviteEventVo)){
            return DtoUtil.getFalseDto("添加邀请事件数据未获取到",26001);
        }
        if (StringUtils.isEmpty(addInviteEventVo.getUserId())){
            return DtoUtil.getFalseDto("userId不能为空",21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(addInviteEventVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21013);
        }
        //保存这条事件
        SingleEvent singleEvent=JSONObject.parseObject(addInviteEventVo.getSingleEvent(),SingleEvent.class);
        singleEvent.setUserid(Long.parseLong(addInviteEventVo.getUserId()));
        //判断是否有冲突事件
        int y=eventMapper.countIdByDate(singleEvent);
        boolean m=stringRedisTemplate.hasKey(addInviteEventVo.getUserId()+singleEvent.getEventid().toString());
        if (y>0 || m){
            return DtoUtil.getFalseDto("该时间段内已有事件不能添加",21012);
        }
        String[] persons=singleEvent.getPerson().split(",");
        String redisKey=addInviteEventVo.getUserId()+singleEvent.getEventid();
        stringRedisTemplate.opsForValue().set(redisKey,JSON.toJSONString(singleEvent));
        //生成统计表
        List<StatisticsTable> tables=new ArrayList<>();
        for (String userId:persons) {
            StatisticsTable statisticsTable=new StatisticsTable();
            statisticsTable.setCreatorId(Long.parseLong(addInviteEventVo.getUserId()));
            statisticsTable.setEventId(singleEvent.getEventid());
            statisticsTable.setUserId(Long.parseLong(userId));
            tables.add(statisticsTable);
        }
        int i=statisticsMapper.createStatistics(tables);
        if (i<=0){
            return DtoUtil.getFalseDto("生成统计表失败",26003);
        }
        //向被邀请者发送信息
        try {
            Account account=accountMapper.queryAccount(addInviteEventVo.getUserId());
            RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
            String content=account.getUserName()+"邀请你参加"+singleEvent.getMonth()+"月"+singleEvent.getDay()+"日"+"的"+singleEvent.getEventname()+"活动;"+"时间"+Integer.parseInt(singleEvent.getStarttime())/60+":"+Integer.parseInt(singleEvent.getStarttime())%60+"至"+Integer.parseInt(singleEvent.getEndtime())/60+":"+Integer.parseInt(singleEvent.getEndtime())%60;
            System.out.println("消息内容"+content);
            InviteMessage inviteMessage=new InviteMessage(content,"", JSON.toJSONString(singleEvent));
            ResponseResult result=rongCloudMethodUtil.sendSystemMessage(addInviteEventVo.getUserId(),persons,inviteMessage,"","");
            if (result.getCode()!=200){
                return DtoUtil.getFalseDto("发送消息失败",17002);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("消息发送出错",26002);
        }
        return DtoUtil.getSuccessDto("消息发送成功",100000);
    }

    /**
     * 修改一条邀请事件
     * @param addInviteEventVo
     * @param token
     * @return
     */
    @Override
    public Dto updInviteEvent(AddInviteEventVo addInviteEventVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(addInviteEventVo)){
            return DtoUtil.getFalseDto("添加邀请事件数据未获取到",26001);
        }
        if (StringUtils.isEmpty(addInviteEventVo.getUserId())){
            return DtoUtil.getFalseDto("userId不能为空",21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(addInviteEventVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21013);
        }
        //接收到的修改信息
        SingleEvent singleEvent=JSONObject.parseObject(addInviteEventVo.getSingleEvent(),SingleEvent.class);
        Map<String,Object> m1=SingleEvent.toMap(singleEvent);
        //原来的信息
        SingleEvent singleEventOld=eventMapper.queryEventOne(singleEvent.getUserid().toString(),singleEvent.getEventid().toString());
        Map<String,Object> m2=SingleEvent.toMap(singleEventOld);
        //比较差异
        StringBuffer different=SingleEventUtil.eventDifferent(m1,m2);

        if (StringUtils.isEmpty(different)){
            return DtoUtil.getFalseDto("没有任何更改",29102);
        }
        System.out.println("修改事件邀请"+singleEvent.toString());
        String redisKey=singleEvent.getUserid().toString()+singleEvent.getEventid();
        stringRedisTemplate.opsForValue().set(redisKey,JSON.toJSONString(singleEvent));
        String[] persons=singleEvent.getPerson().split(",");
        for (String person:persons) {
            StatisticsTable statisticsTable=new StatisticsTable();
            statisticsTable.setCreatorId(singleEvent.getUserid());
            statisticsTable.setEventId(singleEvent.getEventid());
            statisticsTable.setUserId(Long.parseLong(person));
            //生成投票
            System.out.println(statisticsTable.toString());
            if (statisticsMapper.rollbackStatistics(statisticsTable)==0){
                //回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return DtoUtil.getFalseDto("生成投票失败",29101);
            }
        }
        //发送信息
        try {
            Account account=accountMapper.queryAccount(addInviteEventVo.getUserId());
            RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
            //内容修改
            String content=account.getUserName()+"请求修改事件"+singleEventOld.getEventname()+"："+different.replace(different.length()-1,different.length(),"。");
            System.out.println("消息内容==>"+content);
            InviteMessage inviteMessage=new InviteMessage(content,"", JSON.toJSONString(singleEvent));
            //接收人员变动
            for (int i = 0; i < persons.length; i++) {
                if (persons[i].equals(addInviteEventVo.getUserId())){
                    persons[i]=singleEvent.getUserid().toString();
                }
            }
            ResponseResult result=rongCloudMethodUtil.sendSystemMessage(addInviteEventVo.getUserId(),persons,inviteMessage,"","");
            if (result.getCode()!=200){
                return DtoUtil.getFalseDto("发送消息失败",17002);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("消息发送出错",26002);
        }

        return DtoUtil.getFalseDto("发送成功",100000);
    }

    /**
     * 删除一条邀请事件
     * @param receivedSearchOnce
     * @param token
     * @return
     */
    @Override
    public Dto delInviteEvent(ReceivedSearchOnce receivedSearchOnce, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(receivedSearchOnce)){
            return DtoUtil.getFalseDto("添加邀请事件数据未获取到",26001);
        }
        if (StringUtils.isEmpty(receivedSearchOnce.getUserId())){
            return DtoUtil.getFalseDto("userId不能为空",21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedSearchOnce.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21013);
        }
        //找到该事件
        SingleEvent singleEvent=eventMapper.queryEventOne(receivedSearchOnce.getUserId(),receivedSearchOnce.getEventId());
        //查看该事件最高权限
        SingleEventVice singleEventVice=new SingleEventVice();
        singleEventVice.setUserId(Long.parseLong(receivedSearchOnce.getUserId()));
        singleEventVice.setEventId(Long.parseLong(receivedSearchOnce.getEventId()));
        singleEventVice=eventViceMapper.queryEventVice(singleEventVice);
        if (ObjectUtils.isEmpty(singleEvent)||ObjectUtils.isEmpty(singleEventVice)){
            return DtoUtil.getFalseDto("要删除的事件未找到",29001);
        }
        //如果是创建者删除
        if (singleEvent.getUserid().equals(singleEventVice.getCreateBy())){
            //该事件从创建者时间轴删除
            //其他参与者的该事件变为私有
            //参与者清空
            //最高权限表清空


        }else {
            //如果不是创建者删除
            //从自己的事件表里移除
            int i=eventMapper.deleteByDeleteType(singleEvent.getEventid(),"singleevent",receivedSearchOnce.getUserId());
            //其他参与者的事件里删除本参与者
            String[] persons=singleEvent.getPerson().split(",");
            for (int j = 0; j <persons.length ; j++) {
                //其他参与者的事件
                SingleEvent singleEvent1=eventMapper.queryEventOne(persons[j],singleEvent.getEventid().toString());
                singleEvent1.setPerson(singleEvent1.getPerson().indexOf(receivedSearchOnce.getUserId())!=-1?singleEvent1.getPerson().replace(receivedSearchOnce.getUserId(),""):singleEvent1.getPerson());
                int delResult=eventMapper.alterEventsByUserId(singleEvent1);
                if (delResult<=0){
                    //回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return DtoUtil.getFalseDto("其他参与者的事件里删除本参与者失败",29002);
                }
            }
            //通知其他参与者
            try {
                Account account=accountMapper.queryAccount(receivedSearchOnce.getUserId());
                RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
                String content=account.getUserName()+"退出了事件："+singleEvent.getEventname()+"。";
                System.out.println("消息内容"+content);
                InviteMessage inviteMessage=new InviteMessage(content,"", JSON.toJSONString(singleEvent));
                ResponseResult result=rongCloudMethodUtil.sendSystemMessage(receivedSearchOnce.getUserId(),persons,inviteMessage,"","");
                if (result.getCode()!=200){
                    return DtoUtil.getFalseDto("发送消息失败",17002);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return DtoUtil.getFalseDto("消息发送出错",26002);
            }
        }
        return DtoUtil.getSuccessDto("事件删除成功",100000);
    }

    /**
     * 回应事件邀请
     * @param feedbackEventInviteVo
     * @param token
     * @return
     */
    @Override
    public Dto feedbackEventInvite(FeedbackEventInviteVo feedbackEventInviteVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(feedbackEventInviteVo)){
            return DtoUtil.getFalseDto("添加邀请事件数据未获取到",26001);
        }
        if (StringUtils.isEmpty(feedbackEventInviteVo.getUserId())){
            return DtoUtil.getFalseDto("userId不能为空",21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(feedbackEventInviteVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21013);
        }
        //拿到发起者的事件
        SingleEvent singleEvent=JSONObject.parseObject(feedbackEventInviteVo.getExtraData(),SingleEvent.class);
        System.out.println("22222223333333=="+singleEvent.toString());
        //判断该事件的统计表是否已过期
        if ("1".equals(statisticsMapper.selectStaIsOverdue(singleEvent.getUserid().toString(),singleEvent.getEventid().toString()))){
            return DtoUtil.getFalseDto("投票已过期",21013);
        }
        String[] persons=singleEvent.getPerson().split(",");
        StatisticsTable statisticsTable=new StatisticsTable();
        //通过判断所有用户是否都答复决定是否发送消息给事件发起者
        //如果同意
        if (Integer.parseInt(feedbackEventInviteVo.getChoose())==0){
            //判断接受者的事件列表是否有冲突
            //查到冲突的事件集合
            List<SingleEvent> singleEvents=eventMapper.queryClashEventList(singleEvent);
            //判断是否忽略冲突任然添加
            if (Integer.parseInt(feedbackEventInviteVo.getIsHold())==0){
                //如果有冲突反馈给该用户
                if (singleEvents.size()>0){
                    return DtoUtil.getFalseDto("当前时间段已有事件",21016);
                }
            }
            //更改反馈统计表
            statisticsTable.setUserId(Long.parseLong(feedbackEventInviteVo.getUserId()));
            statisticsTable.setEventId(singleEvent.getEventid());
            statisticsTable.setCreatorId(singleEvent.getUserid());
            statisticsTable.setChoose(Long.parseLong(feedbackEventInviteVo.getChoose()));
            statisticsTable.setModify(1L);
            System.out.println("jijijijijijijijjijiji="+statisticsTable.toString());
            statisticsMapper.updateStatistics(statisticsTable);
            //查询是否所有人都给了反馈
            int i=statisticsMapper.queryStatisticsCount(statisticsTable);
            //所有的反馈都收到了 或者  通过判断timeUp字段决定是否发送消息给事件发起者
            if (i>=persons.length || Long.parseLong(feedbackEventInviteVo.getTimeUp())==1){
                //设置统计表为已过期
                statisticsMapper.updStaIsOverdue(singleEvent.getUserid().toString(),singleEvent.getEventid().toString());
                //发送统计结果给事件发起者
                RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
                //查询统计结果
                Map map=statisticsMapper.queryFeedbackStatistics(singleEvent.getUserid().toString(),singleEvent.getEventid().toString());
                //发送统计结果
                InviteMessage inviteMessage=new InviteMessage("你的事件邀请同意者："+map.get("agree")+"人，拒绝者"+map.get("refuse")+"人，未回应"+map.get("noReply")+"人","",feedbackEventInviteVo.getExtraData());
                System.out.println("消息内容："+inviteMessage.getContent());
                String[] targetId={singleEvent.getUserid().toString()};
                try {
                    ResponseResult result=rongCloudMethodUtil.sendSystemMessage(feedbackEventInviteVo.getUserId(),targetId,inviteMessage,"","");
                    if (result.getCode()!=200){
                        return DtoUtil.getFalseDto("发送消息失败",17002);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return DtoUtil.getFalseDto("消息发送失败",26002);
                }
            }
            return DtoUtil.getSuccessDto("信息已发出",100000);
            //如果拒绝
        }else if (Integer.parseInt(feedbackEventInviteVo.getChoose())==1){
            //更改反馈统计表
            statisticsTable.setUserId(Long.parseLong(feedbackEventInviteVo.getUserId()));
            statisticsTable.setEventId(singleEvent.getEventid());
            statisticsTable.setCreatorId(singleEvent.getUserid());
            statisticsTable.setChoose(Long.parseLong(feedbackEventInviteVo.getChoose()));
            statisticsTable.setModify(1L);
            System.out.println("ooooooooooooo="+statisticsTable.toString());
            statisticsMapper.updateStatistics(statisticsTable);
            //查询是否所有人都给了反馈
            int i=statisticsMapper.queryStatisticsCount(statisticsTable);
            //所有的反馈都收到了 或者  通过判断timeUp字段决定是否发送消息给事件发起者
            if (i>=persons.length || Long.parseLong(feedbackEventInviteVo.getTimeUp())==1){
                //设置统计表为已过期
                statisticsMapper.updStaIsOverdue(singleEvent.getUserid().toString(),singleEvent.getEventid().toString());
                //发送统计结果给事件发起者
                RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
                //查询统计结果
                Map map=statisticsMapper.queryFeedbackStatistics(singleEvent.getUserid().toString(),singleEvent.getEventid().toString());
                //发送统计结果
                InviteMessage inviteMessage=new InviteMessage("你的事件邀请同意者："+map.get("agree")+"人，拒绝者"+map.get("refuse")+"人，未回应"+map.get("noReply")+"人","",feedbackEventInviteVo.getExtraData());
                System.out.println("消息内容："+inviteMessage.getContent());
                String[] targetId={singleEvent.getUserid().toString()};
                try {
                    ResponseResult result=rongCloudMethodUtil.sendSystemMessage(feedbackEventInviteVo.getUserId(),targetId,inviteMessage,"","");
                    if (result.getCode()!=200){
                        return DtoUtil.getFalseDto("发送消息失败",17002);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return DtoUtil.getFalseDto("消息发送失败",26002);
                }
            }
            return DtoUtil.getSuccessDto("信息已发出",100000);
        }else if (Integer.parseInt(feedbackEventInviteVo.getChoose())==2 && Integer.parseInt(feedbackEventInviteVo.getTimeUp())==1){
            //通过判断timeUp字段决定是否发送消息给事件发起者
            //设置统计表为已过期
            statisticsMapper.updStaIsOverdue(singleEvent.getUserid().toString(),singleEvent.getEventid().toString());
            //发送统计结果给事件发起者
            RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
            //查询统计结果
            Map map=statisticsMapper.queryFeedbackStatistics(singleEvent.getUserid().toString(),singleEvent.getEventid().toString());
            //发送统计结果
            InviteMessage inviteMessage=new InviteMessage("你的事件邀请同意者："+map.get("agree")+"人，拒绝者"+map.get("refuse")+"人，未回应"+map.get("noReply")+"人","",feedbackEventInviteVo.getExtraData());
            System.out.println("时间到，消息内容："+inviteMessage.getContent());
            String[] targetId={singleEvent.getUserid().toString()};
            try {
                ResponseResult result=rongCloudMethodUtil.sendSystemMessage(feedbackEventInviteVo.getUserId(),targetId,inviteMessage,"","");
                if (result.getCode()!=200){
                    return DtoUtil.getFalseDto("发送消息失败",17002);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return DtoUtil.getFalseDto("消息发送失败",26002);
            }
            return DtoUtil.getSuccessDto("信息已发出",100000);
        }
        return DtoUtil.getFalseDto("反馈内容未识别",26010);
    }




    /**
     * 创建事件时创建者选择
     * @param eventCreatorChooseVo
     * @param token
     * @return
     */
    @Override
    public Dto eventCreatorChoose(EventCreatorChooseVo eventCreatorChooseVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(eventCreatorChooseVo)){
            return DtoUtil.getFalseDto("创建者选择数据未获取到",26001);
        }
        if (StringUtils.isEmpty(eventCreatorChooseVo.getUserId())){
            return DtoUtil.getFalseDto("userId不能为空",21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(eventCreatorChooseVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21013);
        }
        //先拿到事件
        SingleEvent singleEvent=JSONObject.parseObject(eventCreatorChooseVo.getExtraData(),SingleEvent.class);
        System.out.println(eventCreatorChooseVo.getExtraData());
        System.out.println("22222223333333=="+singleEvent.toString());
        RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
        String[] persons=singleEvent.getPerson().split(",");
        Account account=accountMapper.queryAccount(eventCreatorChooseVo.getUserId());
        //判断选择
        if (Integer.parseInt(eventCreatorChooseVo.getChoose())==1){
            //保留
            List<String> agrees=statisticsMapper.queryChooser("1",singleEvent.getUserid().toString(),singleEvent.getEventid().toString());
            String finalPerson=String.join(",",agrees);
            System.out.println("最终参与者"+finalPerson);
            singleEvent.setPerson(finalPerson);

            //事件时间冲突判断
            if (eventMapper.countIdByDate(singleEvent) != 0){
                return DtoUtil.getFalseDto("时间段冲突,无法添加",21012);
            }
            //把该事件添加进发起者事件列表(修改这件事)
            SingleEvent sEvent=eventMapper.queryEventOne(singleEvent.getUserid().toString(),singleEvent.getEventid().toString());
            if (!ObjectUtils.isEmpty(sEvent)){
                //修改
                eventMapper.alterEventsByUserId(singleEvent);
                //在事件副表插入创建者
                SingleEventVice singleEventVice=new SingleEventVice();
                singleEventVice.setCreateBy(Long.parseLong(eventCreatorChooseVo.getUserId()));
                singleEventVice.setUserId(singleEvent.getUserid());
                singleEventVice.setEventId(singleEvent.getEventid());
                eventViceMapper.createEventVice(singleEventVice);
            }
            eventMapper.uploadingEvents(singleEvent);
            //在事件副表插入创建者
            SingleEventVice singleEventVice=new SingleEventVice();
            singleEventVice.setCreateBy(Long.parseLong(eventCreatorChooseVo.getUserId()));
            singleEventVice.setUserId(singleEvent.getUserid());
            singleEventVice.setEventId(singleEvent.getEventid());
            eventViceMapper.createEventVice(singleEventVice);
            //判断同意该事件的人，他们的事件表是否有冲突事件
            for (String userId:agrees) {
                singleEvent.setUserid(Long.parseLong(userId));
                List<SingleEvent> singleEvents=eventMapper.queryClashEventList(singleEvent);
                if (singleEvents.size()>0){
                    for (SingleEvent se:singleEvents) {
                        //把冲突事件移入草稿箱
                        eventMapper.uplDraft(se);
                        //从事件表删除
                        DeleteEventVo deleteEventVo=new DeleteEventVo();
                        deleteEventVo.setEventId(se.getEventid().toString());
                        deleteEventVo.setUserId(se.getUserid().toString());
                        deleteEventVo.setEventStatus("2");
                        eventMapper.withdrawEventsByUserId(deleteEventVo);
                    }
                    //把该事件添加到该好友的事件表
                    //参与者变更(把参与者里的自己替换成创建者)
                    singleEvent.setPerson(singleEvent.getPerson().replace(userId,eventCreatorChooseVo.getUserId()));
                    eventMapper.uploadingEvents(singleEvent);
                    //在事件副表插入创建者
                    SingleEventVice singleEventVice1=new SingleEventVice();
                    singleEventVice1.setCreateBy(Long.parseLong(eventCreatorChooseVo.getUserId()));
                    singleEventVice1.setUserId(singleEvent.getUserid());
                    singleEventVice1.setEventId(singleEvent.getEventid());
                    eventViceMapper.createEventVice(singleEventVice1);
                    //通知该好友事件已修改
                    TxtMessage txtMessage=new TxtMessage(account.getUserName()+"发起的事件"+singleEvent.getEventname()+"已添至你的事件表","");
                    System.out.println("消息内容："+txtMessage.getContent());
                    try {
                        String[] targetId={userId};
                        ResponseResult result=rongCloudMethodUtil.sendSystemMessage(eventCreatorChooseVo.getUserId(),targetId,txtMessage,"","");
                        if (result.getCode()!=200){
                            return DtoUtil.getFalseDto("发送消息失败",17002);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return DtoUtil.getFalseDto("消息发送失败",26002);
                    }
                }
                //不冲突直接添加
                //参与者变更(把参与者里的自己替换成创建者)
                singleEvent.setPerson(singleEvent.getPerson().replace(userId,eventCreatorChooseVo.getUserId()));
                eventMapper.uploadingEvents(singleEvent);
                //在事件副表插入创建者
                SingleEventVice singleEventVice1=new SingleEventVice();
                singleEventVice1.setCreateBy(Long.parseLong(eventCreatorChooseVo.getUserId()));
                singleEventVice1.setUserId(singleEvent.getUserid());
                singleEventVice1.setEventId(singleEvent.getEventid());
                eventViceMapper.createEventVice(singleEventVice1);
                //通知该好友事件已修改
                TxtMessage txtMessage=new TxtMessage(account.getUserName()+"发起的事件"+singleEvent.getEventname()+"已添至你的事件表","");
                System.out.println("消息内容："+txtMessage.getContent());
                try {
                    String[] targetId={userId};
                    ResponseResult result=rongCloudMethodUtil.sendSystemMessage(eventCreatorChooseVo.getUserId(),targetId,txtMessage,"","");
                    if (result.getCode()!=200){
                        return DtoUtil.getFalseDto("发送消息失败",17002);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return DtoUtil.getFalseDto("消息发送失败",26002);
                }
            }
            return DtoUtil.getSuccessDto("消息发送成功",100000);
        }else {
            //不保留
            //删除该事件
            boolean result=stringRedisTemplate.delete(singleEvent.getUserid().toString()+singleEvent.getEventid().toString());
            if (!result){
                return DtoUtil.getFalseDto("删除失败",21014);
            }
            //通知被邀请者
            TxtMessage txtMessage=new TxtMessage("由"+account.getUserName()+"发起的事件"+singleEvent.getEventname()+"已被取消","");
            System.out.println("创建者选择消息内容："+txtMessage.getContent());
            try {
                ResponseResult result1=rongCloudMethodUtil.sendSystemMessage(eventCreatorChooseVo.getUserId(),persons,txtMessage,"","");
                if (result1.getCode()!=200){
                    return DtoUtil.getFalseDto("发送消息失败",17002);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return DtoUtil.getFalseDto("消息发送失败",26002);
            }
        }
        return DtoUtil.getSuccessDto("消息发送成功",100000);
    }

    /**
     * 修改事件时创建者选择
     * @param eventCreatorChooseVo
     * @param token
     * @return
     */
    @Override
    public Dto eventUpdChoose(EventCreatorChooseVo eventCreatorChooseVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(eventCreatorChooseVo)){
            return DtoUtil.getFalseDto("创建者选择数据未获取到",26001);
        }
        if (StringUtils.isEmpty(eventCreatorChooseVo.getUserId())){
            return DtoUtil.getFalseDto("userId不能为空",21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(eventCreatorChooseVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21013);
        }
        //先拿到事件
        SingleEvent singleEvent=JSONObject.parseObject(eventCreatorChooseVo.getExtraData(),SingleEvent.class);
        Map<String,Object> m1=SingleEvent.toMap(singleEvent);
        //原来的信息
        SingleEvent singleEventOld=eventMapper.queryEventOne(singleEvent.getUserid().toString(),singleEvent.getEventid().toString());
        Map<String,Object> m2=SingleEvent.toMap(singleEventOld);
        //比较差异
        StringBuffer different=SingleEventUtil.eventDifferent(m1,m2);

        System.out.println(eventCreatorChooseVo.getExtraData());
        System.out.println("22222223333333=="+singleEvent.toString());
        RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
        String[] persons=singleEvent.getPerson().split(",");
        Account account=accountMapper.queryAccount(eventCreatorChooseVo.getUserId());
        //判断选择
        if (Integer.parseInt(eventCreatorChooseVo.getChoose())==1){
            //保留
            List<String> agrees=statisticsMapper.queryChooser("1",singleEvent.getUserid().toString(),singleEvent.getEventid().toString());

            //把拒绝的和未回应的人的该条事件从事件表移入草稿箱
            List<String> reject=statisticsMapper.queryChooser("0",singleEvent.getUserid().toString(),singleEvent.getEventid().toString());
            reject.addAll(statisticsMapper.queryChooser("2",singleEvent.getUserid().toString(),singleEvent.getEventid().toString()));
            for (String rejectId:reject) {
                singleEventOld.setUserid(Long.parseLong(rejectId));
                //将该事件放入草稿箱
                int i=eventMapper.uplDraft(singleEventOld);
                //将该事件从事件表彻底删除
                int j=eventMapper.deleteSingleEvent(singleEventOld.getUserid().toString(),singleEventOld.getEventid().toString());
                if (i<=0 || j<= 0){
                    //回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return DtoUtil.getFalseDto("事件移动到草稿箱失败",23002);
                }
            }

            String finalPerson=String.join(",",agrees);
            System.out.println("最终参与者"+finalPerson);
            singleEvent.setPerson(finalPerson);
            //事件时间冲突判断
            if (eventMapper.countIdByDate(singleEvent) != 0){
                return DtoUtil.getFalseDto("时间段冲突,无法添加",21012);
            }
            //修改
            eventMapper.alterEventsByUserId(singleEvent);
            //判断同意该事件的人，他们的事件表是否有冲突事件
            for (String userId:agrees) {
                singleEvent.setUserid(Long.parseLong(userId));
                List<SingleEvent> singleEvents=eventMapper.queryClashEventList(singleEvent);
                if (singleEvents.size()>0){
                    for (SingleEvent se:singleEvents) {
                        //把冲突事件移入草稿箱
                        eventMapper.uplDraft(se);
                        //从事件表删除
                        DeleteEventVo deleteEventVo=new DeleteEventVo();
                        deleteEventVo.setEventId(se.getEventid().toString());
                        deleteEventVo.setUserId(se.getUserid().toString());
                        deleteEventVo.setEventStatus("2");
                        eventMapper.withdrawEventsByUserId(deleteEventVo);
                    }
                    //把该事件添加到该好友的事件表
                    //参与者变更(把参与者里的自己替换成创建者)
                    singleEvent.setPerson(singleEvent.getPerson().replace(userId,eventCreatorChooseVo.getUserId()));
                    eventMapper.uploadingEvents(singleEvent);
                    //通知该好友事件已修改
                    TxtMessage txtMessage=new TxtMessage(singleEvent.getEventname()+"事件已修改为："+"："+different.replace(different.length()-1,different.length(),"。"),"");
                    System.out.println("消息内容："+txtMessage.getContent());
                    try {
                        String[] targetId={userId};
                        ResponseResult result=rongCloudMethodUtil.sendSystemMessage(eventCreatorChooseVo.getUserId(),targetId,txtMessage,"","");
                        if (result.getCode()!=200){
                            return DtoUtil.getFalseDto("发送消息失败",17002);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return DtoUtil.getFalseDto("消息发送失败",26002);
                    }
                }
                //不冲突直接添加
                //参与者变更(把参与者里的自己替换成创建者)
                singleEvent.setPerson(singleEvent.getPerson().replace(userId,eventCreatorChooseVo.getUserId()));
                eventMapper.uploadingEvents(singleEvent);
                //在事件副表插入创建者
                SingleEventVice singleEventVice1=new SingleEventVice();
                singleEventVice1.setCreateBy(Long.parseLong(eventCreatorChooseVo.getUserId()));
                singleEventVice1.setUserId(singleEvent.getUserid());
                singleEventVice1.setEventId(singleEvent.getEventid());
                eventViceMapper.createEventVice(singleEventVice1);
                //通知该好友事件已修改
                TxtMessage txtMessage=new TxtMessage(singleEvent.getEventname()+"事件已修改为："+"："+different.replace(different.length()-1,different.length(),"。"),"");
                System.out.println("消息内容："+txtMessage.getContent());
                try {
                    String[] targetId={userId};
                    ResponseResult result=rongCloudMethodUtil.sendSystemMessage(eventCreatorChooseVo.getUserId(),targetId,txtMessage,"","");
                    if (result.getCode()!=200){
                        return DtoUtil.getFalseDto("发送消息失败",17002);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return DtoUtil.getFalseDto("消息发送失败",26002);
                }
            }
            return DtoUtil.getSuccessDto("消息发送成功",100000);
        }else {
            //不保留
            //删除该事件
            boolean result=stringRedisTemplate.delete(singleEvent.getUserid().toString()+singleEvent.getEventid().toString());
            if (!result){
                return DtoUtil.getFalseDto("删除失败",21014);
            }
            //通知被邀请者
            TxtMessage txtMessage=new TxtMessage("修改事件"+singleEventOld.getEventname()+"已被取消","");
            System.out.println("创建者选择消息内容："+txtMessage.getContent());
            try {
                ResponseResult result1=rongCloudMethodUtil.sendSystemMessage(eventCreatorChooseVo.getUserId(),persons,txtMessage,"","");
                if (result1.getCode()!=200){
                    return DtoUtil.getFalseDto("发送消息失败",17002);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return DtoUtil.getFalseDto("消息发送失败",26002);
            }
        }
        return DtoUtil.getSuccessDto("消息发送成功",100000);
    }

    @Override
    public Dto searchByDayForIOS(SearchConditionsForIOS searchConditionsForIOS, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("操作失败,token未获取到",21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(searchConditionsForIOS.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        SingleEvent singleEvent = new SingleEvent();
        StringBuffer stringBuffer = new StringBuffer(searchConditionsForIOS.getDate());
        singleEvent.setUserid(Long.valueOf(searchConditionsForIOS.getUserId()));
        singleEvent.setIsOverdue((long)searchConditionsForIOS.getStatus());
        singleEvent.setIsLoop(searchConditionsForIOS.getIsLoop());
        singleEvent.setYear(Long.valueOf(stringBuffer.substring(0,4)));
        singleEvent.setMonth(Long.valueOf(stringBuffer.substring(4,6)));
        singleEvent.setDay(Long.valueOf(stringBuffer.substring(6,8)));
        List<SingleEvent> singleEventList = eventMapper.queryEventsByDayForIOS(singleEvent);
        if (singleEventList.size() != 0){
            return DtoUtil.getSuccesWithDataDto("查询成功",SingleEventUtil.getShowSingleEventList(singleEventList),100000);
        }
        return DtoUtil.getFalseDto("未查询到数据",100000);
    }

    @Override
    public Dto deleteInBatches(ReceivedDeleteEventIds receivedDeleteEventIds, String token) {
        try {
            if (!StringUtils.hasText(token)) {
                return DtoUtil.getFalseDto("token未获取到", 21013);
            }
            if (!token.equals(stringRedisTemplate.opsForValue().get(receivedDeleteEventIds.getUserId()))) {
                return DtoUtil.getFalseDto("token过期请先登录", 21014);
            }
            if (receivedDeleteEventIds.getDeleteType().equals("0")){
                receivedDeleteEventIds.setDeleteType("singleevent");
            }
            if (receivedDeleteEventIds.getDeleteType().equals("1")){
                receivedDeleteEventIds.setDeleteType("draft");
            }
            for (Long l : receivedDeleteEventIds.getEventIds()){
                if (eventMapper.deleteByDeleteType(l,receivedDeleteEventIds.getDeleteType(),receivedDeleteEventIds.getUserId()) == 0){
                    return DtoUtil.getFalseDto("删除失败",21016);
                }
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getFalseDto("批量删除失败,请重新删除",21015);
        }
        return DtoUtil.getSuccessDto("批量删除成功",100000);
    }

    /**
     * 查询一个草稿事件
     * @param receivedSearchOnce
     * @param token
     * @return
     */
    @Override
    public Dto searchDraftOnce(ReceivedSearchOnce receivedSearchOnce, String token) {
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedSearchOnce.getUserId()))) {
            return DtoUtil.getFalseDto("token过期请先登录", 21014);
        }
        SingleEvent singleEvent = eventMapper.queryDraftOne(receivedSearchOnce.getUserId(),receivedSearchOnce.getEventId());
        if (singleEvent != null){
            return DtoUtil.getSuccesWithDataDto("查询成功",SingleEventUtil.getShowSingleEvent(singleEvent),100000);
        }
        return DtoUtil.getSuccessDto("未查询到事件",200000);
    }

    /**
     * 将事件从事件表移除到草稿箱
     * @param addInviteEventVo
     * @param token
     * @return
     */
    @Override
    public Dto eventRemoveDraft(AddInviteEventVo addInviteEventVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(addInviteEventVo)){
            return DtoUtil.getFalseDto("创建者选择数据未获取到",26001);
        }
        if (StringUtils.isEmpty(addInviteEventVo.getUserId())){
            return DtoUtil.getFalseDto("userId不能为空",21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(addInviteEventVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21013);
        }
        SingleEvent singleEvent=JSONObject.parseObject(addInviteEventVo.getSingleEvent(),SingleEvent.class);
        if (ObjectUtils.isEmpty(singleEvent)){
            return DtoUtil.getFalseDto("事件格式错误",23001);
        }
        //将该事件放入草稿箱
        int i=eventMapper.uplDraft(singleEvent);
        //将该事件从事件表彻底删除
        int j=eventMapper.deleteSingleEvent(singleEvent.getUserid().toString(),singleEvent.getEventid().toString());
        if (i<=0 || j<= 0){
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getFalseDto("事件移动到草稿箱失败",23002);
        }
        return DtoUtil.getSuccessDto("事件移动到草稿箱成功",100000);
    }

    /**
     * 变更邀请事件成员
     * @param updatePersonsVo
     * @param token
     * @return
     */
    @Override
    public Dto updateInvitePerson(UpdatePersonsVo updatePersonsVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(updatePersonsVo)){
            return DtoUtil.getFalseDto("数据未获取到",26001);
        }
        if (StringUtils.isEmpty(updatePersonsVo.getUserId())){
            return DtoUtil.getFalseDto("userId不能为空",21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(updatePersonsVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21013);
        }
        SingleEvent singleEvent=eventMapper.queryEventOne(updatePersonsVo.getUserId(),updatePersonsVo.getEventId());
        if (ObjectUtils.isEmpty(singleEvent)){
            return DtoUtil.getFalseDto("事件不存在",23333);
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
        for(String old : oldPersons){
            //判断是否包含
            if(!list1.contains(old)){
                result1.add(old);
            }
        }
        for(String newP : newPersons){
            //判断是否包含
            if(!list2.contains(newP)){
                result2.add(newP);
            }
        }
        String[] targetId;
        if (result2.size()>0){
            //给新添加的成员发送邀请消息
            targetId= (String[]) result2.toArray();

        }

        if (result1.size()>0){
            //给被移除的成员发送提醒消息
            targetId= (String[]) result1.toArray();
        }
        return DtoUtil.getFalseDto("没有成员发生变动",23334);
    }

    /**
     * 变更支持事件成员
     * @param updatePersonsVo
     * @param token
     * @return
     */
    @Override
    public Dto updateBackers(UpdatePersonsVo updatePersonsVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(updatePersonsVo)){
            return DtoUtil.getFalseDto("数据未获取到",26001);
        }
        if (StringUtils.isEmpty(updatePersonsVo.getUserId())){
            return DtoUtil.getFalseDto("userId不能为空",21011);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(updatePersonsVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21013);
        }
        //给新添加的成员发送邀请消息

        //给被移除的成员发送提醒消息
        return null;
    }
}
