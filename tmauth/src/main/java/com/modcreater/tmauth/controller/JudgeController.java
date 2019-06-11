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

    @PostMapping("searchjudge")
    @ApiOperation("搜索服务判断")
    public Dto searchJudge(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return judgeService.searchServiceJudge(receivedId.getUserId(),request.getHeader("token"));
    }
}
