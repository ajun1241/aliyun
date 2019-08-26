package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.AppType;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/8/20 10:02
 */
public interface AppTypeMapper {

    /**
     * 新增用户端记录
     * @param userId
     * @param appType
     * @param deviceToken
     * @return
     */
    int insertAppType(String userId,String appType,String deviceToken);

    /**
     * 查询客户端
     * @param userId
     * @return
     */
    AppType queryAppType(String userId);

    /**
     * 修改客户端
     * @param appType
     * @param userId
     * @param deviceToken
     * @return
     */
    int updateAppType(String appType,String userId,String deviceToken);
}
