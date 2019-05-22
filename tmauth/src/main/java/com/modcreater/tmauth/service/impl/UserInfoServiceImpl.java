package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.UserInfoService;
import com.modcreater.tmbeans.databaseresult.GetUserEventsGroupByType;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Achievement;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.pojo.UserAchievement;
import com.modcreater.tmbeans.pojo.UserStatistics;
import com.modcreater.tmbeans.show.ShowUserAnalysis;
import com.modcreater.tmbeans.show.userinfo.ShowCompletedEvents;
import com.modcreater.tmbeans.show.userinfo.ShowUserDetails;
import com.modcreater.tmbeans.show.userinfo.ShowUserStatistics;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedEventConditions;
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

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-17
 * Time: 13:43
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    private AchievementMapper achievementMapper;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private EventMapper eventMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Dto showUserDetails(String userId, String token) {
        System.out.println("查询用户成就==>"+userId);
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        if (StringUtils.hasText(userId)){
            UserStatistics userStatistics = achievementMapper.queryUserStatistics(userId);
            ShowUserStatistics showUserStatistics= new ShowUserStatistics();
            showUserStatistics.setCompleted(userStatistics.getCompleted());
            showUserStatistics.setUnfinished(userStatistics.getUnfinished());
            showUserStatistics.setDrafts(userStatistics.getDrafts());
            List<String> imgUrlList = queryUserAchievementInBase(userId);
            Map<String,Object> result = new HashMap<>(3);
            //用户事件状态
            result.put("userStatistics",showUserStatistics);
            //用户所有成就
            result.put("imgUrlList",imgUrlList);
            return DtoUtil.getSuccesWithDataDto("查询用户详情成功",result,100000);
        }
        return DtoUtil.getSuccessDto("没有查询到用户信息",100000);
    }

    @Override
    public Dto showUserEvents(String userId,String isOverdue, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        if (!StringUtils.hasText(isOverdue)){
            return DtoUtil.getFalseDto("事件状态未获取到",40005);
        }
        //查询用户已完成的事件(根据事件排序,只显示7条)
        /*List<SingleEvent> showCompletedLoopEventsList = eventMapper.queryUserCompletedEventsByStartDate(userId,"1");
        DateUtil.stringToWeek(String.valueOf(new Date("yyyyMMdd")));
        if (showCompletedLoopEventsList.size() != 0){
            for (SingleEvent singleEvent : showCompletedLoopEventsList){
                Boolean[] booleans = SingleEventUtil.getRepeatTime(singleEvent);
                if (booleans[DateUtil.getTodayWeek()]){

                }
            }
        }*/
        List<SingleEvent> singleEventList = eventMapper.queryUserEventsByUserIdIsOverdue(userId,isOverdue);
        if (singleEventList.size() != 0){
            List<ShowCompletedEvents> showCompletedEventsList = new ArrayList<>();
            for (SingleEvent singleEvent : singleEventList){
                ShowCompletedEvents showCompletedEvents = new ShowCompletedEvents();
                showCompletedEvents.setEventId(singleEvent.getEventid().toString());
                showCompletedEvents.setUserId(singleEvent.getUserid().toString());
                showCompletedEvents.setEventName(singleEvent.getEventname());
                showCompletedEvents.setDate(singleEvent.getYear().toString()+"-"+singleEvent.getMonth()+"-"+singleEvent.getDay());
                showCompletedEventsList.add(showCompletedEvents);
            }
            return DtoUtil.getSuccesWithDataDto("查询用户事件成功",showCompletedEventsList,100000);
        }
        return DtoUtil.getSuccessDto("未查到用户事件",100000);
    }

    @Override
    public Dto queryUserAchievement(String userId, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        List<String> result = queryUserAchievementInBase(userId);
        if (result.size() != 0){
            Map<String,List<String>> imgUrlList = new HashMap<>();
            imgUrlList.put("imgUrlList",result);
            return DtoUtil.getSuccesWithDataDto("查询用户成就成功",imgUrlList,100000);
        }
        return DtoUtil.getSuccessDto("该用户还没有任何成就",100000);
    }

    @Override
    public List<String> queryUserAchievementInBase(String userId) {
        /**
         * 在此查询用户统计表,并判断该用户是否完成某个成就
         */
        UserStatistics userStatistics = achievementMapper.queryUserStatistics(userId);
        List<Achievement> achievementList = achievementMapper.queryAchievement();
        if (!ObjectUtils.isEmpty(userStatistics) && !ObjectUtils.isEmpty(achievementList)) {
            for (Achievement achievement : achievementList) {
                if (userStatistics.getLoggedDays() == (achievement.getLoggedDaysCondition()).longValue()) {
                    achievementMapper.addNewAchievement(achievement.getId(),userId);
                }
                if (userStatistics.getCompleted() == achievement.getFinishedEventsCondition().longValue()){
                    achievementMapper.addNewAchievement(achievement.getId(),userId);
                }
            }
        }
        return achievementMapper.searchAllAchievement(userId);
    }

    @Override
    public Dto searchUserEventsByEventName(String userId,String eventName,String isOverdue, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        SingleEvent inDataBase = new SingleEvent();
        inDataBase.setUserid(Long.valueOf(userId));
        inDataBase.setEventname(eventName);
        inDataBase.setIsOverdue(Long.valueOf(isOverdue));
        List<SingleEvent> singleEventList = eventMapper.searchEventsByEventName(inDataBase);
        if (singleEventList.size() != 0){
            List<ShowCompletedEvents> showCompletedEventsList = new ArrayList<>();
            for (SingleEvent singleEvent : singleEventList){
                ShowCompletedEvents showCompletedEvents = new ShowCompletedEvents();
                showCompletedEvents.setEventId(singleEvent.getEventid().toString());
                showCompletedEvents.setEventName(singleEvent.getEventname());
                showCompletedEvents.setUserId(singleEvent.getUserid().toString());
                showCompletedEvents.setDate(singleEvent.getYear().toString()+"-"+singleEvent.getMonth()+"-"+singleEvent.getDay());
                showCompletedEventsList.add(showCompletedEvents);
            }
            return DtoUtil.getSuccesWithDataDto("查询成功",showCompletedEventsList,100000);
        }
        return DtoUtil.getSuccessDto("未查询到结果",100000);
    }

    @Override
    public Dto filtrateUserEvents(ReceivedEventConditions receivedEventConditions, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(receivedEventConditions)){
            return DtoUtil.getFalseDto("筛选条件接收失败",40002);
        }
        if (!StringUtils.hasText(receivedEventConditions.getStartTime())){
            return DtoUtil.getFalseDto("开始时间不能为空",40001);
        }
        if (!StringUtils.hasText(receivedEventConditions.getEndTime())){
            return DtoUtil.getFalseDto("结束时间不能为空",40004);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(receivedEventConditions.getUserId());
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        SingleEvent singleEventCondition = new SingleEvent();
        singleEventCondition.setType(Long.valueOf(receivedEventConditions.getEventType()));
        singleEventCondition.setLevel(Long.valueOf(receivedEventConditions.getEventLevel()));
        singleEventCondition.setStarttime(receivedEventConditions.getStartTime());
        singleEventCondition.setEndtime(receivedEventConditions.getEndTime());
        singleEventCondition.setPerson(receivedEventConditions.getPerson());
        singleEventCondition.setIsOverdue(Long.valueOf(receivedEventConditions.getIsOverdue()));
        if (receivedEventConditions.getStartDate().length() != 8){
            return DtoUtil.getFalseDto("日期格式异常",40003);
        }
        StringBuilder startDate = new StringBuilder(receivedEventConditions.getStartDate());
        singleEventCondition.setYear(Long.valueOf(startDate.substring(0,4)));
        singleEventCondition.setMonth(Long.valueOf(startDate.substring(4,6)));
        singleEventCondition.setDay(Long.valueOf(startDate.substring(6,8)));
        List<SingleEvent> singleEventList = eventMapper.queryEventsByConditions(singleEventCondition);
        /**
         * 可能要做重复事件
         */
        if (singleEventList.size() != 0){
            List<ShowCompletedEvents> showCompletedEventsList = new ArrayList<>();
            for (SingleEvent singleEvent : singleEventList){
                ShowCompletedEvents showCompletedEvents = new ShowCompletedEvents();
                showCompletedEvents.setEventId(singleEvent.getEventid().toString());
                showCompletedEvents.setUserId(singleEvent.getUserid().toString());
                showCompletedEvents.setEventName(singleEvent.getEventname());
                showCompletedEvents.setDate(singleEvent.getYear().toString()+"-"+singleEvent.getMonth()+"-"+singleEvent.getDay());
                showCompletedEventsList.add(showCompletedEvents);
            }
            return DtoUtil.getSuccesWithDataDto("筛选已完成事件成功",showCompletedEventsList,100000);
        }
        return DtoUtil.getSuccessDto("没有查询到事件",100000);
    }

    @Override
    public Dto statisticAnalysisOfData(String userId, String token) {
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        String redisToken = stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)) {
            return DtoUtil.getFalseDto("token过期请先登录", 21014);
        }
        ShowUserAnalysis showUserAnalysis = new ShowUserAnalysis();
        showUserAnalysis.setUserId(userId);
        //记录单一事件和重复事件的总和
        Long totalEvents = 0L;
        //记录单一事件和重复事件的总用时分钟数
        Long totalMinutes = 0L;
        Map<String,String> percentResult = new HashMap<>();
        Map<String,Long> totalMinutesResult = new HashMap<>();
        List<GetUserEventsGroupByType> typeList = eventMapper.getUserEventsGroupByType(userId);
        List<GetUserEventsGroupByType> newList = new ArrayList<>();
        List<GetUserEventsGroupByType> loopTypeList = eventMapper.getUserLoopEventsGroupByType(userId);
        System.out.println(typeList.size() +"<=======>"+loopTypeList.size());
        //控制计算精度
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        //此处判断,如果重复事件和单一事件都有查询结果
        if (loopTypeList.size() != 0 && typeList.size() != 0) {
            for (GetUserEventsGroupByType loopType : loopTypeList) {
                for (GetUserEventsGroupByType type : typeList) {
                    if (loopType.getType().equals(type.getType())){
                        System.out.println("进入单一+重复");
                        //记录事件总和
                        totalEvents += type.getNum() + loopType.getNum();
                        //记录用时分钟总和
                        totalMinutes += type.getTotalMinutes() + loopType.getTotalMinutes();
                        type.setNum(type.getNum() + loopType.getNum());
                        type.setTotalMinutes(type.getTotalMinutes() + loopType.getTotalMinutes());
                    }else {
                        System.out.println("进入单一+重复else");
                        //记录事件总和
                        totalEvents += type.getNum() + loopType.getNum();
                        //记录用时分钟总和
                        totalMinutes += type.getTotalMinutes() + loopType.getTotalMinutes();
                        newList.add(loopType);
                    }
                }
            }
            for (GetUserEventsGroupByType type : typeList){
                if (type.getType() == 0){
                    if (type.getNum() == 0){
                        percentResult.put("a","0");
                    }
                    percentResult.put("a",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("a",type.getTotalMinutes());
                }
                if (type.getType() == 1){
                    if (type.getNum() == 1){
                        percentResult.put("b","0");
                    }
                    percentResult.put("b",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("b",type.getTotalMinutes());
                }
                if (type.getType() == 2){
                    if (type.getNum() == 2){
                        percentResult.put("c","0");
                    }
                    percentResult.put("c",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("c",type.getTotalMinutes());
                }
                if (type.getType() == 3){
                    if (type.getNum() == 3){
                        percentResult.put("d","0");
                    }
                    percentResult.put("d",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("d",type.getTotalMinutes());
                }
                if (type.getType() == 4){
                    if (type.getNum() == 4){
                        percentResult.put("e","0");
                    }
                    percentResult.put("e",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("e",type.getTotalMinutes());
                }
                if (type.getType() == 5){
                    if (type.getNum() == 5){
                        percentResult.put("f","0");
                    }
                    percentResult.put("f",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("f",type.getTotalMinutes());
                }
                if (type.getType() == 6){
                    if (type.getNum() == 6){
                        percentResult.put("g","0");
                    }
                    percentResult.put("g",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("g",type.getTotalMinutes());
                }
                if (type.getType() == 7){
                    if (type.getNum() == 7){
                        percentResult.put("h","0");
                    }
                    percentResult.put("h",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("h",type.getTotalMinutes());
                }
            }
            Long maxSingleEventType = eventMapper.getMaxSingleEventType(userId);
            Long maxLoopEventType = eventMapper.getMaxLoopEventType(userId);
            if (maxSingleEventType > maxLoopEventType){
                showUserAnalysis.setMaxType(maxSingleEventType);
            }else {
                showUserAnalysis.setMaxType(maxLoopEventType);
            }
            Long minSingleEventType = eventMapper.getMinSingleEventType(userId);
            Long minLoopEventType = eventMapper.getMinLoopEventType(userId);
            if (minSingleEventType < minLoopEventType){
                showUserAnalysis.setMaxType(minSingleEventType);
            }else {
                showUserAnalysis.setMaxType(minLoopEventType);
            }
        }else if (loopTypeList.size() != 0 && typeList.size() == 0){
            System.out.println("进入重复");
            for (GetUserEventsGroupByType type : loopTypeList){
                totalEvents += type.getNum();
                totalMinutes += type.getTotalMinutes();
            }
            for (GetUserEventsGroupByType type : typeList){
                if (type.getType() == 0){
                    if (type.getNum() == 0){
                        percentResult.put("a","0");
                    }
                    percentResult.put("a",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("a",type.getTotalMinutes());
                }
                if (type.getType() == 1){
                    if (type.getNum() == 1){
                        percentResult.put("b","0");
                    }
                    percentResult.put("b",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("b",type.getTotalMinutes());
                }
                if (type.getType() == 2){
                    if (type.getNum() == 2){
                        percentResult.put("c","0");
                    }
                    percentResult.put("c",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("c",type.getTotalMinutes());
                }
                if (type.getType() == 3){
                    if (type.getNum() == 3){
                        percentResult.put("d","0");
                    }
                    percentResult.put("d",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("d",type.getTotalMinutes());
                }
                if (type.getType() == 4){
                    if (type.getNum() == 4){
                        percentResult.put("e","0");
                    }
                    percentResult.put("e",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("e",type.getTotalMinutes());
                }
                if (type.getType() == 5){
                    if (type.getNum() == 5){
                        percentResult.put("f","0");
                    }
                    percentResult.put("f",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("f",type.getTotalMinutes());
                }
                if (type.getType() == 6){
                    if (type.getNum() == 6){
                        percentResult.put("g","0");
                    }
                    percentResult.put("g",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("g",type.getTotalMinutes());
                }
                if (type.getType() == 7){
                    if (type.getNum() == 7){
                        percentResult.put("h","0");
                    }
                    percentResult.put("h",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("h",type.getTotalMinutes());
                }
            }
            showUserAnalysis.setMaxType(eventMapper.getMaxLoopEventType(userId));
            showUserAnalysis.setMinType(eventMapper.getMinLoopEventType(userId));
        }else if (loopTypeList.size() == 0 && typeList.size() != 0){
            System.out.println("进入单一");
            for (GetUserEventsGroupByType type : typeList){
                totalEvents += type.getNum();
                totalMinutes += type.getTotalMinutes();
            }
            for (GetUserEventsGroupByType type : typeList){
                if (type.getType() == 0){
                    if (type.getNum() == 0){
                        percentResult.put("a","0");
                    }
                    percentResult.put("a",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("a",type.getTotalMinutes());
                }
                if (type.getType() == 1){
                    if (type.getNum() == 1){
                        percentResult.put("b","0");
                    }
                    percentResult.put("b",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("b",type.getTotalMinutes());
                }
                if (type.getType() == 2){
                    if (type.getNum() == 2){
                        percentResult.put("c","0");
                    }
                    percentResult.put("c",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("c",type.getTotalMinutes());
                }
                if (type.getType() == 3){
                    if (type.getNum() == 3){
                        percentResult.put("d","0");
                    }
                    percentResult.put("d",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("d",type.getTotalMinutes());
                }
                if (type.getType() == 4){
                    if (type.getNum() == 4){
                        percentResult.put("e","0");
                    }
                    percentResult.put("e",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("e",type.getTotalMinutes());
                }
                if (type.getType() == 5){
                    if (type.getNum() == 5){
                        percentResult.put("f","0");
                    }
                    percentResult.put("f",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("f",type.getTotalMinutes());
                }
                if (type.getType() == 6){
                    if (type.getNum() == 6){
                        percentResult.put("g","0");
                    }
                    percentResult.put("g",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("g",type.getTotalMinutes());
                }
                if (type.getType() == 7){
                    if (type.getNum() == 7){
                        percentResult.put("h","0");
                    }
                    percentResult.put("h",decimalFormat.format((double)type.getNum()/totalEvents));
                    totalMinutesResult.put("h",type.getTotalMinutes());
                }
            }
            showUserAnalysis.setMaxType(eventMapper.getMaxSingleEventType(userId));
            showUserAnalysis.setMinType(eventMapper.getMinSingleEventType(userId));
        }
        showUserAnalysis.setPercentResult(percentResult);
        showUserAnalysis.setTotalEvents(totalEvents);
        showUserAnalysis.setTotalMinutesResult(totalMinutesResult);
        showUserAnalysis.setSumMinutes(totalMinutes);
        return DtoUtil.getSuccesWithDataDto("用户数据统计成功",showUserAnalysis,100000);
    }

    /*@Override
    public Dto showUnfinishedEvents(String userId, String token) {
        return null;
    }

    @Override
    public Dto searchUnfinishedEventsByEventName(String userId,String eventName, String token) {
        return null;
    }

    @Override
    public Dto filtrateUnfinishedEvents(ReceivedEventConditions receivedEventConditions, String token) {
        return null;
    }*/
}
