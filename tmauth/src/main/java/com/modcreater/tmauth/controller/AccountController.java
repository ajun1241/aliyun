package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.service.AccountService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.AccountVo;
import com.modcreater.tmbeans.vo.AddPwdVo;
import com.modcreater.tmbeans.vo.LoginVo;
import com.modcreater.tmbeans.vo.QueryUserVo;
import com.modcreater.tmbeans.vo.uservo.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth/")
public class AccountController {
    @Resource
    private AccountService userService;

   /* @PostMapping("dologin")
    @ApiOperation("登录")
    public Dto dologin(@RequestBody LoginVo loginVo){
        return userService.doLogin(loginVo);
    }*/

    @PostMapping("addpwd")
    @ApiOperation("添加密码")
    public Dto addPassword(@RequestBody AddPwdVo addPwdVo){
        return userService.addPassword(addPwdVo);
    }

    @PostMapping("registered")
    @ApiOperation("注册/登录")
    public Dto registered(@RequestBody LoginVo loginVo){
        return userService.registered(loginVo);
    }

    @PostMapping("queryaccount")
    @ApiOperation("查询账户信息")
    public Dto queryAccount(@RequestBody QueryUserVo queryUserVo, HttpServletRequest request){
        System.out.println("token===>>>"+request.getHeader("token"));
        return userService.queryAccount(queryUserVo,request.getHeader("token"));
    }

    @PostMapping("updateaccount")
    @ApiOperation("修改账户信息")
    public Dto updateAccount(@RequestBody AccountVo accountVo, HttpServletRequest request){
        return userService.updateAccount(accountVo,request.getHeader("token"));
    }

    @PostMapping("queryfriend")
    @ApiOperation("搜索好友")
    public Dto queryFriendByUserCode(@RequestBody QueFridenVo queFridenVo, HttpServletRequest request){
        return userService.queryFriendByUserCode(queFridenVo,request.getHeader("token"));
    }

    @PostMapping("sendfriendrequest")
    @ApiOperation("发送添加好友请求")
    public Dto sendFriendRequest(@RequestBody SendFriendRequestVo requestVo, HttpServletRequest request){
        return userService.sendFriendRequest(requestVo,request.getHeader("token"));
    }

    @PostMapping("sendfriendresponse")
    @ApiOperation("发送同意添加好友请求")
    public Dto sendFriendResponse(@RequestBody SendFriendResponseVo responseVo, HttpServletRequest request){
        return userService.sendFriendResponse(responseVo,request.getHeader("token"));
    }

    @PostMapping("queryFriendList")
    @ApiOperation("查询好友列表")
    public Dto queryFriendList(@RequestBody UserIdVo userIdVo, HttpServletRequest request){
        return userService.queryFriendList(userIdVo,request.getHeader("token"));
    }

    @PostMapping("updateFriendJurisdiction")
    @ApiOperation("修改好友权限")
    public Dto updateFriendJurisdiction(@RequestBody UpdateFriendJurisdictionVo updateFriendJurisdictionVo, HttpServletRequest request){
        return userService.updateFriendJurisdiction(updateFriendJurisdictionVo,request.getHeader("token"));
    }

    @PostMapping("deleteFriendship")
    @ApiOperation("解除好友关系")
    public Dto deleteFriendship(@RequestBody DeleteFriendshipVo deleteFriendshipVo, HttpServletRequest request){
        return userService.deleteFriendship(deleteFriendshipVo,request.getHeader("token"));
    }
}
