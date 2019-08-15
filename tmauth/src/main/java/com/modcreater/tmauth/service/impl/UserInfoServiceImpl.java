package com.modcreater.tmauth.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmauth.config.annotation.Safety;
import com.modcreater.tmauth.service.UserInfoService;
import com.modcreater.tmauth.service.UserServiceJudgeService;
import com.modcreater.tmbeans.databaseparam.QueryEventsCondition;
import com.modcreater.tmbeans.databaseparam.UserEventsGroupByInWeek;
import com.modcreater.tmbeans.databaseresult.GetUserEventsGroupByPriority;
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
import com.modcreater.tmbeans.vo.userinfovo.*;
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
import java.lang.reflect.Array;
import java.math.RoundingMode;
import java.text.NumberFormat;
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
    private UserServiceJudgeService userServiceJudgeService;

    @Resource
    private EventMapper eventMapper;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private UserServiceMapper userServiceMapper;

    @Resource
    private SynchronHistoryMapper synchronHistoryMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Dto showUserDetails(String userId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(userId))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        ShowUserStatistics showUserStatistics = new ShowUserStatistics();
        showUserStatistics.setCompleted(eventMapper.countCompletedEvents(Long.valueOf(userId)));
        showUserStatistics.setUnfinished(eventMapper.countUnfinishedEvents(Long.valueOf(userId)));
        //如果用户开通了备份服务且可以正常使用,显示用户草稿箱数量,否则显示0
        showUserStatistics.setDrafts(userServiceJudgeService.backupServiceJudge(userId, token).getResCode() == 100000 ? eventMapper.countDrafts(Long.valueOf(userId)) : 0);
        Map<String, Object> result = new HashMap<>(3);
        //用户事件状态
        result.put("userStatistics", showUserStatistics);
        //用户所有成就
        return DtoUtil.getSuccesWithDataDto("查询用户详情成功", result, 100000);
    }

    @Override
    public Dto queryUserAchievement(String userId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(userId))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        //查询用户所有成就
        List<Achievement> result = queryUserAchievementInBase(userId);
        if (result.size() == 0) {
            return DtoUtil.getSuccessDto("该用户还没有任何成就", 100000);
        }
        Map<String, List<Achievement>> imgUrlList = new HashMap<>();
        imgUrlList.put("imgUrlList", result);
        return DtoUtil.getSuccesWithDataDto("查询用户成就成功", imgUrlList, 100000);
    }

    @Override
    public Dto getAchievementNum(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        Map<String,Long> result = new HashMap<>(2);
        result.put("achievedNum",achievementMapper.getAchievedNum(receivedId.getUserId()));
        result.put("totalNum",achievementMapper.getTotalNum(receivedId.getUserId()));
        return DtoUtil.getSuccesWithDataDto("查询成功",result,100000);
    }

    @Override
    public List<Achievement> queryUserAchievementInBase(String userId) {
        //在此查询用户统计表,并判断该用户是否完成某个成就
        UserStatistics userStatistics = achievementMapper.queryUserStatistics(userId);
        //查询已存在的所有成就用于判断
        List<Achievement> achievementList = achievementMapper.queryAchievement();
        if (!ObjectUtils.isEmpty(userStatistics) && !ObjectUtils.isEmpty(achievementList)) {
            for (Achievement achievement : achievementList) {
                //查询用户对应本次遍历的成就是否存在,不存在就判断是否满足条件
                List<UserAchievement> userAchievementList = achievementMapper.queryUserAchievement(userId, achievement.getId());
                if (userAchievementList.size() == 0) {
                    if (achievement.getType() == 1 && userStatistics.getLoggedDays() >= (achievement.getCondition()).longValue()) {
                        achievementMapper.addNewAchievement(achievement.getId(), userId, DateUtil.dateToStamp(new Date()));
                    }else if (achievement.getType() == 2 && eventMapper.getUserAllEvent(userId) >= (achievement.getCondition()).longValue()){
                        achievementMapper.addNewAchievement(achievement.getId(), userId, DateUtil.dateToStamp(new Date()));
                    }/*else if (achievement.getType() == 3 && eventMapper.countCompletedEvents(Long.valueOf(userId)) >= (achievement.getCondition()).longValue()){
                        achievementMapper.addNewAchievement(achievement.getId(), userId, DateUtil.dateToStamp(new Date()));
                    }*/else if (achievement.getType() == 4 && accountMapper.countAllMyFriends(userId) >= (achievement.getCondition()).longValue()){
                        achievementMapper.addNewAchievement(achievement.getId(), userId, DateUtil.dateToStamp(new Date()));
                    }
                }
            }
        }
        //返回该用户所有成就对应图片地址的集合
        return achievementMapper.searchAllAchievement(userId);
    }

    @Override
    public Dto filtrateUserEvents(ReceivedEventConditions receivedEventConditions, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedEventConditions.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (ObjectUtils.isEmpty(receivedEventConditions)) {
            return DtoUtil.getFalseDto("筛选条件接收失败", 40002);
        }
        //判断必要条件是否存在
        QueryEventsCondition singleEventCondition = new QueryEventsCondition();
        if (receivedEventConditions.getUserId() == null || "".equals(receivedEventConditions.getUserId())) {
            return DtoUtil.getFalseDto("条件缺失", 40006);
        }
        singleEventCondition.setUserid(Long.valueOf(receivedEventConditions.getUserId()));
        if (receivedEventConditions.getIsOverdue() == null || "".equals(receivedEventConditions.getIsOverdue())) {
            return DtoUtil.getFalseDto("条件缺失", 40006);
        }
        if (!StringUtils.hasText(receivedEventConditions.getSearchType())) {
            return DtoUtil.getFalseDto("条件缺失", 40006);
        }
        if (!StringUtils.hasText(receivedEventConditions.getIsOverdue())) {
            return DtoUtil.getFalseDto("条件缺失", 40006);
        }
        //将符合规则的条件一一放入新对象等待查询
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
        //如果传入的时间不符合规则,将使用当前时间代替
        if (!StringUtils.hasText(receivedEventConditions.getStartDate()) || receivedEventConditions.getStartDate().length() != 8) {
            singleEventCondition.setEventid(System.currentTimeMillis() / 1000);
        } else {
            StringBuilder startDate = new StringBuilder(receivedEventConditions.getStartDate());
            singleEventCondition.setYear(Long.valueOf(startDate.substring(0, 4)));
            singleEventCondition.setMonth(Long.valueOf(startDate.substring(4, 6)));
            singleEventCondition.setDay(Long.valueOf(startDate.substring(6, 8)));
        }
        //将不符合规则的页码和显示数量改为默认第一页显示7行
        if (!StringUtils.hasText(receivedEventConditions.getPageNum()) || receivedEventConditions.getPageNum().equals("0")) {
            receivedEventConditions.setPageNum("1");
        }
        if (!StringUtils.hasText(receivedEventConditions.getPageSize()) || !receivedEventConditions.getPageSize().equals("7")) {
            receivedEventConditions.setPageSize("7");
        }
        //此处判断用户是否开启了查询服务
        if (StringUtils.hasText(receivedEventConditions.getSearchType()) && receivedEventConditions.getSearchType().equals("0") && receivedEventConditions.getIsOverdue().equals("1")) {
            if (StringUtils.hasText(receivedEventConditions.getPageNum()) && !receivedEventConditions.getPageNum().equals("1")) {
                if (!isSearchServiceNice(receivedEventConditions)) {
                    return DtoUtil.getSuccesWithDataDto("未开通查询服务,不能查看更多", null, 12003);
                }
            }
        }
        singleEventCondition.setPageNum((Long.valueOf(receivedEventConditions.getPageNum()) - 1) * Long.valueOf(receivedEventConditions.getPageSize()));
        singleEventCondition.setPageSize(Long.valueOf(receivedEventConditions.getPageSize()));
        List<SingleEvent> singleEventList = new ArrayList<>();
        List<ShowCompletedEvents> showCompletedEventsList = new ArrayList<>();
        //searchType为0时查看普通事件,1为查看草稿箱
        if (receivedEventConditions.getSearchType() != null && receivedEventConditions.getSearchType().equals("0")) {
            singleEventList = eventMapper.queryEventsByConditions(singleEventCondition);
        } else if (receivedEventConditions.getSearchType() != null && receivedEventConditions.getSearchType().equals("1")) {
            singleEventList = eventMapper.queryDraft(singleEventCondition);
        }
        if (singleEventList.size() == 0) {
            return DtoUtil.getSuccessDto("没有查询到事件", 200000);
        }
        //以下是对person字段的特殊筛选处理
        for (SingleEvent singleEvent : singleEventList) {
            if (receivedEventConditions.getPerson() != null && !"".equals(receivedEventConditions.getPerson())) {
                //该person为传入的条件
                String[] persons;
                //该person为查询结果
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
                //如果条件人数多于查询结果人数,则不符合查询结果
                if (persons.length > personsInResult.length) {
                    continue;
                }
                //此处将i作为person字段条件与结果的匹配成功次数
                for (String sOut : persons) {
                    for (String sInside : personsInResult) {
                        if (sOut.equals(sInside)) {
                            i += 1;
                        }
                    }
                }
                //如果成功次数和传入条件人数相等则本次查询成功
                if (i == persons.length) {
                    showCompletedEventsList.add(SingleEventUtil.getShowCompleted(singleEvent));
                }
            } else {
                //如果person没有值则默认查询所有
                showCompletedEventsList.add(SingleEventUtil.getShowCompleted(singleEvent));
            }
        }
        return DtoUtil.getSuccesWithDataDto("筛选事件成功", showCompletedEventsList, 100000);
    }

    @Override
    public Dto statisticAnalysisOfData(String userId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(userId))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        ShowUserAnalysis showUserAnalysis = new ShowUserAnalysis();
        showUserAnalysis.setUserId(userId);
        UserEventsGroupByInWeek userEventsGroupByInWeek = new UserEventsGroupByInWeek();
        userEventsGroupByInWeek.setUserId(userId);
        //查询已完成事件的总数
        Long totalEvents = eventMapper.countEvents(userEventsGroupByInWeek);
        //记录事件的总用时分钟数
        Long totalMinutes = 0L;
        //类型:结果百分比
        Map<String, Double> percentResult = new HashMap<>();
        //类型:总用时
        Map<String, Long> totalMinutesResult = new HashMap<>();
        //将type分组
        List<GetUserEventsGroupByType> typeList = eventMapper.getUserEventsGroupByType(userId);
        //格式化运算结果
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setRoundingMode(RoundingMode.HALF_UP);
        nf.setMaximumFractionDigits(2);
        //将作为结果传出的map给予默认值
        for (int i = 0; i <= 7; i++) {
            percentResult.put(FinalValues.TYPE[i], null);
            totalMinutesResult.put(FinalValues.TYPE[i], null);
        }
        //运算
        for (GetUserEventsGroupByType type : typeList) {
            for (int i = 0; i <= 7; i++) {
                if (type.getType() == i) {
                    percentResult.put(FinalValues.TYPE[i], Double.valueOf(nf.format((double) type.getNum() / totalEvents)));
                    totalMinutesResult.put(FinalValues.TYPE[i], type.getTotalMinutes());
                }
            }
            totalMinutes += type.getTotalMinutes();
        }
        //赋值
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
        if (!token.equals(stringRedisTemplate.opsForValue().get(userId))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        Dto dto = userServiceJudgeService.annualReportingServiceJudge(userId, token);
        if (dto.getResCode() != 100000) {
            return DtoUtil.getSuccessDto("尚未开通报表服务", 200000);
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
            //获取当前下标日期
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
        //根据type分组查询
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
        //将多出来的大于等于30分钟的时间变为小时+1
        if (totalMinutes % 60 >= 30) {
            totalMinutes = totalMinutes / 60 + 1;
        }
        totalMinutes = totalMinutes / 60;
        allStatistic.put("totalHours", totalMinutes);
        allStatistic.put("sector", sector);
        allStatistic.put("typeDuration", typeDuration);
        //线形图板块
        List<Map<String, Object>> showWeekEventsNumList = new ArrayList<>();
        //上周
        Long lastWeek = 0L;
        //上上周
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
        allStatistic.put("myBestFriends", getMyBestFriendList(userId, 1));
        return DtoUtil.getSuccesWithDataDto("查询成功", allStatistic, 100000);
    }

    @Override
    public Dto weeklyReport2(String userId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(userId))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        Account account = accountMapper.queryAccount(userId);
        if (System.currentTimeMillis() - account.getCreateDate().getTime() <= 24 * 60 * 60 * 1000 * 7) {
            return DtoUtil.getSuccesWithDataDto("使用时间不够,暂时无法生成报表", null, 13045);
        }
        //定义小数精度
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setRoundingMode(RoundingMode.HALF_UP);
        nf.setMaximumFractionDigits(1);
        Map<String, Object> result = new HashMap<>();
        //title
        List<NaturalWeek> naturalWeeks = DateUtil.getLastWeekOfNatural(1);
        NaturalWeek firstDay = naturalWeeks.get(0);
        NaturalWeek lastDay = naturalWeeks.get(6);
        result.put("title", firstDay.getYear() + "." + firstDay.getMonth() + "." + firstDay.getDay() + "-" + lastDay.getMonth() + "." + lastDay.getDay());
        //mod1
        UserEventsGroupByInWeek userEventsGroupByInWeek = new UserEventsGroupByInWeek();
        userEventsGroupByInWeek.setUserId(userId);
        userEventsGroupByInWeek.setTodayDay(naturalWeeks.get(0).getDay());
        userEventsGroupByInWeek.setTodayMonth(naturalWeeks.get(0).getMonth());
        userEventsGroupByInWeek.setTodayYear(naturalWeeks.get(0).getYear());
        userEventsGroupByInWeek.setYesterdayDay(naturalWeeks.get(1).getDay());
        userEventsGroupByInWeek.setYesterdayMonth(naturalWeeks.get(1).getMonth());
        userEventsGroupByInWeek.setYesterdayYear(naturalWeeks.get(1).getYear());
        userEventsGroupByInWeek.setThirdDayDay(naturalWeeks.get(2).getDay());
        userEventsGroupByInWeek.setThirdDayMonth(naturalWeeks.get(2).getMonth());
        userEventsGroupByInWeek.setThirdDayYear(naturalWeeks.get(2).getYear());
        userEventsGroupByInWeek.setFourthDayDay(naturalWeeks.get(3).getDay());
        userEventsGroupByInWeek.setFourthDayMonth(naturalWeeks.get(3).getMonth());
        userEventsGroupByInWeek.setFourthDayYear(naturalWeeks.get(3).getYear());
        userEventsGroupByInWeek.setFifthDayDay(naturalWeeks.get(4).getDay());
        userEventsGroupByInWeek.setFifthDayMonth(naturalWeeks.get(4).getMonth());
        userEventsGroupByInWeek.setFifthDayYear(naturalWeeks.get(4).getYear());
        userEventsGroupByInWeek.setSixthDayDay(naturalWeeks.get(5).getDay());
        userEventsGroupByInWeek.setSixthDayMonth(naturalWeeks.get(5).getMonth());
        userEventsGroupByInWeek.setSixthDayYear(naturalWeeks.get(5).getYear());
        userEventsGroupByInWeek.setSeventhDayDay(naturalWeeks.get(6).getDay());
        userEventsGroupByInWeek.setSeventhDayMonth(naturalWeeks.get(6).getMonth());
        userEventsGroupByInWeek.setSeventhDayYear(naturalWeeks.get(6).getYear());
        Long totalEvents = eventMapper.countEvents(userEventsGroupByInWeek);
        if (totalEvents <= 5) {
            DtoUtil.getFalseDto("数据量太小", 200000);
        }
        List<GetUserEventsGroupByType> typeList = eventMapper.getUserEventsGroupByTypeInWeek(userEventsGroupByInWeek);
        Map<String, Object> mod1 = new HashMap<>();
        List<Map<String, Object>> mod1F = new ArrayList<>();
        List<Long> types = new ArrayList<>();
        for (GetUserEventsGroupByType type : typeList) {
            for (int i = 0; i < 8; i++) {
                if (type.getType() == i) {
                    Double percent = Double.valueOf(nf.format((double) type.getNum() / totalEvents * 100));
                    Map<String, Object> map = new HashMap<>();
                    map.put("type", i);
                    map.put("typeName", SingleEventUtil.getTypeValues(FinalValues.TYPE[i]));
                    map.put("typeValue", percent);
                    mod1F.add(map);
                    types.add(type.getType());
                }
            }
        }
        List<Long> dingchang = new ArrayList<>();
        for (long i = 0; i < 8; i++) {
            dingchang.add(i);
        }
        for (Long d : dingchang) {
            int dex = 0;
            for (Long l : types) {
                if (!d.equals(l)) {
                    dex += 1;
                }
            }
            if (dex == types.size()) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", d);
                map.put("typeName", SingleEventUtil.getTypeValues(FinalValues.TYPE[d.intValue()]));
                map.put("typeValue", 0);
                mod1F.add(map);
            }
        }
        mod1.put("mod1F", mod1F);
        List<Map<String, Object>> mod1S = new ArrayList<>();
        Double four = 100.0;
        for (int i = 0; i <= 4; i++) {
            Map<String, Object> map = new HashMap<>();
            Integer typeIndex = Integer.valueOf(mod1F.get(i).get("type").toString());
            map.put("typeName", FinalValues.TYPE[i].toUpperCase());
            Double value = Double.valueOf(mod1F.get(i).get("typeValue").toString());
            map.put("typeValue", value);
            mod1S.add(map);
            four -= value;
        }
        /*Map<String ,Object> others = new HashMap<>();
        others.put("typeName","E");
        others.put("typeValue",four);
        mod1S.add(others);*/
        mod1.put("mod1S", mod1S);
        result.put("mod1", mod1);
        /*mod1.put("max", "");
        Map<String, Long> typeAndNums = new HashMap<>();
        Map<String, Object> mod1F = new HashMap<>();
        for (String s : FinalValues.TYPE) {
            mod1F.put(s, "0");
            typeAndNums.put(s, 0L);
        }
        for (GetUserEventsGroupByType type : typeList) {
            for (int i = 0; i < FinalValues.TYPE.length; i++) {
                if (type.getType() == i) {
                    String percent = nf.format((double) type.getNum() / totalEvents * 100);
                    mod1F.put(FinalValues.TYPE[i], percent);
                    typeAndNums.put(FinalValues.TYPE[i], type.getNum());
                }
            }
        }
        mod1.put("mod1F", mod1F);
        Map<String, Object> mod1S = new HashMap<>();
        Long maxNum = 0L;
        String maxNumKey = "a";
        double countFour = 0.0;
        for (int i = 0; i <= 3; i++) {
            for (String key : typeAndNums.keySet()) {
                Long currentNum = typeAndNums.get(key);
                if (currentNum >= maxNum) {
                    maxNum = currentNum;
                    maxNumKey = key;
                }
            }
            String d = nf.format(((double) maxNum / totalEvents) * 100);
            countFour += Double.parseDouble(d);
            Map<String, Object> typeMod = new HashMap<>();
            typeMod.put("typeName", maxNumKey.toUpperCase());
            typeMod.put("typeValue", d);
            mod1S.put(FinalValues.TYPE[i], typeMod);
            typeAndNums.remove(maxNumKey);
            if (i == 0){
                mod1.put("max", d);
            }
            maxNum = 0L;
            maxNumKey = "a";
        }
        mod1S.put("e", nf.format(100 - countFour));
        mod1.put("mod1S", mod1S);
        result.put("mod1", mod1);*/
        Map<String, Object> mod2 = new HashMap<>();
        for (int i = 0; i < FinalValues.PRIORITY.length; i++) {
            mod2.put(FinalValues.PRIORITY[i], "0");
        }
        List<GetUserEventsGroupByPriority> getUserEventsGroupByPriority = eventMapper.getUserEventsGroupByPriorityInWeek(userEventsGroupByInWeek);
        for (GetUserEventsGroupByPriority priority : getUserEventsGroupByPriority) {
            for (int i = 2; i < FinalValues.PRIORITY.length + 2; i++) {
                if (priority.getPriority() == i) {
                    String percent = nf.format((double) priority.getNum() / totalEvents * 100);
                    mod2.put(FinalValues.PRIORITY[i - 2], percent);
                }
            }
        }
        result.put("mod2", mod2);
        Map<String, Object> mod3 = new HashMap<>();
        for (int n = 1; n <= 2; n++) {
            for (int i = -6; i <= 0; i++) {
                mod3.put(n == 1 ? "p" + (i + 7) : "e" + (i + 7), "0");
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        for (int i = -6; i <= 0; i++) {
            String day = DateUtil.getDay(i - DateUtil.stringToWeek(simpleDateFormat.format(new Date())));
            StringBuilder stringBuilder = new StringBuilder(day);
            NaturalWeek naturalWeek = new NaturalWeek();
            naturalWeek.setUserId(userId);
            naturalWeek.setYear(stringBuilder.substring(0, 4));
            naturalWeek.setMonth(stringBuilder.substring(4, 6));
            naturalWeek.setDay(stringBuilder.substring(6));
            Long pNum = 0L;
            Long eNum = 0L;
            List<SingleEvent> singleEventList = eventMapper.getEventsNumByCommon(naturalWeek);
            for (SingleEvent singleEvent : singleEventList) {
                StringBuffer stringBuffer = new StringBuffer(simpleDateFormat.format(DateUtil.stampToDate(singleEvent.getEventid().toString())));
                Long year = Long.valueOf(stringBuffer.substring(0, 4));
                Long month = Long.valueOf(stringBuffer.substring(4, 6));
                Long daily = Long.valueOf(stringBuffer.substring(6));
                if (year.equals(singleEvent.getYear()) && month.equals(singleEvent.getMonth()) && daily.equals(singleEvent.getDay())) {
                    eNum += 1;
                } else {
                    pNum += 1;
                }
            }
            mod3.put("p" + (i + 7), pNum);
            mod3.put("e" + (i + 7), eNum);
        }
        result.put("mod3", mod3);
        Map<String, Object> mod4 = new HashMap<>();
        mod4.put("support", mod2.get("d"));
        Long succeed = synchronHistoryMapper.countSucceedSynchronHistory(userId);
        Long failed = synchronHistoryMapper.countFailedSynchronHistory(userId);
        Long refused = synchronHistoryMapper.countRefusedSynchronHistory(userId);
        Long agreed = synchronHistoryMapper.countAgreedSynchronHistory(userId);
        if (succeed + failed == 0) {
            mod4.put("modify", "0");
        } else {
            try {
                mod4.put("modify", nf.format(((double) succeed / (succeed + failed)) * 100));
            } catch (Exception e) {
                mod4.put("modify", "0");
            }
        }
        if (refused + agreed == 0) {
            mod4.put("refuse", "0");
        } else {
            try {
                mod4.put("refuse", nf.format(((double) refused / (refused + agreed)) * 100));
            } catch (Exception e) {
                mod4.put("refuse", "0");
            }
        }
        result.put("mod4", mod4);
        Map<String, Object> mod5 = new HashMap<>();
        mod5.put("friendList", getMyBestFriendList(userId, 2));
        result.put("mod5", mod5);
        System.out.println(result.toString());
        return DtoUtil.getSuccesWithDataDto("周报已生成", result, 100000);
    }

    @Override
    public Dto myWeek(String userId, String token) {
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
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedAlterUserInfo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        int i = accountMapper.alterUserInfo(receivedAlterUserInfo.getUserId(), receivedAlterUserInfo.getUserSign(), receivedAlterUserInfo.getUserName(), receivedAlterUserInfo.getHeadImgUrl());
        if (i != 0) {
            return DtoUtil.getSuccessDto("修改成功", 100000);
        }
        return DtoUtil.getFalseDto("修改失败", 200000);
    }

    @Override
    public Dto getMsgList(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        List<EventMsg> eventMsgs = userServiceMapper.getHistoryMsgList(receivedId.getUserId());
        if (eventMsgs.size() == 0) {
            return DtoUtil.getSuccessDto("查询成功", 200000);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (EventMsg eventMsg : eventMsgs) {
            Account account = accountMapper.queryAccount(eventMsg.getMsgSenderId());
            if (ObjectUtils.isEmpty(account)) {
                return DtoUtil.getFalseDto("操作失败", 23001);
            }
            Map<String, Object> em = new HashMap<>();
            em.put("createDate", eventMsg.getCreateDate());
            em.put("content", account.getUserName() + eventMsg.getContent());
            result.add(em);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
    }

    @Override
    public Dto getCompletedInThisMonth(ReceivedCompletedInThisMonth receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        int totalNum = userServiceMapper.countAMonthEvents(receivedId.getUserId(), receivedId.getMonth(), receivedId.getYear(), null);
        int completedNum = userServiceMapper.countAMonthEvents(receivedId.getUserId(), receivedId.getMonth(), receivedId.getYear(), "1");
        int unfinishedNum = totalNum - completedNum;
        Map<String, Integer> result = new HashMap<>(3);
        result.put("totalNum", totalNum);
        result.put("completedNum", completedNum);
        result.put("unfinishedNum", unfinishedNum);
        return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
    }

    @Override
    public Dto getUserTimeCard(ReceivedGetUserTimeCard receivedGetUserTimeCard, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedGetUserTimeCard.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        return DtoUtil.getSuccesWithDataDto("查询剩余次数成功", userServiceMapper.getTimeCard(receivedGetUserTimeCard.getUserId(), receivedGetUserTimeCard.getServiceId()), 100000);
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

    private List<Map<String, String>> getMyBestFriendList(String userId, Integer version) {
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
        List<Long> heats = new ArrayList<>();
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
            heats.add(maxEvents * 2);
            maxEvents = 0L;
            friendId = 100000L;
        }
        for (int i = 0; i < friends.size(); i++) {
            Long id = friends.get(i);
            Map<String, String> map = new HashMap<>();
            if (id != 100000) {
                Account accounts = accountMapper.queryNameAndHead(id);
                map.put("userName", accounts.getUserName());
                map.put("headImgUrl", accounts.getHeadImgUrl());
                if (version == 2) {
                    map.put("value", heats.get(i).toString());
                }
            }
            myBestFriends.add(map);
        }
        return myBestFriends;
    }
}
