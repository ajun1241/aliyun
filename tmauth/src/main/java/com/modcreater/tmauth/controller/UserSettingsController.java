package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.service.UserSettingsService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.usersettings.UserSettingsIdAndStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-21
 * Time: 8:51
 */
@RestController
@RequestMapping("/settings/")
public class UserSettingsController {

    @Resource
    private UserSettingsService userSettingsService;

    @RequestMapping(value = "updateReceiveNewMessage",method = RequestMethod.POST)
    public Dto updateReceiveNewMessage(@RequestBody UserSettingsIdAndStatus idAndStatus , HttpServletRequest request){
        return null;
    }

}
