package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.service.UserServiceJudgeService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.AddPwdVo;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/6/10 15:06
 */
@RestController
@RequestMapping("/judge/")
public class JudgeController {

    @Resource
    private UserServiceJudgeService judgeService;

    @PostMapping("realjudge")
    @ApiOperation("实名认证判断")
    public Dto realJudge(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        String token=request.getHeader("token");
        return judgeService.realInfoJudge(receivedId.getUserId(),token);
    }

    @PostMapping("searchservicejudge")
    @ApiOperation("搜索服务判断")
    public Dto searchJudge(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return judgeService.searchServiceJudge(receivedId.getUserId(),request.getHeader("token"));
    }

    @PostMapping("friendservicejudge")
    @ApiOperation("好友服务判断")
    public Dto friendServiceJudge(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return judgeService.friendServiceJudge(receivedId.getUserId(),request.getHeader("token"));
    }

    @PostMapping("annualreportingservicejudge")
    @ApiOperation("年报服务判断")
    public Dto reportServiceJudge(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return judgeService.annualReportingServiceJudge(receivedId.getUserId(),request.getHeader("token"));
    }

    @PostMapping("backupservicejudge")
    @ApiOperation("备份服务判断")
    public Dto backupServiceJudge(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return judgeService.backupServiceJudge(receivedId.getUserId(),request.getHeader("token"));
    }

    @PostMapping(value = "queryuserallservicefunction")
    @ApiOperation("查看用户所有服务是否开通")
    public Dto queryUserAllServiceFunction(@RequestBody ReceivedId receivedId,HttpServletRequest request){
        return judgeService.queryUserAllServiceFunction(receivedId,request.getHeader("token"));
    }
}
