package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.UserSettings;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    /**
     * 根据字段userId修改设置
     * @param type
     * @param userId
     * @param status
     * @return
     */
    int updateUserSettings(@Param("type") String type,@Param("userId") String userId,@Param("status") int status);

    /**
     * 拉取用户设置
     * @param userId
     * @return
     */
    UserSettings queryAllSettings(String userId);
}
