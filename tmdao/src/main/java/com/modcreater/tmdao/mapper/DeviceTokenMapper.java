package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.Userdevicetoken;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/7/9 13:51
 */
public interface DeviceTokenMapper {

    /**
     * 添加deviceToken
     * @param userId
     * @param deviceToken
     * @param appType
     * @return
     */
    int insertDeviceToken(String userId,String deviceToken,String appType);

    /**
     * 更换deviceToken
     * @param userId
     * @param deviceToken
     * @param appType
     * @return
     */
    int updDeviceToken(String userId,String deviceToken,String appType);

    /**
     * 查询deviceToken
     * @param userId
     * @return
     */
    Userdevicetoken queryDeviceToken(String userId);
}
