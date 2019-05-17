package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.UserInfoService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Achievement;
import com.modcreater.tmbeans.pojo.UserAchievement;
import com.modcreater.tmbeans.pojo.UserStatistics;
import com.modcreater.tmbeans.show.userinfo.ShowUserDetails;
import com.modcreater.tmbeans.show.userinfo.ShowUserStatistics;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedEventConditions;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmdao.mapper.AchievementMapper;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Dto showUserDetails(String userId, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        if (StringUtils.hasText(userId)){
            ShowUserDetails showUserDetails = accountMapper.queryUserDetails(userId);
            UserStatistics userStatistics = achievementMapper.queryUserStatistics(userId);
            ShowUserStatistics showUserStatistics= new ShowUserStatistics();
            showUserStatistics.setCompleted(userStatistics.getCompleted());
            showUserStatistics.setUnfinished(userStatistics.getUnfinished());
            showUserStatistics.setDrafts(userStatistics.getDrafts());
            List<String> imgUrlList = queryUserAchievementInBase(userId);
            Map<String,Object> result = new HashMap<>(3);
            //用户信息
            result.put("showUserDetails",showUserDetails);
            //用户事件状态
            result.put("userStatistics",showUserStatistics);
            //用户所有成就
            result.put("imgUrlList",imgUrlList);
            return DtoUtil.getSuccesWithDataDto("查询用户详情成功",result,100000);
        }
        return DtoUtil.getFalseDto("查询用户详情失败",40001);
    }

    @Override
    public Dto showCompletedEvents(String userId, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        return null;
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
        List<String> imgUrlList = queryUserAchievementInBase(userId);
        if (imgUrlList.size() != 0){
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
    public Dto searchCompletedEventsByEventName(String eventName, String token) {
        return null;
    }

    @Override
    public Dto filtrateCompletedEvents(ReceivedEventConditions receivedEventConditions, String token) {
        return null;
    }

    @Override
    public Dto showUnfinishedEvents(String userId, String token) {
        return null;
    }

    @Override
    public Dto searchUnfinishedEventsByEventName(String eventName, String token) {
        return null;
    }

    @Override
    public Dto filtrateUnfinishedEvents(ReceivedEventConditions receivedEventConditions, String token) {
        return null;
    }

    @Override
    public Dto statisticAnalysisOfData(String userId, String token) {
        return null;
    }

}
