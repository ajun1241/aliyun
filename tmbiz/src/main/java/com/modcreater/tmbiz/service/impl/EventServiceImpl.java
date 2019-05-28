package com.modcreater.tmbiz.service.impl;

import com.alibaba.fastjson.JSON;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.*;
import com.modcreater.tmbeans.show.ShowSingleEvent;
import com.modcreater.tmbeans.vo.eventvo.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedDeleteEventIds;
import com.modcreater.tmbiz.service.EventService;
import com.modcreater.tmdao.mapper.*;
import com.modcreater.tmutils.*;
import io.rong.messages.TxtMessage;
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
    private BackerMapper backerMapper;

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
    public Dto addEventBacker(AddbackerVo addbackerVo, String token) {
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
            rongCloudMethodUtil.sendSystemMessage(addbackerVo.getUserId(),backers,inviteMessage,"","");
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



        //同意
        if (Long.parseLong(feedbackEventBackerVo.getChoose())==1){
            //更改backer表状态
            backers.setUserId(singleEvent.getUserid());
            backers.setEventId(singleEvent.getEventid());
            backers.setBackerId(Long.parseLong(feedbackEventBackerVo.getUserId()));
            backers.setStatus(1L);
            if (backerMapper.updateBacker(backers)==0){
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
            try {
                rongCloudMethodUtil.sendSystemMessage(feedbackEventBackerVo.getUserId(),targetId,inviteMessage,"","");
            } catch (Exception e) {
                e.printStackTrace();
                return DtoUtil.getFalseDto("消息发送失败",26002);
            }
            //设置定时给支持者发信息



        }else if (Long.parseLong(feedbackEventBackerVo.getChoose())==2){
            //拒绝
            //更改backer表状态
            backers.setUserId(singleEvent.getUserid());
            backers.setEventId(singleEvent.getEventid());
            backers.setBackerId(Long.parseLong(feedbackEventBackerVo.getUserId()));
            backers.setStatus(2L);
            if (backerMapper.updateBacker(backers)==0){
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
            try {
                rongCloudMethodUtil.sendSystemMessage(feedbackEventBackerVo.getUserId(),targetId,inviteMessage,"","");
            } catch (Exception e) {
                e.printStackTrace();
                return DtoUtil.getFalseDto("消息发送失败",26002);
            }
        }
        return DtoUtil.getSuccessDto("消息发送成功",100000);
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
            rongCloudMethodUtil.sendSystemMessage(addInviteEventVo.getUserId(),persons,inviteMessage,"","");
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
        SingleEvent singleEvent=JSONObject.parseObject(addInviteEventVo.getSingleEvent(),SingleEvent.class);
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
            String content=account.getUserName()+"请求修改"+singleEvent.getMonth()+"月"+singleEvent.getDay()+"日"+"的"+singleEvent.getEventname()+"活动。";
            System.out.println("消息内容==>"+content);
            InviteMessage inviteMessage=new InviteMessage(content,"", JSON.toJSONString(singleEvent));
            //接收人员变动
            for (int i = 0; i < persons.length; i++) {
                if (persons[i].equals(addInviteEventVo.getUserId())){
                    persons[i]=singleEvent.getUserid().toString();
                }
            }
            rongCloudMethodUtil.sendSystemMessage(addInviteEventVo.getUserId(),persons,inviteMessage,"","");
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
        //从自己的时间轴删除
        //其他参与者的事件里删除本参与者
        //通知其他参与者
        return null;
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
        String[] persons=singleEvent.getPerson().split(",");
        StatisticsTable statisticsTable=new StatisticsTable();
        //通过判断所有用户是否都答复决定是否发送消息给事件发起者
        //如果同意
        if (Integer.parseInt(feedbackEventInviteVo.getChoose())==0){
            //判断接受者的事件列表是否有冲突
            //查到冲突的事件集合
            List<SingleEvent> singleEvents=eventMapper.queryClashEventList(singleEvent);
            //如果有冲突反馈给该用户
            if (singleEvents.size()>0){
                return DtoUtil.getFalseDto("当前时间段已有事件",21016);
            }
            //更改反馈统计表
            statisticsTable.setUserId(Long.parseLong(feedbackEventInviteVo.getUserId()));
            statisticsTable.setEventId(singleEvent.getEventid());
            statisticsTable.setCreatorId(singleEvent.getUserid());
            statisticsTable.setChoose(Long.parseLong(feedbackEventInviteVo.getChoose()));
            statisticsTable.setModify(1);
            System.out.println("jijijijijijijijjijiji="+statisticsTable.toString());
            statisticsMapper.updateStatistics(statisticsTable);
            //查询是否所有人都给了反馈
            int i=statisticsMapper.queryStatisticsCount(statisticsTable);
            //所有的反馈都收到了 或者  通过判断timeUp字段决定是否发送消息给事件发起者
            if (i>=persons.length || Long.parseLong(feedbackEventInviteVo.getTimeUp())==1){
                //发送统计结果给事件发起者
                RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
                //查询统计结果
                Map map=statisticsMapper.queryFeedbackStatistics(singleEvent.getUserid().toString(),singleEvent.getEventid().toString());
                //发送统计结果
                InviteMessage inviteMessage=new InviteMessage("你的事件邀请同意者："+map.get("agree")+"人，拒绝者"+map.get("refuse")+"人，未回应"+map.get("noReply")+"人","",feedbackEventInviteVo.getExtraData());
                System.out.println("消息内容："+inviteMessage.getContent());
                String[] targetId={singleEvent.getUserid().toString()};
                try {
                    rongCloudMethodUtil.sendSystemMessage(feedbackEventInviteVo.getUserId(),targetId,inviteMessage,"","");
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
            statisticsTable.setModify(1);
            System.out.println("ooooooooooooo="+statisticsTable.toString());
            statisticsMapper.updateStatistics(statisticsTable);
            //查询是否所有人都给了反馈
            int i=statisticsMapper.queryStatisticsCount(statisticsTable);
            //所有的反馈都收到了 或者  通过判断timeUp字段决定是否发送消息给事件发起者
            if (i>=persons.length || Long.parseLong(feedbackEventInviteVo.getTimeUp())==1){
                //发送统计结果给事件发起者
                RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
                //查询统计结果
                Map map=statisticsMapper.queryFeedbackStatistics(singleEvent.getUserid().toString(),singleEvent.getEventid().toString());
                //发送统计结果
                InviteMessage inviteMessage=new InviteMessage("你的事件邀请同意者："+map.get("agree")+"人，拒绝者"+map.get("refuse")+"人，未回应"+map.get("noReply")+"人","",feedbackEventInviteVo.getExtraData());
                System.out.println("消息内容："+inviteMessage.getContent());
                String[] targetId={singleEvent.getUserid().toString()};
                try {
                    rongCloudMethodUtil.sendSystemMessage(feedbackEventInviteVo.getUserId(),targetId,inviteMessage,"","");
                } catch (Exception e) {
                    e.printStackTrace();
                    return DtoUtil.getFalseDto("消息发送失败",26002);
                }
            }
            return DtoUtil.getSuccessDto("信息已发出",100000);
        }else if (Integer.parseInt(feedbackEventInviteVo.getChoose())==2 && Integer.parseInt(feedbackEventInviteVo.getTimeUp())==1){
            //通过判断timeUp字段决定是否发送消息给事件发起者
            //发送统计结果给事件发起者
            RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
            //查询统计结果
            Map map=statisticsMapper.queryFeedbackStatistics(singleEvent.getUserid().toString(),singleEvent.getEventid().toString());
            //发送统计结果
            InviteMessage inviteMessage=new InviteMessage("你的事件邀请同意者："+map.get("agree")+"人，拒绝者"+map.get("refuse")+"人，未回应"+map.get("noReply")+"人","",feedbackEventInviteVo.getExtraData());
            System.out.println("时间到，消息内容："+inviteMessage.getContent());
            String[] targetId={singleEvent.getUserid().toString()};
            try {
                rongCloudMethodUtil.sendSystemMessage(feedbackEventInviteVo.getUserId(),targetId,inviteMessage,"","");
            } catch (Exception e) {
                e.printStackTrace();
                return DtoUtil.getFalseDto("消息发送失败",26002);
            }
            return DtoUtil.getSuccessDto("信息已发出",100000);
        }
        return DtoUtil.getFalseDto("反馈内容未识别",26010);
    }

    /**
     * 创建者选择
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
            }
            eventMapper.uploadingEvents(singleEvent);
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
                    eventMapper.uploadingEvents(singleEvent);
                    //通知该好友事件已修改
                    TxtMessage txtMessage=new TxtMessage(account.getUserName()+"发起的事件"+singleEvent.getEventname()+"已添至你的事件表","");
                    System.out.println("消息内容："+txtMessage.getContent());
                    try {
                        String[] targetId={userId};
                        rongCloudMethodUtil.sendSystemMessage(eventCreatorChooseVo.getUserId(),targetId,txtMessage,"","");
                    } catch (Exception e) {
                        e.printStackTrace();
                        return DtoUtil.getFalseDto("消息发送失败",26002);
                    }
                }
                //不冲突直接添加
                eventMapper.uploadingEvents(singleEvent);
                //通知该好友事件已修改
                TxtMessage txtMessage=new TxtMessage(account.getUserName()+"发起的事件"+singleEvent.getEventname()+"已添至你的事件表","");
                System.out.println("消息内容："+txtMessage.getContent());
                try {
                    String[] targetId={userId};
                    rongCloudMethodUtil.sendSystemMessage(eventCreatorChooseVo.getUserId(),targetId,txtMessage,"","");
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
                rongCloudMethodUtil.sendSystemMessage(eventCreatorChooseVo.getUserId(),persons,txtMessage,"","");
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
                if (eventMapper.deleteByDeleteType(l,receivedDeleteEventIds.getDeleteType()) == 0){
                    return DtoUtil.getFalseDto("删除失败",21016);
                }
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getFalseDto("批量删除失败,请重新删除",21015);
        }
        return DtoUtil.getSuccessDto("批量删除成功",100000);
    }
}
