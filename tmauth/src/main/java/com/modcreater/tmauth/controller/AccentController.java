package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.service.AccentService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Account;
import com.modcreater.tmbeans.vo.AccountVo;
import com.modcreater.tmbeans.vo.LoginVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/auth/")
public class AccentController {
    @Resource
    private AccentService userService;

    @PostMapping("dologin")
    @ApiOperation("登录")
    public Dto dologin(@RequestBody LoginVo loginVo){
        System.out.println(loginVo.toString());
        return userService.doLogin(loginVo);
    }
    @PostMapping("updateaccent")
    @ApiOperation("修改账户信息")
    public Dto updateAccent(@RequestBody AccountVo accountVo){
        System.out.println(accountVo.toString());
        return userService.updateAccent(accountVo);
    }
}
