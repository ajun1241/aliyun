package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.UserInfoService;
import com.modcreater.tmbeans.databaseparam.QueryEventsCondition;
import com.modcreater.tmbeans.databaseresult.GetUserEventsGroupByType;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Achievement;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.pojo.UserAchievement;
import com.modcreater.tmbeans.pojo.UserStatistics;
import com.modcreater.tmbeans.show.ShowSingleEvent;
import com.modcreater.tmbeans.show.ShowUserAnalysis;
import com.modcreater.tmbeans.show.userinfo.ShowCompletedEvents;
import com.modcreater.tmbeans.show.userinfo.ShowUserDetails;
import com.modcreater.tmbeans.show.userinfo.ShowUserStatistics;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedEventConditions;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedIdIsOverdue;
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
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private static final String[] TYPE = {"a","b","c","d","e","f","g","h"};
    private static final String[] ONEWEEKINNUM = {"a","b","c","d","e","f","g"};

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
    public Dto showUserEvents(ReceivedIdIsOverdue receivedIdIsOverdue, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(receivedIdIsOverdue.getUserId());
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        if (!StringUtils.hasText(receivedIdIsOverdue.getIsOverdue())){
            return DtoUtil.getFalseDto("事件状态未获取到",40005);
        }
        //查询用户已完成的事件(根据时间排序,如果用户未开通该服务,则只显示7条)
        if (1==1){
            receivedIdIsOverdue.setPageNum(0L);
            receivedIdIsOverdue.setPageSize(7L);
        }
        List<SingleEvent> singleEventList = eventMapper.queryUserEventsByUserIdIsOverdue(receivedIdIsOverdue);
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
                try {
                    if (userStatistics.getLoggedDays() == (achievement.getLoggedDaysCondition()).longValue()) {
                        achievementMapper.addNewAchievement(achievement.getId(),userId,DateUtil.dateToStamp(new Date()));
                    }
                    if (userStatistics.getCompleted() == achievement.getFinishedEventsCondition().longValue()){
                        achievementMapper.addNewAchievement(achievement.getId(),userId,DateUtil.dateToStamp(new Date()));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return achievementMapper.searchAllAchievement(userId);
    }

    @Override
    public Dto filtrateUserEvents(ReceivedEventConditions receivedEventConditions, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(receivedEventConditions)){
            return DtoUtil.getFalseDto("筛选条件接收失败",40002);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(receivedEventConditions.getUserId());
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        //此处判断用户是否开启了该项服务
        if (1==0){
            return DtoUtil.getSuccessDto("您还没有开通该服务",100000);
        }
        List<String> list = new ArrayList();
        list.add("userId");
        list.add("appType");
        list.add("isOverdue");
        //判断条件除userId和appType是否全部为空
        if (SingleEventUtil.isAllPropertiesEmpty(receivedEventConditions,list)){
            ReceivedIdIsOverdue receivedIdIsOverdue = new ReceivedIdIsOverdue();
            receivedIdIsOverdue.setUserId(receivedEventConditions.getUserId());
            receivedIdIsOverdue.setIsOverdue(receivedEventConditions.getIsOverdue());
            receivedIdIsOverdue.setPageNum(0L);
            receivedIdIsOverdue.setPageSize(7L);
            return showUserEvents(receivedIdIsOverdue,token);
        }
        QueryEventsCondition singleEventCondition = new QueryEventsCondition();
        if (receivedEventConditions.getEventName() != null && !"".equals(receivedEventConditions.getEventName())){
            singleEventCondition.setEventname(receivedEventConditions.getEventName());
        }
        if (receivedEventConditions.getEventType() != null && !"".equals(receivedEventConditions.getEventType())){
            singleEventCondition.setType(Long.valueOf(receivedEventConditions.getEventType()));
        }
        if (receivedEventConditions.getEventLevel() != null && !"".equals(receivedEventConditions.getEventLevel())){
            singleEventCondition.setLevel(Long.valueOf(receivedEventConditions.getEventLevel()));
        }
        if (receivedEventConditions.getStartTime() != null && !"".equals(receivedEventConditions.getStartTime())){
            singleEventCondition.setStarttime(receivedEventConditions.getStartTime());
        }
        if (receivedEventConditions.getEndTime() != null && !"".equals(receivedEventConditions.getEndTime())){
            singleEventCondition.setEndtime(receivedEventConditions.getEndTime());
        }
        if (receivedEventConditions.getIsOverdue() != null && !"".equals(receivedEventConditions.getIsOverdue())){
            singleEventCondition.setIsOverdue(Long.valueOf(receivedEventConditions.getIsOverdue()));
        }
        if (receivedEventConditions.getStartDate().length() != 8){
            receivedEventConditions.setStartDate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        }
        StringBuilder startDate = new StringBuilder(receivedEventConditions.getStartDate());
        singleEventCondition.setYear(Long.valueOf(startDate.substring(0,4)));
        singleEventCondition.setMonth(Long.valueOf(startDate.substring(4,6)));
        singleEventCondition.setDay(Long.valueOf(startDate.substring(6,8)));
        if (receivedEventConditions.getPageNum() == 0){
            receivedEventConditions.setPageNum(1L);
        }
        singleEventCondition.setPageNum((receivedEventConditions.getPageNum()-1)*receivedEventConditions.getPageSize());
        singleEventCondition.setPageSize(receivedEventConditions.getPageSize());
        List<SingleEvent> singleEventList = eventMapper.queryEventsByConditions(singleEventCondition);
        if (singleEventList.size() != 0){
            List<ShowCompletedEvents> showCompletedEventsList = new ArrayList<>();
            for (SingleEvent singleEvent : singleEventList){
                if (receivedEventConditions.getPerson() != null && !"".equals(receivedEventConditions.getPerson())){
                    String[] persons = receivedEventConditions.getPerson().split(",");
                    String[] personsInResult = singleEvent.getPerson().split(",");
                    if (persons.length > personsInResult.length){
                        continue;
                    }
                    int i = 0;
                    for (String sOut : persons){
                        for (String sInside :personsInResult){
                            if (sOut.equals(sInside)){
                                i += 1;
                            }
                        }
                    }
                    if (i == persons.length){
                        ShowCompletedEvents showCompletedEvents = new ShowCompletedEvents();
                        showCompletedEvents.setEventId(singleEvent.getEventid().toString());
                        showCompletedEvents.setUserId(singleEvent.getUserid().toString());
                        showCompletedEvents.setEventName(singleEvent.getEventname());
                        showCompletedEvents.setDate(singleEvent.getYear().toString()+"-"+singleEvent.getMonth()+"-"+singleEvent.getDay());
                        showCompletedEventsList.add(showCompletedEvents);
                    }
                }
            }
            return DtoUtil.getSuccesWithDataDto("筛选已完成事件成功",showCompletedEventsList,100000);
        }
        return DtoUtil.getSuccessDto("没有查询到事件",200000);
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
        DecimalFormat format = new DecimalFormat("0.00");
        //记录的总和
        Long totalEvents = eventMapper.countEvents(userId);
        //记录事件的总用时分钟数
        Long totalMinutes = 0L;
        Map<String,Double> percentResult = new HashMap<>();
        Map<String,Long> totalMinutesResult = new HashMap<>();
        List<GetUserEventsGroupByType> typeList = eventMapper.getUserEventsGroupByType(userId);
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        for (int i = 0 ;i <= 7; i++){
            percentResult.put(TYPE[i],null);
            totalMinutesResult.put(TYPE[i],null);
        }
        for (GetUserEventsGroupByType type : typeList){
            for (int i = 0 ;i <= 7; i++){
                if (type.getType() == i){
                    percentResult.put(TYPE[i],Double.valueOf(nf.format((double)type.getNum()/totalEvents)));
                    totalMinutesResult.put(TYPE[i],type.getTotalMinutes());
                }
            }
            totalMinutes += type.getTotalMinutes();
        }
        showUserAnalysis.setMaxType(eventMapper.getMaxSingleEventType(userId));
        showUserAnalysis.setMinType(eventMapper.getMinSingleEventType(userId));
        showUserAnalysis.setPercentResult(percentResult);
        showUserAnalysis.setTotalEvents(totalEvents);
        showUserAnalysis.setTotalMinutesResult(totalMinutesResult);
        showUserAnalysis.setSumMinutes(totalMinutes);
        return DtoUtil.getSuccesWithDataDto("用户数据统计成功",showUserAnalysis,100000);
    }

    @Override
    public Dto myWeek(String userId, String token) {
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        String redisToken = stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)) {
            return DtoUtil.getFalseDto("token过期请先登录", 21014);
        }
        SingleEvent condition = new SingleEvent();
        condition.setUserid(Long.valueOf(userId));
        List<ShowSingleEvent> weekLists = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime((simpleDateFormat).parse(simpleDateFormat.format(new Date())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (int i = 0; i <= 6; i++) {
            if (i != 0) {
                calendar.add(Calendar.DATE, -1);
            }
            StringBuffer startDate = new StringBuffer(simpleDateFormat.format(calendar.getTime()));
            condition.setYear(Long.valueOf(startDate.substring(0, 4)));
            condition.setMonth(Long.valueOf(startDate.substring(4, 6)));
            condition.setDay(Long.valueOf(startDate.substring(6, 8)));
            List<SingleEvent> singleEventList = eventMapper.queryEvents(condition);
            if (singleEventList.size() > 0){
                for (SingleEvent singleEvent : singleEventList){
                    weekLists.add(SingleEventUtil.getShowSingleEvent(singleEvent));
                }
            }
        }
        if (weekLists.size() == 0) {
            return DtoUtil.getSuccessDto("未查询到数据", 100000);
        }
        return DtoUtil.getSuccesWithDataDto("查询我的一周成功", weekLists, 100000);
    }
}
