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
     * @param createDate
     * @return
     */
    int addBackers(@Param("userId") String userId,@Param("backerId") String backerId,@Param("createDate") Long createDate);

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
    Backers getRealMyBacker(String userId);

    /**
     * 获取我的支持者(可能是未同意)
     * @param userId
     * @return
     */
    Backers getMyBacker(String userId);

    /**
     * 修改用户支持者
     * @param userId
     * @param friendId
     * @param createDate
     * @return
     */
    int updateBacker(@Param("userId") String userId,@Param("friendId") String friendId,@Param("createDate") Long createDate);

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

    /**
     * 修改消息状态
     * @param msgId
     * @param userId
     * @param status
     * @return
     */
    int updateMsgStatus(@Param("msgId")String msgId,@Param("userId") String userId,@Param("status") String status);

    /**
     * 修改支持者状态
     * @param receiverId
     * @param i
     * @return
     */
    int updateBackerStatus(String receiverId, int i);
}
