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
     * 查询所有好友消息
     * @param userId
     * @param msgStatus
     * @return
     */
    List<SystemMsgRecord> queryAllUnreadMsg(String userId,String msgStatus,String msgType);

    /**
     *修改未读消息为已读
     * @param userId
     * @param fromId
     * @param msgContent
     * @param msgStatus
     * @return
     */
    int updateUnreadMsg(String userId,String fromId,String msgStatus,String msgContent);

    /**
     * 查询一条好友请求消息
     * @param systemMsgRecord
     * @return
     */
    int queryMsgByUserIdFriendIdMsgType(SystemMsgRecord systemMsgRecord);

    /**
     * 查询一条好友请求消息详情
     * @param systemMsgRecord
     * @return
     */
    SystemMsgRecord queryMsgByUserIdFriendIdMsgTypeDetial(SystemMsgRecord systemMsgRecord);

    /**
     * 删除消息记录
     * @param userId
     * @param fromId
     * @return
     */
    int deleteSystemMsg(String userId,String fromId);
}
