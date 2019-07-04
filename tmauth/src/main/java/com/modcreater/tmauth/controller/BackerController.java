package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.service.BackerService;
import com.modcreater.tmauth.service.UserSettingsService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.backer.ReceivedBeSupporterFeedback;
import com.modcreater.tmbeans.vo.backer.ReceivedChangeBackerInfo;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-07-01
 * Time: 15:11
 */
@RestController
@RequestMapping("/backer/")
public class BackerController {

    @Resource
    private BackerService backerService;

    @PostMapping("getmyfriendlist")
    @ApiOperation("获取好友列表")
    public Dto getFriendList(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return backerService.getFriendList(receivedId,request.getHeader("token"));
    }

    @PostMapping("changebacker")
    @ApiOperation("修改我的支持者")
    public Dto changeBacker(@RequestBody ReceivedChangeBackerInfo receivedChangeBackerInfo , HttpServletRequest request){
        return backerService.changeBacker(receivedChangeBackerInfo,request.getHeader("token"));
    }

    @PostMapping("getmybacker")
    @ApiOperation("获取我的支持者")
    public Dto getMyBacker(@RequestBody ReceivedId receivedId , HttpServletRequest request){
        return backerService.getMyBacker(receivedId,request.getHeader("token"));
    }

    @PostMapping("besupporterfeedback")
    @ApiOperation("是否成为支持者做出响应")
    public Dto beSupporterFeedback(ReceivedBeSupporterFeedback receivedBeSupporterFeedback , HttpServletRequest request){
        return backerService.beSupporterFeedback(receivedBeSupporterFeedback,request.getHeader("token"));
    }

}
