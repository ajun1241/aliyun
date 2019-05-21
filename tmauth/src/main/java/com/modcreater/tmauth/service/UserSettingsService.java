package com.modcreater.tmauth.service;

import com.modcreater.tmbeans.dto.Dto;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-21
 * Time: 10:14
 */
public interface UserSettingsService {

    /**
     * 修改是否接收新消息
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateReceiveNewMessage(String userId, int status, String token);

    /**
     * 修改通知消息详情
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateNewMessageDetails(String userId, int status, String token);

    /**
     * 修改新消息系统通知
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateNewMessageSystemNotify(String userId, int status, String token);

    /**
     * 修改聊天界面中的新消息通知
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateNewMessageForChat(String userId, int status, String token);

    /**
     * 修改勿扰模式
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateDND(String userId, int status, String token);

    /**
     * 修改勿扰模式开始时间
     * @param userId
     * @param time
     * @param token
     * @return
     */
    Dto updateDNDStartTime(String userId, int time, String token);

    /**
     * 修改勿扰模式结束时间
     * @param userId
     * @param time
     * @param token
     * @return
     */
    Dto updateDNDEndTime(String userId, int time, String token);

    /**
     * 修改重要又紧急
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateImportantAndUrgent(String userId, int status, String token);

    /**
     * 修改重要但不紧急
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateImportant(String userId, int status, String token);

    /**
     * 修改不重要但紧急
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateUrgent(String userId, int status, String token);

    /**
     * 修改不重要不紧急
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateNotImportantAndUrgent(String userId, int status, String token);

    /**
     * 修改自定义
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateOptional(String userId, int status, String token);

    /**
     * 修改好友邀请
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateFriendInvite(String userId, int status, String token);

    /**
     * 修改好友支持
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateFriendSupport(String userId, int status, String token);

    /**
     * 修改通过电话号码邀请
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updatePhoneNumInvite(String userId, int status, String token);

    /**
     * 修改通过微信邀请
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateWechatNumInvite(String userId, int status, String token);

    /**
     * 修改通过QQ邀请
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateQQNumInvite(String userId, int status, String token);

    /**
     * 修改通过群组邀请
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateGroupInvite(String userId, int status, String token);

    /**
     * 修改通过我的ID邀请
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateMyIdInvite(String userId, int status, String token);

    /**
     * 修改只在WIFI下更新
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateOnlyWiFi(String userId, int status, String token);

    /**
     * 修改简体中文
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateSimplifiedChinese(String userId, int status, String token);

    /**
     * 修改繁体中文(香港)
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateChineseTraditionalForHongKong(String userId, int status, String token);

    /**
     * 修改繁体中文(台湾)
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateChineseTraditionalForTaiWan(String userId, int status, String token);

    /**
     * 修改英语
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateForEnglish(String userId, int status, String token);

    /**
     * 修改印度尼西亚语
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateIndonesia(String userId, int status, String token);

    /**
     * 修改日语
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateJapanese(String userId, int status, String token);

    /**
     * 修改法语
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateFrench(String userId, int status, String token);

    /**
     * 修改字体
     * @param userId
     * @param status
     * @param token
     * @return
     */
    Dto updateFont(String userId, int status, String token);

    /**
     * 修改用户设置
     * @param status
     * @param userId
     * @param type
     * @param token
     * @return
     */
    Dto updateUserSettings(int status, String userId, String type, String token);
}
