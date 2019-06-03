package com.modcreater.tmbiz.controller;

import com.modcreater.tmbeans.vo.BackendLoginVo;
import com.modcreater.tmbeans.vo.LoginVo;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/6/3 17:23
 */
@RestController
@RequestMapping(value = "/backend/")
public class backendController {

    /**
     * 添加一条事件
     *
     * @param backendLoginVo
     * @return
     */
    @PostMapping(value = "login")
    public String login(@RequestBody BackendLoginVo backendLoginVo) {
        if (!ObjectUtils.isEmpty(backendLoginVo)){
            System.out.println("*****************************************登录*****************************************");
        }
        return null;
    }
}
