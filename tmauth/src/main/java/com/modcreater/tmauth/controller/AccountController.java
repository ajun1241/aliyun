package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.service.AccountService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.AccountVo;
import com.modcreater.tmbeans.vo.LoginVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/auth/")
public class AccountController {
    @Resource
    private AccountService userService;

    @PostMapping("dologin")
    @ApiOperation("登录")
    public Dto dologin(@RequestBody LoginVo loginVo){
//        System.out.println(loginVo.toString());
        return userService.doLogin(loginVo);
    }

    @PostMapping("queryaccount")
    @ApiOperation("查询账户信息")
    public Dto queryAccount(@RequestBody String id){
//        System.out.println(id);
        return userService.queryAccount(id);
    }

    @PostMapping("updateaccount")
    @ApiOperation("修改账户信息")
    public Dto updateAccount(@RequestBody AccountVo accountVo){
//        System.out.println(accountVo.toString());
        return userService.updateAccount(accountVo);
    }
}
