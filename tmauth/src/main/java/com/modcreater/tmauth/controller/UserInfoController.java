package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.config.annotation.GLOT;
import com.modcreater.tmauth.config.annotation.Safety;
import com.modcreater.tmauth.service.UserInfoService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.userinfovo.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @GLOT
    @RequestMapping(value = "getmineforios",method = RequestMethod.POST)
    @ApiOperation("显示用户详情(IOS)")
    public Dto getMineForIOS(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return userInfoService.getMineForIOS(receivedId.getUserId(),request.getHeader("token"));
    }
    @Safety
    @RequestMapping(value = "filtrateuserevents",method = RequestMethod.POST)
    @ApiOperation("筛选事件")
    public Dto filtrateUserEvents(@RequestBody ReceivedEventConditions receivedEventConditions,HttpServletRequest request){
        return userInfoService.filtrateUserEvents(receivedEventConditions,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "filtrateusereventsforios")
    @ApiOperation("筛选事件(IOS)")
    public Dto filtrateUserEventsForIOS(@RequestBody ReceivedEventConditions receivedEventConditions ,HttpServletRequest request){
        return userInfoService.filtrateUserEventsForIOS(receivedEventConditions,request.getHeader("token"));
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

    @CrossOrigin(origins = {"*", "null"})
    @RequestMapping(value = "weeklyreport2",method = RequestMethod.POST)
    @ApiOperation("周报2.0")
    public Dto weeklyReport2(String userId,String appType, HttpServletRequest request){
        return userInfoService.weeklyReport2(userId,request.getHeader("token"));
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

    @PostMapping(value = "getachievementnum")
    @ApiOperation("获取成就总数/已完成")
    public Dto getAchievementNum(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return userInfoService.getAchievementNum(receivedId,request.getHeader("token"));
    }

    @PostMapping(value = "getuserachievementforios")
    @ApiOperation("获取用户成就(IOS)")
    public Dto getUserAchievementForIOS(@RequestBody ReceivedId receivedId,HttpServletRequest request){
        return userInfoService.getUserAchievementForIOS(receivedId,request.getHeader("token"));
    }
}
