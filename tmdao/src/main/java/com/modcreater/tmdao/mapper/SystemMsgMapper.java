package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.SystemMsgRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/24 16:06
 */
@Mapper
public interface SystemMsgMapper {
    /**
     * 添加一条消息
     * @param map
     * @return
     */
    int addNewMsg(Map<String,String> map);

    /**
     * 查询所有未读消息
     * @param userId
     * @param msgStatus
     * @return
     */
    List<SystemMsgRecord> queryAllUnreadMsg(String userId,String msgStatus);

    /**
     *修改未读消息为已读
     * @param userId
     * @return
     */
    int updateUnreadMsg(String userId,String fromId,String msgStatus);

    /**
     * 查询一条好友请求消息
     * @param systemMsgRecord
     * @return
     */
    int queryMsgByUserIdFriendIdMsgType(SystemMsgRecord systemMsgRecord);
}
