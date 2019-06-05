package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.service.ManageService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.ComplaintVo;
import com.modcreater.tmbeans.vo.realname.ReceivedUserRealInfo;
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

    @PostMapping(value = "uploadrn")
    @ApiOperation("实名认证")
    public Dto verify(@RequestBody ReceivedUserRealInfo receivedUserRealInfo, HttpServletRequest request){
        return manageService.uploadUserRealInfo(receivedUserRealInfo,request.getHeader("token"));
    }
    @PostMapping(value = "complaint")
    @ApiOperation("投诉")
    public Dto complaint(@RequestBody ComplaintVo complaintVo, HttpServletRequest request){
        return manageService.complaint(complaintVo,request.getHeader("token"));
    }

}
