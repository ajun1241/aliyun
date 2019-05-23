package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.service.UserSettingsService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmbeans.vo.usersettings.PeopleNotAllowed;
import com.modcreater.tmbeans.vo.usersettings.UserSettingsIdAndStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    @RequestMapping(value = "updateUserSettings",method = RequestMethod.POST)
    public Dto updateUserSettings(@RequestBody UserSettingsIdAndStatus idAndStatus,HttpServletRequest request){
        return userSettingsService.updateUserSettings(idAndStatus.getStatus(),idAndStatus.getUserId(),idAndStatus.getType(),request.getHeader("token"));
    }

    /**
     * 修改好友权限(邀请或支持的限制)
     * @param peopleNotAllowed
     * @param request
     * @return
     */
    @RequestMapping(value = "updatenotallowedinvited",method = RequestMethod.POST)
    public Dto updateNotAllowed(@RequestBody PeopleNotAllowed peopleNotAllowed, HttpServletRequest request){
        return userSettingsService.updateNotAllowed(peopleNotAllowed,request.getHeader("token"));
    }

    @RequestMapping(value = "getusersettings",method = RequestMethod.POST)
    public Dto getUserSettings(@RequestBody ReceivedId receivedId ,HttpServletRequest request){
        return userSettingsService.getUserSettings(receivedId.getUserId(),request.getHeader("token"));
    }

    /*@RequestMapping(value = "updateReceiveNewMessage", method = RequestMethod.POST)
    public Dto updateReceiveNewMessage(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateReceiveNewMessage(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateNewMessageDetails", method = RequestMethod.POST)
    public Dto updateNewMessageDetails(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateNewMessageDetails(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateNewMessageSystemNotify", method = RequestMethod.POST)
    public Dto updateNewMessageSystemNotify(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateNewMessageSystemNotify(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateNewMessageForChat", method = RequestMethod.POST)
    public Dto updateNewMessageForChat(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateNewMessageForChat(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateDND", method = RequestMethod.POST)
    public Dto updateDND(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateDND(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateDNDStartTime", method = RequestMethod.POST)
    public Dto updateDNDStartTime(@RequestBody UserSettingsIdAndTime idAndTime, HttpServletRequest request) {
        return userSettingsService.updateDNDStartTime(idAndTime.getUserId(), idAndTime.getTime(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateDNDEndTime", method = RequestMethod.POST)
    public Dto updateDNDEndTime(@RequestBody UserSettingsIdAndTime idAndTime, HttpServletRequest request) {
        return userSettingsService.updateDNDEndTime(idAndTime.getUserId(), idAndTime.getTime(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateImportantAndUrgent", method = RequestMethod.POST)
    public Dto updateImportantAndUrgent(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateImportantAndUrgent(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateImportant", method = RequestMethod.POST)
    public Dto updateImportant(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateImportant(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateUrgent", method = RequestMethod.POST)
    public Dto updateUrgent(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateUrgent(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateNotImportantAndUrgent", method = RequestMethod.POST)
    public Dto updateNotImportantAndUrgent(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateNotImportantAndUrgent(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateOptional", method = RequestMethod.POST)
    public Dto updateOptional(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateOptional(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateFriendInvite", method = RequestMethod.POST)
    public Dto updateFriendInvite(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateFriendInvite(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateFriendSupport", method = RequestMethod.POST)
    public Dto updateFriendSupport(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateFriendSupport(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updatePhoneNumInvite", method = RequestMethod.POST)
    public Dto updatePhoneNumInvite(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updatePhoneNumInvite(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateWechatNumInvite", method = RequestMethod.POST)
    public Dto updateWechatNumInvite(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateWechatNumInvite(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateQQNumInvite", method = RequestMethod.POST)
    public Dto updateQQNumInvite(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateQQNumInvite(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateGroupInvite", method = RequestMethod.POST)
    public Dto updateGroupInvite(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateGroupInvite(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateMyIdInvite", method = RequestMethod.POST)
    public Dto updateMyIdInvite(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateMyIdInvite(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateOnlyWiFi", method = RequestMethod.POST)
    public Dto updateOnlyWiFi(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateOnlyWiFi(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateSimplifiedChinese", method = RequestMethod.POST)
    public Dto updateSimplifiedChinese(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateSimplifiedChinese(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateChineseTraditionalForHongKong", method = RequestMethod.POST)
    public Dto updateChineseTraditionalForHongKong(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateChineseTraditionalForHongKong(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateChineseTraditionalForTaiWan", method = RequestMethod.POST)
    public Dto updateChineseTraditionalForTaiWan(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateChineseTraditionalForTaiWan(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateForEnglish", method = RequestMethod.POST)
    public Dto updateForEnglish(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateForEnglish(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }


    @RequestMapping(value = "updateIndonesia", method = RequestMethod.POST)
    public Dto updateIndonesia(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateIndonesia(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateJapanese", method = RequestMethod.POST)
    public Dto updateJapanese(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateJapanese(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateFrench", method = RequestMethod.POST)
    public Dto updateFrench(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateFrench(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }

    @RequestMapping(value = "updateFont", method = RequestMethod.POST)
    public Dto updateFont(@RequestBody UserSettingsIdAndStatus idAndStatus, HttpServletRequest request) {
        return userSettingsService.updateFont(idAndStatus.getUserId(), idAndStatus.getStatus(), request.getHeader("token"));
    }*/
}
