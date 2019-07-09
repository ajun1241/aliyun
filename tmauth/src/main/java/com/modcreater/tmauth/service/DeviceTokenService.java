package com.modcreater.tmauth.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.DeviceTokenVo;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/7/9 10:44
 */
public interface DeviceTokenService {

    /**
     * 生成/置换DeviceToken 和 appType
     * @param deviceTokenVo
     * @param token
     * @return
     */
    Dto replaceDeviceToken(DeviceTokenVo deviceTokenVo, String token);
}
