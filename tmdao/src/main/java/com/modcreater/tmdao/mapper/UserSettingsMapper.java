package com.modcreater.tmdao.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-21
 * Time: 11:26
 */
@Mapper
public interface UserSettingsMapper {

    /**
     * 为用户添加一条新的设置
     * @param userId
     * @return
     */
    int addNewUserSettings(String userId);
}
