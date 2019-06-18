package com.modcreater.tmauth.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmauth.service.UserInfoService;
import com.modcreater.tmauth.service.UserServiceJudgeService;
import com.modcreater.tmbeans.databaseparam.QueryEventsCondition;
import com.modcreater.tmbeans.databaseparam.UserEventsGroupByInWeek;
import com.modcreater.tmbeans.databaseresult.GetUserEventsGroupByType;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.dto.EventPersons;
import com.modcreater.tmbeans.pojo.*;
import com.modcreater.tmbeans.show.ShowSingleEvent;
import com.modcreater.tmbeans.show.ShowUserAnalysis;
import com.modcreater.tmbeans.show.userinfo.ShowCompletedEvents;
import com.modcreater.tmbeans.show.userinfo.ShowUserStatistics;
import com.modcreater.tmbeans.utils.NaturalWeek;
import com.modcreater.tmbeans.values.FinalValues;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedAlterUserInfo;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedEventConditions;
import com.modcreater.tmdao.mapper.*;
import com.modcreater.tmutils.DateUtil;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmutils.SingleEventUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.text.NumberFormat;
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
    private UserServiceJudgeService userServiceJudgeService;

    @Resource
    private EventMapper eventMapper;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private UserServiceMapper userServiceMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Dto showUserDetails(String userId, String token) {
        System.out.println("查询用户成就==>" + userId);
        if (StringUtils.isEmpty(userId)) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        String redisToken = stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!StringUtils.hasText(userId)) {
            return DtoUtil.getSuccessDto("没有查询到用户信息", 200000);
        }
        UserStatistics userStatistics = achievementMapper.queryUserStatistics(userId);
        ShowUserStatistics showUserStatistics = new ShowUserStatistics();
        showUserStatistics.setCompleted(userStatistics.getCompleted());
        showUserStatistics.setUnfinished(userStatistics.getUnfinished());
        showUserStatistics.setDrafts(userStatistics.getDrafts());
        List<String> imgUrlList = queryUserAchievementInBase(userId);
        Map<String, Object> result = new HashMap<>(3);
        Account account = accountMapper.queryAccount(userId);
        //用户部分信息
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userName", account.getUserName());
        userInfo.put("userSign", account.getUserSign());
        userInfo.put("headImgURL", account.getHeadImgUrl());
        result.put("userInfo", userInfo);
        //用户事件状态
        result.put("userStatistics", showUserStatistics);
        //用户所有成就
        result.put("imgUrlList", imgUrlList);
        return DtoUtil.getSuccesWithDataDto("查询用户详情成功", result, 100000);
    }

    @Override
    public Dto queryUserAchievement(String userId, String token) {
        if (StringUtils.isEmpty(userId)) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        String redisToken = stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        List<String> result = queryUserAchievementInBase(userId);
        if (result.size() == 0) {
            return DtoUtil.getSuccessDto("该用户还没有任何成就", 100000);
        }
        Map<String, List<String>> imgUrlList = new HashMap<>();
        imgUrlList.put("imgUrlList", result);
        return DtoUtil.getSuccesWithDataDto("查询用户成就成功", imgUrlList, 100000);
    }

    @Override
    public List<String> queryUserAchievementInBase(String userId) {
        //在此查询用户统计表,并判断该用户是否完成某个成就
        UserStatistics userStatistics = achievementMapper.queryUserStatistics(userId);
        List<Achievement> achievementList = achievementMapper.queryAchievement();
        if (!ObjectUtils.isEmpty(userStatistics) && !ObjectUtils.isEmpty(achievementList)) {
            for (Achievement achievement : achievementList) {
                List<UserAchievement> userAchievementList = achievementMapper.queryUserAchievement(userId, achievement.getId());
                if (userAchievementList.size() == 0) {
                    if (userStatistics.getLoggedDays() >= (achievement.getLoggedDaysCondition()).longValue()) {
                        achievementMapper.addNewAchievement(achievement.getId(), userId, DateUtil.dateToStamp(new Date()));
                        continue;
                    }
                    if (achievement.getFinishedEventsCondition().longValue() != 0 && userStatistics.getCompleted() >= achievement.getFinishedEventsCondition().longValue()) {
                        achievementMapper.addNewAchievement(achievement.getId(), userId, DateUtil.dateToStamp(new Date()));
                    }
                }
            }
        }
        return achievementMapper.searchAllAchievement(userId);
    }

    @Override
    public Dto filtrateUserEvents(ReceivedEventConditions receivedEventConditions, String token) {
        if (StringUtils.isEmpty(receivedEventConditions.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        if (ObjectUtils.isEmpty(receivedEventConditions)) {
            return DtoUtil.getFalseDto("筛选条件接收失败", 40002);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedEventConditions.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        System.out.println("查询草稿箱/已完成,未完成");
        QueryEventsCondition singleEventCondition = new QueryEventsCondition();
        if (receivedEventConditions.getUserId() == null || "".equals(receivedEventConditions.getUserId())) {
            return DtoUtil.getFalseDto("条件缺失", 40006);
        }
        singleEventCondition.setUserid(Long.valueOf(receivedEventConditions.getUserId()));
        if (receivedEventConditions.getIsOverdue() == null || "".equals(receivedEventConditions.getIsOverdue())) {
            return DtoUtil.getFalseDto("条件缺失", 40006);
        }
        singleEventCondition.setIsOverdue(Long.valueOf(receivedEventConditions.getIsOverdue()));
        if (receivedEventConditions.getEventName() != null && !"".equals(receivedEventConditions.getEventName())) {
            singleEventCondition.setEventname(receivedEventConditions.getEventName());
        }
        if (receivedEventConditions.getEventType() != null && !"".equals(receivedEventConditions.getEventType())) {
            singleEventCondition.setType(Long.valueOf(receivedEventConditions.getEventType()));
        }
        if (receivedEventConditions.getEventLevel() != null && !"".equals(receivedEventConditions.getEventLevel())) {
            singleEventCondition.setLevel(Long.valueOf(receivedEventConditions.getEventLevel()));
        }
        if (receivedEventConditions.getStartTime() != null && !"".equals(receivedEventConditions.getStartTime())) {
            singleEventCondition.setStarttime(receivedEventConditions.getStartTime());
        }
        if (receivedEventConditions.getEndTime() != null && !"".equals(receivedEventConditions.getEndTime())) {
            singleEventCondition.setEndtime(receivedEventConditions.getEndTime());
        }
        if (receivedEventConditions.getIsOverdue() != null && !"".equals(receivedEventConditions.getIsOverdue())) {
            singleEventCondition.setIsOverdue(Long.valueOf(receivedEventConditions.getIsOverdue()));
        }
        if (!StringUtils.hasText(receivedEventConditions.getStartDate()) || receivedEventConditions.getStartDate().length() != 8) {
            singleEventCondition.setEventid(System.currentTimeMillis() / 1000);
        } else {
            StringBuilder startDate = new StringBuilder(receivedEventConditions.getStartDate());
            singleEventCondition.setYear(Long.valueOf(startDate.substring(0, 4)));
            singleEventCondition.setMonth(Long.valueOf(startDate.substring(4, 6)));
            singleEventCondition.setDay(Long.valueOf(startDate.substring(6, 8)));
        }
        if (!StringUtils.hasText(receivedEventConditions.getPageNum()) || receivedEventConditions.getPageNum().equals("0")) {
            receivedEventConditions.setPageNum("1");
        }
        if (!StringUtils.hasText(receivedEventConditions.getPageSize()) || receivedEventConditions.getPageSize().equals("0")) {
            receivedEventConditions.setPageSize("7");
        }
        //此处判断用户是否开启了查询服务
        if (StringUtils.hasText(receivedEventConditions.getSearchType()) && receivedEventConditions.getSearchType().equals("0") && receivedEventConditions.getIsOverdue().equals("1")){
            if (StringUtils.hasText(receivedEventConditions.getPageNum()) && !receivedEventConditions.getPageNum().equals("1")){
                if (!isSearchServiceNice(receivedEventConditions)) {
                    return DtoUtil.getSuccesWithDataDto("未开通查询服务,不能查看更多", null, 200000);
                }
            }
        }else {
            return DtoUtil.getFalseDto("searchType未接收到", 40005);
        }
        singleEventCondition.setPageNum((Long.valueOf(receivedEventConditions.getPageNum()) - 1) * Long.valueOf(receivedEventConditions.getPageSize()));
        singleEventCondition.setPageSize(Long.valueOf(receivedEventConditions.getPageSize()));
        List<SingleEvent> singleEventList = new ArrayList<>();
        List<ShowCompletedEvents> showCompletedEventsList = new ArrayList<>();
        if (receivedEventConditions.getSearchType() != null && receivedEventConditions.getSearchType().equals("0")) {
            singleEventList = eventMapper.queryEventsByConditions(singleEventCondition);
        } else if (receivedEventConditions.getSearchType() != null && receivedEventConditions.getSearchType().equals("1")) {
            singleEventList = eventMapper.queryDraft(singleEventCondition);
        }
        if (singleEventList.size() == 0) {
            return DtoUtil.getSuccessDto("没有查询到事件", 200000);
        }
        for (SingleEvent singleEvent : singleEventList) {
            if (receivedEventConditions.getPerson() != null && !"".equals(receivedEventConditions.getPerson())) {
                String[] persons;
                String[] personsInResult;
                try {
                    EventPersons eventPersons1 = JSONObject.parseObject(receivedEventConditions.getPerson(), EventPersons.class);
                    persons = eventPersons1.getFriendsId().split(",");
                    EventPersons eventPersons2 = JSONObject.parseObject(singleEvent.getPerson(), EventPersons.class);
                    personsInResult = eventPersons2.getFriendsId().split(",");
                } catch (NullPointerException e) {
                    continue;
                }
                int i = 0;
                if (persons.length > personsInResult.length) {
                    continue;
                }
                for (String sOut : persons) {
                    for (String sInside : personsInResult) {
                        if (sOut.equals(sInside)) {
                            i += 1;
                        }
                    }
                }
                if (i == persons.length) {
                    showCompletedEventsList.add(SingleEventUtil.getShowCompleted(singleEvent));
                }
            } else {
                showCompletedEventsList.add(SingleEventUtil.getShowCompleted(singleEvent));
            }
        }
        return DtoUtil.getSuccesWithDataDto("筛选事件成功", showCompletedEventsList, 100000);
    }

    @Override
    public Dto statisticAnalysisOfData(String userId, String token) {
        if (StringUtils.isEmpty(userId)) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        String redisToken = stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        ShowUserAnalysis showUserAnalysis = new ShowUserAnalysis();
        showUserAnalysis.setUserId(userId);
        //记录的总和
        UserEventsGroupByInWeek userEventsGroupByInWeek = new UserEventsGroupByInWeek();
        userEventsGroupByInWeek.setUserId(userId);
        Long totalEvents = eventMapper.countEvents(userEventsGroupByInWeek);
        //记录事件的总用时分钟数
        Long totalMinutes = 0L;
        Map<String, Double> percentResult = new HashMap<>();
        Map<String, Long> totalMinutesResult = new HashMap<>();
        List<GetUserEventsGroupByType> typeList = eventMapper.getUserEventsGroupByType(userId);
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setRoundingMode(RoundingMode.HALF_UP);
        nf.setMaximumFractionDigits(2);
        for (int i = 0; i <= 7; i++) {
            percentResult.put(FinalValues.TYPE[i], null);
            totalMinutesResult.put(FinalValues.TYPE[i], null);
        }
        for (GetUserEventsGroupByType type : typeList) {
            for (int i = 0; i <= 7; i++) {
                if (type.getType() == i) {
                    percentResult.put(FinalValues.TYPE[i], Double.valueOf(nf.format((double) type.getNum() / totalEvents)));
                    totalMinutesResult.put(FinalValues.TYPE[i], type.getTotalMinutes());
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
        return DtoUtil.getSuccesWithDataDto("用户数据统计成功", showUserAnalysis, 100000);
    }

    @Override
    public Dto weeklyReport(String userId, String token) {
        if (StringUtils.isEmpty(userId)) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        String redisToken = stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        //已完成事件的总分钟数
        Long totalMinutes = 0L;
        //返回数据
        Map<String, Object> allStatistic = new HashMap<>();
        //板块1:扇形图
        //百分比
        Map<String, String> sector = new HashMap<>();
        //时长
        Map<String, String> typeDuration = new HashMap<>();
        //定义小数精度
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setRoundingMode(RoundingMode.HALF_UP);
        nf.setMaximumFractionDigits(2);

        //添加所有类型到百分比,时长中
        for (String s : FinalValues.TYPE) {
            sector.put(s, "0");
            typeDuration.put(s, "0");
        }
        //查询事件数量,用时(根据type分组)
        UserEventsGroupByInWeek userEventsGroupByInWeek = new UserEventsGroupByInWeek();
        userEventsGroupByInWeek.setUserId(userId);
        for (int i = 0; i >= -6; i--) {
            String date = DateUtil.getDay(i);
            if (i == 0) {
                userEventsGroupByInWeek.setTodayYear(date.substring(0, 4));
                userEventsGroupByInWeek.setTodayMonth(date.substring(4, 6));
                userEventsGroupByInWeek.setTodayDay(date.substring(6));
            } else if (i == -1) {
                userEventsGroupByInWeek.setYesterdayYear(date.substring(0, 4));
                userEventsGroupByInWeek.setYesterdayMonth(date.substring(4, 6));
                userEventsGroupByInWeek.setYesterdayDay(date.substring(6));
            } else if (i == -2) {
                userEventsGroupByInWeek.setThirdDayYear(date.substring(0, 4));
                userEventsGroupByInWeek.setThirdDayMonth(date.substring(4, 6));
                userEventsGroupByInWeek.setThirdDayDay(date.substring(6));
            } else if (i == -3) {
                userEventsGroupByInWeek.setFourthDayYear(date.substring(0, 4));
                userEventsGroupByInWeek.setFourthDayMonth(date.substring(4, 6));
                userEventsGroupByInWeek.setFourthDayDay(date.substring(6));
            } else if (i == -4) {
                userEventsGroupByInWeek.setFifthDayYear(date.substring(0, 4));
                userEventsGroupByInWeek.setFifthDayMonth(date.substring(4, 6));
                userEventsGroupByInWeek.setFifthDayDay(date.substring(6));
            } else if (i == -5) {
                userEventsGroupByInWeek.setSixthDayYear(date.substring(0, 4));
                userEventsGroupByInWeek.setSixthDayMonth(date.substring(4, 6));
                userEventsGroupByInWeek.setSixthDayDay(date.substring(6));
            } else if (i == -6) {
                userEventsGroupByInWeek.setSeventhDayYear(date.substring(0, 4));
                userEventsGroupByInWeek.setSeventhDayMonth(date.substring(4, 6));
                userEventsGroupByInWeek.setSeventhDayDay(date.substring(6));
            }
        }
        //已完成的事件的总和
        Long totalEvents = eventMapper.countEvents(userEventsGroupByInWeek);
        List<GetUserEventsGroupByType> typeList = eventMapper.getUserEventsGroupByTypeInWeek(userEventsGroupByInWeek);
        for (GetUserEventsGroupByType type : typeList) {
            for (int i = 0; i < FinalValues.TYPE.length; i++) {
                if (type.getType() == i) {
                    sector.put(FinalValues.TYPE[i], nf.format((double) type.getNum() / totalEvents));
                    typeDuration.put(FinalValues.TYPE[i], type.getTotalMinutes() / 60 + "h" + type.getTotalMinutes() % 60 + "min");
                }
            }
            //将每个类型的事件所占时长统计到总时长中
            totalMinutes += type.getTotalMinutes();
        }
        if (totalMinutes % 60 >= 30) {
            totalMinutes = totalMinutes / 60 + 1;
        }
        totalMinutes = totalMinutes / 60;
        allStatistic.put("totalHours", totalMinutes);
        allStatistic.put("sector", sector);
        allStatistic.put("typeDuration", typeDuration);
        //线形图板块
        List<Map<String, Object>> showWeekEventsNumList = new ArrayList<>();
        Long lastWeek = 0L;
        Long lastLastWeek = 0L;
        List<Map<String, String>> maxTypes = new ArrayList<>();
        List<Map<String, String>> minTypes = new ArrayList<>();
        for (int i = FinalValues.SEARCH_WEEK_NUM; i >= 1; i--) {
            List<NaturalWeek> naturalWeeks = DateUtil.getLastWeekOfNatural(i);
            Map<String, Object> showWeekEventsNum = new HashMap<>();
            Long eventsNum = 0L;
            Long maxMinutes = 0L;
            int maxType = 0;
            Long minMinutes = 10100L;
            int minType = 0;
            for (NaturalWeek naturalWeek : naturalWeeks) {
                naturalWeek.setUserId(userId);
                eventsNum += eventMapper.getEventsNum(naturalWeek);
                if (i == 1) {
                    userEventsGroupByInWeek.setTodayYear(naturalWeek.getYear());
                    userEventsGroupByInWeek.setTodayMonth(naturalWeek.getMonth());
                    userEventsGroupByInWeek.setTodayDay(naturalWeek.getDay());
                } else if (i == 2) {
                    userEventsGroupByInWeek.setYesterdayYear(naturalWeek.getYear());
                    userEventsGroupByInWeek.setYesterdayMonth(naturalWeek.getMonth());
                    userEventsGroupByInWeek.setYesterdayDay(naturalWeek.getDay());
                } else if (i == 3) {
                    userEventsGroupByInWeek.setThirdDayYear(naturalWeek.getYear());
                    userEventsGroupByInWeek.setThirdDayMonth(naturalWeek.getMonth());
                    userEventsGroupByInWeek.setThirdDayDay(naturalWeek.getDay());
                } else if (i == 4) {
                    userEventsGroupByInWeek.setFourthDayYear(naturalWeek.getYear());
                    userEventsGroupByInWeek.setFourthDayMonth(naturalWeek.getMonth());
                    userEventsGroupByInWeek.setFourthDayDay(naturalWeek.getDay());
                } else if (i == 5) {
                    userEventsGroupByInWeek.setFifthDayYear(naturalWeek.getYear());
                    userEventsGroupByInWeek.setFifthDayMonth(naturalWeek.getMonth());
                    userEventsGroupByInWeek.setFifthDayDay(naturalWeek.getDay());
                } else if (i == 6) {
                    userEventsGroupByInWeek.setSixthDayYear(naturalWeek.getYear());
                    userEventsGroupByInWeek.setSixthDayMonth(naturalWeek.getMonth());
                    userEventsGroupByInWeek.setSixthDayDay(naturalWeek.getDay());
                } else if (i == 7) {
                    userEventsGroupByInWeek.setSeventhDayYear(naturalWeek.getYear());
                    userEventsGroupByInWeek.setSeventhDayMonth(naturalWeek.getMonth());
                    userEventsGroupByInWeek.setSeventhDayDay(naturalWeek.getDay());
                }
            }
            List<GetUserEventsGroupByType> typeList1 = eventMapper.getUserEventsGroupByTypeInWeek(userEventsGroupByInWeek);
            for (int ttt = 0; ttt < typeList1.size(); ttt++) {
                Long minutes = typeList1.get(ttt).getTotalMinutes();
                System.out.println("minutes:" + minutes);
                if (minutes > maxMinutes) {
                    maxMinutes = minutes;
                    maxType = ttt;
                }
                if (minutes < minMinutes) {
                    minMinutes = minutes;
                    minType = ttt;
                }
            }
            Map<String, String> xTypes = new HashMap<>();
            xTypes.put("date", Long.valueOf(naturalWeeks.get(0).getMonth())
                    + "." + Long.valueOf(naturalWeeks.get(0).getDay())
                    + "~" + Long.valueOf(naturalWeeks.get(naturalWeeks.size() - 1).getMonth())
                    + "." + Long.valueOf(naturalWeeks.get(naturalWeeks.size() - 1).getDay()));
            xTypes.put("type", FinalValues.TYPE[maxType]);
            maxTypes.add(xTypes);
            Map<String, String> nTypes = new HashMap<>();
            nTypes.put("date", Long.valueOf(naturalWeeks.get(0).getMonth())
                    + "." + Long.valueOf(naturalWeeks.get(0).getDay())
                    + "~" + Long.valueOf(naturalWeeks.get(naturalWeeks.size() - 1).getMonth())
                    + "." + Long.valueOf(naturalWeeks.get(naturalWeeks.size() - 1).getDay()));
            nTypes.put("type", FinalValues.TYPE[minType]);
            minTypes.add(nTypes);
            showWeekEventsNum.put("totalEvents", eventsNum);
            showWeekEventsNum.put("startDateAndEndDate", Long.valueOf(naturalWeeks.get(0).getMonth())
                    + "." + Long.valueOf(naturalWeeks.get(0).getDay())
                    + "~" + Long.valueOf(naturalWeeks.get(naturalWeeks.size() - 1).getMonth())
                    + "." + Long.valueOf(naturalWeeks.get(naturalWeeks.size() - 1).getDay()));
            showWeekEventsNumList.add(showWeekEventsNum);
            if (i == 2) {
                lastLastWeek = eventsNum;
            }
            if (i == 1) {
                lastWeek = eventsNum;
            }
        }
        if (lastLastWeek == 0) {
            lastLastWeek = 1L;
        }
        allStatistic.put("showWeekEventsNumList", showWeekEventsNumList);
        allStatistic.put("lastWeekContrastEarlier", nf.format((double) (lastWeek - lastLastWeek) / lastLastWeek));
        //周事件统计
        List<Map<String, Object>> frontSevenDays = new ArrayList<>();
        Long maxEventNum = 0L;
        Long totalEventsNum = 0L;
        for (int i = -6; i <= 0; i++) {
            String day = DateUtil.getDay(i);
            StringBuilder stringBuilder = new StringBuilder(day);
            NaturalWeek naturalWeek = new NaturalWeek();
            naturalWeek.setUserId(userId);
            naturalWeek.setYear(stringBuilder.substring(0, 4));
            naturalWeek.setMonth(stringBuilder.substring(4, 6));
            naturalWeek.setDay(stringBuilder.substring(6));
            Long num = eventMapper.getEventsNum(naturalWeek);
            totalEventsNum += num;
            Map<String, Object> map = new HashMap<>();
            map.put("date", Long.valueOf(stringBuilder.substring(4, 6)) + "." + Long.valueOf(stringBuilder.substring(6)));
            map.put("num", num);
            frontSevenDays.add(map);
            if (num > maxEventNum) {
                maxEventNum = num;
            }
        }
        allStatistic.put("frontSevenDays", frontSevenDays);
        allStatistic.put("maxEventsNum", maxEventNum);
        allStatistic.put("avgEventsNum", totalEventsNum / 7);
        allStatistic.put("maxTypes", maxTypes);
        allStatistic.put("minTypes", minTypes);
        List<Map<String, String>> myBestFriends = new ArrayList<>();
        List<String> persons = eventMapper.queryEventInBestFriends(userId);
        Long maxEvents = 0L;
        Long friendId = 100000L;

        List<Long> friendIdsList = accountMapper.queryAllFriendList(userId);
        Map<Long, Long> completeEventsTogether = new HashMap<>();
        for (Long friend : friendIdsList) {
            completeEventsTogether.put(friend, 0L);
        }
        for (String person : persons) {
            EventPersons eventPersons = JSONObject.parseObject(person, EventPersons.class);
            String[] friendIds;
            try {
                friendIds = eventPersons.getFriendsId().split(",");
            } catch (NullPointerException e) {
                continue;
            }
            for (String s : friendIds) {
                if (s == null || s.equals("")) {
                    continue;
                }
                Long key = Long.valueOf(s);
                if (ObjectUtils.isEmpty(completeEventsTogether.get(key))) {
                    continue;
                }
                completeEventsTogether.put(key, completeEventsTogether.get(key) + 1);
            }
        }
        Set<Long> sets = completeEventsTogether.keySet();
        List<Long> friends = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            for (Long set : sets) {
                Long num = completeEventsTogether.get(set);
                if (num > maxEvents) {
                    maxEvents = num;
                    friendId = set;

                }
            }
            completeEventsTogether.remove(friendId);
            friends.add(friendId);
            maxEvents = 0L;
            friendId = 100000L;
        }
        for (Long id : friends) {
            Map<String, String> map = new HashMap<>();
            if (id != 100000) {
                Account accounts = accountMapper.queryNameAndHead(id);
                map.put("userName", accounts.getUserName());
                map.put("headImgUrl", accounts.getHeadImgUrl());
            }
            myBestFriends.add(map);
        }
        allStatistic.put("myBestFriends", myBestFriends);
        return DtoUtil.getSuccesWithDataDto("查询成功", allStatistic, 100000);
    }

    @Override
    public Dto myWeek(String userId, String token) {
        if (StringUtils.isEmpty(userId)) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(userId))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        List<ShowSingleEvent> weekLists = new ArrayList<>();
        for (int i = 0; i >= -6; i--) {
            List<ShowSingleEvent> singleEventList = SingleEventUtil.getShowSingleEventList(eventMapper.queryCompletedEvents(SingleEventUtil.getSingleEvent(userId, DateUtil.getDay(i))));
            if (singleEventList.size() != 0) {
                for (ShowSingleEvent singleEvent : singleEventList) {
                    weekLists.add(singleEvent);
                }
            }
        }
        if (weekLists.size() == 0) {
            return DtoUtil.getSuccessDto("未查询到数据", 200000);
        }
        return DtoUtil.getSuccesWithDataDto("查询我的一周成功", weekLists, 100000);
    }

    @Override
    public Dto alterUserSign(ReceivedAlterUserInfo receivedAlterUserInfo, String token) {
        if (StringUtils.isEmpty(receivedAlterUserInfo.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedAlterUserInfo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        int i = accountMapper.alterUserInfo(receivedAlterUserInfo.getUserId(), receivedAlterUserInfo.getUserSign(), receivedAlterUserInfo.getUserName(), receivedAlterUserInfo.getHeadImgUrl());
        if (i != 0) {
            return DtoUtil.getSuccessDto("修改成功", 100000);
        }
        return DtoUtil.getFalseDto("修改失败", 200000);
    }

    public boolean isSearchServiceNice(ReceivedEventConditions receivedEventConditions) {
        //此处判断用户是否开启了查询服务
        ServiceRemainingTime time = userServiceMapper.getServiceRemainingTime(receivedEventConditions.getUserId(), "2");
        //用户未开通
        if (ObjectUtils.isEmpty(time)) {
            return false;
        }
        //开通了,查询次卡是否有剩余
        if (time.getResidueDegree() == 0) {
            //无剩余,判断剩余年/月卡时间
            Long timeRemaining = time.getTimeRemaining();
            if (timeRemaining == 0 || timeRemaining < System.currentTimeMillis() / 1000) {
                return false;
            }
        } else {
            time.setResidueDegree(time.getResidueDegree() - 1);
            //判断剩余次数-1后是否为0,如果为0...
            if (time.getResidueDegree() == 0 && time.getStorageTime() != 0) {
                //如果有库存时间,将这个时间加入用户有效的剩余时间中
                time.setTimeRemaining(System.currentTimeMillis() / 1000 + time.getStorageTime());
                long l = 0L;
                time.setStorageTime(l);
            }
        }
        userServiceMapper.updateServiceRemainingTime(time);
        return true;
    }
}
