package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.service.AccountService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.AccountVo;
import com.modcreater.tmbeans.vo.AddPwdVo;
import com.modcreater.tmbeans.vo.LoginVo;
import com.modcreater.tmbeans.vo.QueryUserVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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
    public Dto queryAccount(@RequestBody QueryUserVo queryUserVo){
        return userService.queryAccount(queryUserVo);
    }

    @PostMapping("updateaccount")
    @ApiOperation("修改账户信息")
    public Dto updateAccount(@RequestBody AccountVo accountVo){
        return userService.updateAccount(accountVo);
    }
}
