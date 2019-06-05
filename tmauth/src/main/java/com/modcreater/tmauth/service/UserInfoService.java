package com.modcreater.tmauth.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.UserAchievement;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedDeleteEventIds;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedEventConditions;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedIdIsOverdue;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-17
 * Time: 13:43
 */
public interface UserInfoService {

    /**
     * 显示用户详情
     * @param userId
     * @param token
     * @return
     */
    Dto showUserDetails(String userId, String token);

    /**
     * 筛选已完成的事件
     * @param receivedEventConditions
     * @param token
     * @return
     */
    Dto filtrateUserEvents(ReceivedEventConditions receivedEventConditions, String token);

    /**
     * 数据统计
     * @param userId
     * @param token
     * @return
     */
    Dto statisticAnalysisOfData(String userId, String token);
    Dto statisticAnalysisOfData2(String userId, String token);

    /**
     * 查询用户的成就
     * @param userId
     * @param token
     * @return
     */
    Dto queryUserAchievement(String userId, String token);

    /**
     * 查询用户的成就(数据库)
     * @param userId
     * @return
     */
    List<String> queryUserAchievementInBase(String userId);

    /**
     * 获取我的(前)一周的数据
     * @param userId
     * @param token
     * @return
     */
    Dto myWeek(String userId, String token);
}
