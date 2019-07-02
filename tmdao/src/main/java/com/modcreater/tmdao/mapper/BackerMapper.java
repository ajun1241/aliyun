package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.Backers;
import com.modcreater.tmbeans.show.backer.ShowFriendList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/27 16:17
 */
@Mapper
public interface BackerMapper {
    /**
     * 添加事件支持者
     * @param userId
     * @param backerId
     * @return
     */
    int addBackers(String userId,String backerId);

    /**
     * 获取好友列表
     * @param userId
     * @return
     */
    List<ShowFriendList> getFriendList(String userId);

    /**
     * 获取我的支持者
     * @param userId
     * @return
     */
    Backers getMyBacker(String userId);

    /**
     * 修改用户支持者
     * @param userId
     * @param friendId
     * @return
     */
    int updateBacke(@Param("userId") String userId,@Param("friendId") String friendId);

    /**
     * 删除我的支持者
     * @param userId
     * @return
     */
    int deleteBacker(@Param("userId")String userId);

    /**
     * 查询当天的事件
     * @param year
     * @param month
     * @param day
     * @return
     */
    List<Map<String,Object>> findBackerForEvent(String year,String month,String day);
}
