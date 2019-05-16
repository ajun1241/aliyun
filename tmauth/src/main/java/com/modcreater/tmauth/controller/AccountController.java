package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.service.AccountService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.AccountVo;
import com.modcreater.tmbeans.vo.AddPwdVo;
import com.modcreater.tmbeans.vo.LoginVo;
import com.modcreater.tmbeans.vo.QueryUserVo;
import com.modcreater.tmbeans.vo.uservo.BuildFriendshipVo;
import com.modcreater.tmbeans.vo.uservo.QueFridenVo;
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

    @PostMapping("buildfriendship")
    @ApiOperation("添加好友")
    public Dto buildFriendship(@RequestBody BuildFriendshipVo buildFriendshipVo, HttpServletRequest request){
        return userService.buildFriendship(buildFriendshipVo,request.getHeader("token"));
    }

    /**
     * 查询用户成就(图片的URL地址)
     * @param userId
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "searchachievement",method = RequestMethod.POST)
    public Dto queryUserAchievement(@RequestBody String userId,HttpServletRequest httpServletRequest){
        return userService.queryUserAchievement(userId,httpServletRequest.getHeader("token"));
    }
}
