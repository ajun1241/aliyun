package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.service.AppService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.app.ReceivedAppInfo;
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
 * Time: 8:43
 */
@RestController
@RequestMapping(value = "/app/")
public class AppController {

    @Resource
    private AppService appService;

    @PostMapping(value = "update")
    public Dto updateApp(@RequestBody ReceivedAppInfo appInfo, HttpServletRequest request){
        return appService.updateApp(appInfo,request);
    }

}
