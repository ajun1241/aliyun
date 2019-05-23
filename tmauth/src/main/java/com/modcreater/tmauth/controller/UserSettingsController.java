package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.service.UserSettingsService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmbeans.vo.usersettings.GetFriendListInSettings;
import com.modcreater.tmbeans.vo.usersettings.ReceivedShowFriendList;
import com.modcreater.tmbeans.vo.usersettings.UserSettingsIdAndStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-21
 * Time: 8:51
 */
@RestController
@RequestMapping("/settings/")
public class UserSettingsController {

    @Resource
    private UserSettingsService userSettingsService;

    /**
     * 用户设置上传
     * @param idAndStatus
     * @param request
     * @return
     */
    @RequestMapping(value = "updateusersettings",method = RequestMethod.POST)
    public Dto updateUserSettings(@RequestBody UserSettingsIdAndStatus idAndStatus,HttpServletRequest request){
        return userSettingsService.updateUserSettings(idAndStatus.getStatus(),idAndStatus.getUserId(),idAndStatus.getType(),request.getHeader("token"));
    }

    /**
     * 修改好友邀请或支持权限
     * @param receivedShowFriendList
     * @param request
     * @return
     */
    @RequestMapping(value = "updatenotallowedinvited",method = RequestMethod.POST)
    public Dto updateNotAllowed(@RequestBody ReceivedShowFriendList receivedShowFriendList, HttpServletRequest request){
        return userSettingsService.updateNotAllowed(receivedShowFriendList,request.getHeader("token"));
    }

    /**
     * 获取用户设置
     * @param receivedId
     * @param request
     * @return
     */
    @RequestMapping(value = "getusersettings",method = RequestMethod.POST)
    public Dto getUserSettings(@RequestBody ReceivedId receivedId ,HttpServletRequest request) {
        return userSettingsService.getUserSettings(receivedId.getUserId(), request.getHeader("token"));
    }
}
