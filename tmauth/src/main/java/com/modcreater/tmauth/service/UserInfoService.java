package com.modcreater.tmauth.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.UserAchievement;
import com.modcreater.tmbeans.vo.userinfovo.*;

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

    /**
     * 周报
     * @param userId
     * @param token
     * @return
     */
    Dto weeklyReport(String userId, String token);

    /**
     * 周报2
     * @param userId
     * @param token
     * @return
     */
    Dto weeklyReport2(String userId, String token);

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

    /**
     * 修改用户信息
     * @param receivedAlterUserInfo
     * @param token
     * @return
     */
    Dto alterUserSign(ReceivedAlterUserInfo receivedAlterUserInfo, String token);

    /**
     * 获取消息列表
     * @param receivedId
     * @param token
     * @return
     */
    Dto getMsgList(ReceivedId receivedId, String token);

    /**
     * 获取本月总事件数和已完成的数量
     * @param receivedId
     * @param token
     * @return
     */
    Dto getCompletedInThisMonth(ReceivedCompletedInThisMonth receivedId, String token);

    /**
     * 获取用户次卡剩余
     * @param receivedGetUserTimeCard
     * @param token
     * @return
     */
    Dto getUserTimeCard(ReceivedGetUserTimeCard receivedGetUserTimeCard, String token);
}
