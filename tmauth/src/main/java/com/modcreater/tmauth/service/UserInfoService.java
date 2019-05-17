package com.modcreater.tmauth.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.UserAchievement;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedEventConditions;

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
     * 查询用户已完成的事件
     * @param userId
     * @param token
     * @return
     */
    Dto showCompletedEvents(String userId, String token);


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

    Dto searchCompletedEventsByEventName(String eventName, String token);

    Dto filtrateCompletedEvents(ReceivedEventConditions receivedEventConditions, String token);

    Dto showUnfinishedEvents(String userId, String token);

    Dto searchUnfinishedEventsByEventName(String eventName, String token);

    Dto filtrateUnfinishedEvents(ReceivedEventConditions receivedEventConditions, String token);

    Dto statisticAnalysisOfData(String userId, String token);
}
