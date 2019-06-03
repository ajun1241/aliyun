package com.modcreater.tmauth.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.app.ReceivedAppInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-03
 * Time: 9:00
 */
public interface AppService {

    /**
     * 更新App
     * @param appInfo
     * @param request
     * @return
     */
    Dto updateApp(ReceivedAppInfo appInfo, HttpServletRequest request);
}
