package com.modcreater.tmauth.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.usersettings.GetFriendListInSettings;
import com.modcreater.tmbeans.vo.usersettings.ReceivedShowFriendList;

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
     * 修改用户设置
     * @param status
     * @param userId
     * @param type
     * @param token
     * @return
     */
    Dto updateUserSettings(int status, String userId, String type, String token);

    /**
     * 修改好友邀请或支持权限
     * @param receivedShowFriendList
     * @param token
     * @return
     */
    Dto updateNotAllowed(ReceivedShowFriendList receivedShowFriendList, String token);

    /**
     * 用户获取云端设置
     * @param userId
     * @param token
     * @return
     */
    Dto getUserSettings(String userId, String token);

    /**
     * 获取好友列表
     * @param getFriendListInSettings
     * @param token
     * @return
     */
    Dto getFriendList(GetFriendListInSettings getFriendListInSettings, String token);
}
