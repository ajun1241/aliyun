package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.config.annotation.GLOT;
import com.modcreater.tmauth.config.annotation.Safety;
import com.modcreater.tmauth.service.UserInfoService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.userinfovo.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-17
 * Time: 13:42
 */
@RestController
@RequestMapping(value = "/userinfo/")
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    @GLOT
    @RequestMapping(value = "showuserdetails",method = RequestMethod.POST)
    @ApiOperation("显示用户详情")
    public Dto showUserDetails(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return userInfoService.showUserDetails(receivedId.getUserId(),request.getHeader("token"));
    }

    @RequestMapping(value = "filtrateuserevents",method = RequestMethod.POST)
    @ApiOperation("筛选事件")
    public Dto filtrateUserEvents(@RequestBody ReceivedEventConditions receivedEventConditions,HttpServletRequest request){
        return userInfoService.filtrateUserEvents(receivedEventConditions,request.getHeader("token"));
    }

    @RequestMapping(value = "statisticanalysisofdata",method = RequestMethod.POST)
    @ApiOperation("数据统计")
    public Dto statisticAnalysisOfData(@RequestBody ReceivedId receivedId,HttpServletRequest request){
        return userInfoService.statisticAnalysisOfData(receivedId.getUserId(),request.getHeader("token"));
    }

    @RequestMapping(value = "weeklyreport",method = RequestMethod.POST)
    @ApiOperation("周报")
    public Dto weeklyReport(@RequestBody ReceivedId receivedId,HttpServletRequest request){
        return userInfoService.weeklyReport(receivedId.getUserId(),request.getHeader("token"));
    }

    @RequestMapping(value = "weeklyreport2",method = RequestMethod.POST)
    @ApiOperation("周报2.0")
    public Dto weeklyReport2(@RequestBody ReceivedId receivedId,HttpServletRequest request){
        return userInfoService.weeklyReport2(receivedId.getUserId(),request.getHeader("token"));
    }

    @RequestMapping(value = "searchachievement",method = RequestMethod.POST)
    @ApiOperation("查询用户成就")
    public Dto searchAchievement(@RequestBody ReceivedId receivedId,HttpServletRequest request){
        return userInfoService.queryUserAchievement(receivedId.getUserId(),request.getHeader("token"));
    }

    @RequestMapping(value = "myweek",method = RequestMethod.POST)
    @ApiOperation("我的一周")
    public Dto myWeek(@RequestBody ReceivedId receivedId,HttpServletRequest request){
        return userInfoService.myWeek(receivedId.getUserId(),request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "alterusersign")
    @ApiOperation("修改用户信息(部分)")
    public Dto alterUserSign(@RequestBody ReceivedAlterUserInfo receivedAlterUserInfo,HttpServletRequest request){
        return userInfoService.alterUserSign(receivedAlterUserInfo,request.getHeader("token"));
    }

    @PostMapping(value = "getmsglist")
    @ApiOperation("获取消息列表")
    public Dto getMsgList(@RequestBody ReceivedId receivedId ,HttpServletRequest request){
        return userInfoService.getMsgList(receivedId,request.getHeader("token"));
    }

    @PostMapping(value = "getcompletedinthismonth")
    @ApiOperation("获取本月总事件数和已完成数量")
    public Dto getCompletedInThisMonth(@RequestBody ReceivedCompletedInThisMonth receivedId ,HttpServletRequest request){
        return userInfoService.getCompletedInThisMonth(receivedId,request.getHeader("token"));
    }

    @PostMapping(value = "getusertimecard")
    @ApiOperation("获取用户次卡剩余数量")
    public Dto getUserTimeCard(@RequestBody ReceivedGetUserTimeCard receivedGetUserTimeCard, HttpServletRequest request){
        return userInfoService.getUserTimeCard(receivedGetUserTimeCard,request.getHeader("token"));
    }
}
