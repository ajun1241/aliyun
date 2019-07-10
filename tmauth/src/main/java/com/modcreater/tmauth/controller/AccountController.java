package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.config.annotation.Safety;
import com.modcreater.tmauth.service.AccountService;
import com.modcreater.tmauth.service.DeviceTokenService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmbeans.vo.uservo.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth/")
public class AccountController {
    @Resource
    private AccountService userService;

    @Resource
    private DeviceTokenService deviceTokenService;

    @Safety
    @PostMapping("addpwd")
    @ApiOperation("添加密码")
    public Dto addPassword(@RequestBody AddPwdVo addPwdVo){
        return userService.addPassword(addPwdVo);
    }

    @Safety
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

    @Safety
    @PostMapping("updateaccount")
    @ApiOperation("修改账户信息")
    public Dto updateAccount(@RequestBody UpdAccountInfo accountVo, HttpServletRequest request){
        return userService.updateAccount(accountVo,request.getHeader("token"));
    }

    @PostMapping("queryfriend")
    @ApiOperation("搜索好友")
    public Dto queryFriendByUserCode(@RequestBody QueFridenVo queFridenVo, HttpServletRequest request){
        return userService.queryFriendByUserCode(queFridenVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping("sendfriendrequest")
    @ApiOperation("发送添加好友请求")
    public Dto sendFriendRequest(@RequestBody SendFriendRequestVo requestVo, HttpServletRequest request){
        return userService.sendFriendRequest(requestVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping("sendfriendresponse")
    @ApiOperation("发送同意添加好友请求")
    public Dto sendFriendResponse(@RequestBody FriendshipVo responseVo, HttpServletRequest request){
        return userService.sendFriendResponse(responseVo,request.getHeader("token"));
    }

    @PostMapping("queryfrienddetails")
    @ApiOperation("查询好友详情")
    public Dto queryFriendDetails(@RequestBody FriendshipVo queFridenVo, HttpServletRequest request){
        return userService.queryFriendDetails(queFridenVo,request.getHeader("token"));
    }

    @PostMapping("queryFriendList")
    @ApiOperation("查询好友列表")
    public Dto queryFriendList(@RequestBody UserIdVo userIdVo, HttpServletRequest request){
        return userService.queryFriendList(userIdVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping("updateFriendJurisdiction")
    @ApiOperation("修改好友权限")
    public Dto updateFriendJurisdiction(@RequestBody UpdateFriendJurisdictionVo updateFriendJurisdictionVo, HttpServletRequest request){
        return userService.updateFriendJurisdiction(updateFriendJurisdictionVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping("deleteFriendship")
    @ApiOperation("解除好友关系")
    public Dto deleteFriendship(@RequestBody FriendshipVo deleteFriendshipVo, HttpServletRequest request){
        return userService.deleteFriendship(deleteFriendshipVo,request.getHeader("token"));
    }

    @PostMapping("queryallunreadmsg")
    @ApiOperation("拉取所有好友消息")
    public Dto queryAllUnreadMsg(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return userService.queryAllUnreadMsg(receivedId,request.getHeader("token"));
    }

    @PostMapping("queryallunreadmsgcount")
    @ApiOperation("拉取所有未读消息条数")
    public Dto queryAllUnreadMsgCount(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return userService.queryAllUnreadMsgCount(receivedId,request.getHeader("token"));
    }

    @Safety
    @PostMapping("uplheadimg")
    @ApiOperation("上传头像")
    public Dto uplHeadImg(@RequestBody HeadImgVo headImgVo, HttpServletRequest request){
        return userService.uplHeadImg(headImgVo,request.getHeader("token"));
    }

    @PostMapping("sessiondetail")
    @ApiOperation("查询用户列表信息")
    public Dto querySessionListDetail(@RequestBody FriendshipVo friendshipVo, HttpServletRequest request){
        return userService.querySessionListDetail(friendshipVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping("replacedevicetoken")
    @ApiOperation("更换deviceToken")
    public Dto replaceDeviceToken(@RequestBody DeviceTokenVo deviceTokenVo, HttpServletRequest request){
        return deviceTokenService.replaceDeviceToken(deviceTokenVo ,request.getHeader("token"));
    }
}
