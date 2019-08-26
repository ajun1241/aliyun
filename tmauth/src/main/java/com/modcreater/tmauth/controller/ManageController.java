package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.config.annotation.Safety;
import com.modcreater.tmauth.service.ManageService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.ComplaintVo;
import com.modcreater.tmbeans.vo.realname.ReceivedStudentRealInfo;
import com.modcreater.tmbeans.vo.realname.ReceivedUserRealInfo;
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
 * @Date: 2019-06-03
 * Time: 13:46
 */
@RestController
@RequestMapping(value = "/user/")
public class ManageController {

    @Resource
    private ManageService manageService;

    @Safety
    @PostMapping(value = "uploadrn")
    @ApiOperation("实名认证（普通用户）")
    public Dto verify(@RequestBody ReceivedUserRealInfo receivedUserRealInfo, HttpServletRequest request){
        return manageService.uploadUserRealInfo(receivedUserRealInfo,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "studentuploadrn")
    @ApiOperation("实名认证（学生）")
    public Dto studentVerify(@RequestBody ReceivedStudentRealInfo receivedStudentRealInfo, HttpServletRequest request){
        return manageService.uploadStudentRealInfo(receivedStudentRealInfo,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "complaint")
    @ApiOperation("投诉")
    public Dto complaint(@RequestBody ComplaintVo complaintVo, HttpServletRequest request){
        return manageService.complaint(complaintVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "changerealinfo")
    @ApiOperation("更换实名信息")
    public Dto changeRealInfo(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return manageService.changeRealInfo(receivedId,request.getHeader("token"));
    }

}
