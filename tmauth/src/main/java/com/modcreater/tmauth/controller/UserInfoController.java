package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.service.UserInfoService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedEventConditions;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedIdIsOverdue;
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
 * @Date: 2019-05-17
 * Time: 13:42
 */
@RestController
@RequestMapping(value = "/userinfo/")
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    /**
     * 显示用户详情
     * @param receivedId
     * @param request
     * @return
     */
    @RequestMapping(value = "showuserdetails",method = RequestMethod.POST)
    public Dto showUserDetails(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return userInfoService.showUserDetails(receivedId.getUserId(),request.getHeader("token"));
    }

    /**
     * 显示用户事件(已完成,未完成)
     * @param receivedIdIsOverdue
     * @param request
     * @return
     */
    @RequestMapping(value = "showuserevents",method = RequestMethod.POST)
    public Dto showUserEvents(@RequestBody ReceivedIdIsOverdue receivedIdIsOverdue, HttpServletRequest request){
        return userInfoService.showUserEvents(receivedIdIsOverdue,request.getHeader("token"));
    }

    /**
     * 筛选已完成的事件
     * @param receivedEventConditions
     * @param request
     * @return
     */
    @RequestMapping(value = "filtrateuserevents",method = RequestMethod.POST)
    public Dto filtrateUserEvents(@RequestBody ReceivedEventConditions receivedEventConditions,HttpServletRequest request){
        return userInfoService.filtrateUserEvents(receivedEventConditions,request.getHeader("token"));
    }

    /**
     * 数据统计
     * @param receivedId
     * @param request
     * @return
     */
    @RequestMapping(value = "statisticanalysisofdata",method = RequestMethod.POST)
    public Dto statisticAnalysisOfData(@RequestBody ReceivedId receivedId,HttpServletRequest request){
        return userInfoService.statisticAnalysisOfData(receivedId.getUserId(),request.getHeader("token"));
    }

    /**
     * 查询用户成就
     * @param receivedId
     * @param request
     * @return
     */
    @RequestMapping(value = "searchachievement",method = RequestMethod.POST)
    public Dto searchAchievement(@RequestBody ReceivedId receivedId,HttpServletRequest request){
        return userInfoService.queryUserAchievement(receivedId.getUserId(),request.getHeader("token"));
    }

    /**
     * 查询我的一周
     * @param receivedId
     * @param request
     * @return
     */
    @RequestMapping(value = "myweek",method = RequestMethod.POST)
    public Dto myWeek(@RequestBody ReceivedId receivedId,HttpServletRequest request){
        return userInfoService.myWeek(receivedId.getUserId(),request.getHeader("token"));
    }
}
