package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.service.UserInfoService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedEventConditions;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedIdName;
import com.modcreater.tmbeans.vo.uservo.UserIdVo;
import org.apache.ibatis.annotations.Param;
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
     * @param userIdVo
     * @param request
     * @return
     */
    @RequestMapping(value = "showuserdetails",method = RequestMethod.POST)
    public Dto showUserDetails(@RequestBody UserIdVo userIdVo, HttpServletRequest request){
        return userInfoService.showUserDetails(userIdVo.getUserId(),request.getHeader("token"));
    }

    /**
     * 显示用户已完成事件
     * @param userIdVo
     * @param request
     * @return
     */
    @RequestMapping(value = "showcompletedevents",method = RequestMethod.POST)
    public Dto showCompletedEvents(@RequestBody UserIdVo userIdVo,HttpServletRequest request){
        return userInfoService.showCompletedEvents(userIdVo.getUserId(),request.getHeader("token"));
    }

    /**
     * 根据事件名称查询事件
     * @param receivedIdName
     * @param request
     * @return
     */
    @RequestMapping(value = "searchcompletedeventsbyeventname",method = RequestMethod.POST)
    public Dto searchCompletedEventsByEventName(@RequestBody ReceivedIdName receivedIdName, HttpServletRequest request){
        return userInfoService.searchCompletedEventsByEventName(receivedIdName.getUserId(),receivedIdName.getEventName(),request.getHeader("token"));
    }

    /**
     * 筛选已完成的事件
     * @param receivedEventConditions
     * @param request
     * @return
     */
    @RequestMapping(value = "filtratecompletedevents",method = RequestMethod.POST)
    public Dto filtrateCompletedEvents(@RequestBody ReceivedEventConditions receivedEventConditions,HttpServletRequest request){
        return userInfoService.filtrateCompletedEvents(receivedEventConditions,request.getHeader("token"));
    }

    /**
     * 显示用户未完成的事件
     * @param userIdVo
     * @param request
     * @return
     */
    @RequestMapping(value = "showunfinishedevents",method = RequestMethod.POST)
    public Dto showUnfinishedEvents(@RequestBody UserIdVo userIdVo,HttpServletRequest request){
        return userInfoService.showUnfinishedEvents(userIdVo.getUserId(),request.getHeader("token"));
    }

    /**
     * 根据事件名称查询用户未完成的事件
     * @param receivedIdName
     * @param request
     * @return
     */
    @RequestMapping(value = "searchunfinishedeventsbyeventname",method = RequestMethod.POST)
    public Dto searchUnfinishedEventsByEventName(@RequestBody ReceivedIdName receivedIdName,HttpServletRequest request){
        return userInfoService.searchUnfinishedEventsByEventName(receivedIdName.getUserId(),receivedIdName.getEventName(),request.getHeader("token"));
    }

    /**
     * 筛选用户未完成的事件
     * @param receivedEventConditions
     * @param request
     * @return
     */
    @RequestMapping(value = "filtrateunfinishedevents",method = RequestMethod.POST)
    public Dto filtrateUnfinishedEvents(@RequestBody ReceivedEventConditions receivedEventConditions ,String appType,HttpServletRequest request){
        return userInfoService.filtrateUnfinishedEvents(receivedEventConditions,request.getHeader("token"));
    }

    /**
     * 数据统计
     * @param userIdVo
     * @param request
     * @return
     */
    @RequestMapping(value = "statisticanalysisofdata",method = RequestMethod.POST)
    public Dto statisticAnalysisOfData(@RequestBody UserIdVo userIdVo,HttpServletRequest request){
        return userInfoService.statisticAnalysisOfData(userIdVo.getUserId(),request.getHeader("token"));
    }


    /**
     * 查询用户成就(图片的URL地址)
     * @param userIdVo
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "searchachievement",method = RequestMethod.POST)
    public Dto queryUserAchievement(@RequestBody UserIdVo userIdVo,HttpServletRequest httpServletRequest){
        return userInfoService.queryUserAchievement(userIdVo.getUserId(),httpServletRequest.getHeader("token"));
    }

}
