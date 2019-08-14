package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.config.annotation.Safety;
import com.modcreater.tmauth.service.AppService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.app.ReceivedAppInfo;
import com.modcreater.tmbeans.vo.app.ReceivedNotice;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedIdExtra;
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
 * Time: 8:43
 */
@RestController
@RequestMapping(value = "/app/")
public class AppController {

    @Resource
    private AppService appService;

    /**
     * 更新APP
     *
     * @param appInfo
     * @param request
     * @return
     */

    @Safety
    @PostMapping(value = "update")
    @ApiOperation("更新APP")
    public Dto updateApp(@RequestBody ReceivedAppInfo appInfo, HttpServletRequest request) {
        return appService.updateApp(appInfo, request);
    }

    @Safety
    @PostMapping(value = "getactivityinform")
    @ApiOperation("获取公告")
    public Dto getActivityInform(@RequestBody ReceivedId receivedId, HttpServletRequest request) {
        return appService.getActivityInform(receivedId, request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "getuserdiscountcount")
    @ApiOperation("获取用户优惠券数量")
    public Dto getUserDiscountCount(@RequestBody ReceivedId receivedId, HttpServletRequest request) {
        return appService.getUserDiscountCount(receivedId, request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "getuserdiscountlist")
    @ApiOperation("获取用户优惠券列表")
    public Dto getUserDiscountList(@RequestBody ReceivedIdExtra receivedId, HttpServletRequest request) {
        return appService.getUserDiscountList(receivedId, request.getHeader("token"));
    }

}
