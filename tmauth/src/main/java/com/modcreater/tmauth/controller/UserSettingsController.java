package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.service.UserSettingsService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.usersettings.UserSettingsIdAndStatus;
import com.modcreater.tmbeans.vo.usersettings.UserSettingsIdAndTime;
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

    /*@RequestMapping(value = "updateReceiveNewMessage",method = RequestMethod.POST)
    public Dto updateReceiveNewMessage(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.updateReceiveNewMessage(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "updateNewMessageDetails",method = RequestMethod.POST)
    public Dto updateNewMessageDetails(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.updateNewMessageDetails(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "updateNewMessageSystemNotify",method = RequestMethod.POST)
    public Dto updateNewMessageSystemNotify(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.updateNewMessageSystemNotify(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "updateNewMessageForChat",method = RequestMethod.POST)
    public Dto updateNewMessageForChat(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.updateNewMessageForChat(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "updateDND",method = RequestMethod.POST)
    public Dto updateDND(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.updateDND(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "updateDNDStartTime",method = RequestMethod.POST)
    public Dto updateDNDStartTime(@RequestBody UserSettingsIdAndTime idAndTime , HttpServletRequest request){
        return userSettingsService.updateDNDStartTime(idAndTime.getUserId(),idAndTime.getTime(),request.getHeader("token"));
    }

    @RequestMapping(value = "updateDNDEndTime",method = RequestMethod.POST)
    public Dto updateDNDEndTime(@RequestBody UserSettingsIdAndTime idAndTime , HttpServletRequest request){
        return userSettingsService.updateDNDEndTime(idAndTime.getUserId(),idAndTime.getTime(),request.getHeader("token"));
    }

    @RequestMapping(value = "updateimportantAndUrgent",method = RequestMethod.POST)
    public Dto updateimportantAndUrgent(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.updateimportantAndUrgent(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "updateimportant",method = RequestMethod.POST)
    public Dto updateimportant(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.updateimportant(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "updateurgent",method = RequestMethod.POST)
    public Dto updateurgent(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.updateurgent(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "updatenotImportantAndUrgent",method = RequestMethod.POST)
    public Dto updatenotImportantAndUrgent(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.updatenotImportantAndUrgent(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "updateoptional",method = RequestMethod.POST)
    public Dto updateoptional(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.updateoptional(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "updatefriendInvite",method = RequestMethod.POST)
    public Dto updatefriendInvite(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.updatefriendInvite(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "updatefriendSupport",method = RequestMethod.POST)
    public Dto updatefriendSupport(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.updatefriendSupport(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "updatephoneNumInvite",method = RequestMethod.POST)
    public Dto updatephoneNumInvite(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.updatephoneNumInvite(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "updatewechatNumInvite",method = RequestMethod.POST)
    public Dto updatewechatNumInvite(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.updatewechatNumInvite(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "updateQQNumInvite",method = RequestMethod.POST)
    public Dto updateQQNumInvite(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.updateQQNumInvite(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "updategroupInvite",method = RequestMethod.POST)
    public Dto updategroupInvite(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.updategroupInvite(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "updatemyIdInvite",method = RequestMethod.POST)
    public Dto updatemyIdInvite(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.updatemyIdInvite(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "updateonlyWiFi",method = RequestMethod.POST)
    public Dto updateonlyWiFi(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.updateonlyWiFi(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "updatesimplifiedChinese",method = RequestMethod.POST)
    public Dto updatesimplifiedChinese(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.updatesimplifiedChinese(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "updatechineseTraditionalForHongKong",method = RequestMethod.POST)
    public Dto updatechineseTraditionalForHongKong(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.updatechineseTraditionalForHongKong(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "updatechineseTraditionalForTaiWan",method = RequestMethod.POST)
    public Dto updatechineseTraditionalForTaiWan(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.updatechineseTraditionalForTaiWan(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "updateforEnglish",method = RequestMethod.POST)
    public Dto updateforEnglish(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.updateforEnglish(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }


    @RequestMapping(value = "update",method = RequestMethod.POST)
    public Dto update(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.update(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "update",method = RequestMethod.POST)
    public Dto update(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.update(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "update",method = RequestMethod.POST)
    public Dto update(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.update(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "update",method = RequestMethod.POST)
    public Dto update(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.update(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "update",method = RequestMethod.POST)
    public Dto update(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.update(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "update",method = RequestMethod.POST)
    public Dto update(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.update(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }

    @RequestMapping(value = "update",method = RequestMethod.POST)
    public Dto update(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return userSettingsService.update(idAndStatus.getUserId(),idAndStatus.getStatus(),request.getHeader("token"));
    }*/
}
