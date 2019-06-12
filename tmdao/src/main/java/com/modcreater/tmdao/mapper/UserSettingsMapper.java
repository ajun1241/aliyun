package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.UserSettings;
import com.modcreater.tmbeans.show.usersettings.ShowFriendList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
     * @param type 要修改的设置字段(invite/sustain)
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

    /**
     * 获取邀请权限好友列表
     * @param userId
     * @return
     */
    List<ShowFriendList> getInviteFriendList(String userId);

    /**
     * 获取支持权限好友列表
     * @param userId
     * @return
     */
    List<ShowFriendList> getSupportFriendList(String userId);

    /**
     * 获取查看事件权限好友列表
     * @param userId
     * @return
     */
    List<ShowFriendList> getHideFriendList(String userId);

    /**
     * 获取好友查看权限
     * @param userId
     * @param friendId
     * @return
     */
    int getIsHideFromFriend(String userId, String friendId);

    /**
     * 获取用户设置列表中的friendHide属性值
     * @param friendId
     * @return
     */
    int getFriendHide(String friendId);

    /**
     * 获取用户是否开启了勿扰模式
     * @param userId
     * @return
     */
    Long getDND(String userId);

    /**
     * 修改对好友的邀请/支持/不被看权限
     * @param type
     * @param friendId
     * @param status
     * @param userId
     * @return
     */
    int updateUserSettingsToFriends(@Param("type") String type,@Param("friendId") String friendId,@Param("status") int status ,@Param("userId")String userId);

    /**
     * 判断用户是否已经添加过设置
     * @param userId
     * @return
     */
    int isUserSettingsExists(String userId);
}
