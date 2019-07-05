package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.MsgStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/6/11 14:02
 */
@Mapper
public interface MsgStatusMapper {
    /**
     * 新增数据
     * @param msgStatus
     * @return
     */
    int addNewMsg(MsgStatus msgStatus);

    /**
     * 查询状态
     * @param id
     * @return
     */
    MsgStatus queryMsg(String id);

    /**
     * 修改信息状态
     * @param status
     * @param id
     * @return
     */
    int updateMsgStatus(@Param("status") String status, @Param("id") String id);

    /**
     * 添加新的历史消息
     * @param msgOwnerId
     * @param eventId
     * @param msgSenderId
     * @param content
     * @param createDate
     * @return
     */
    int addNewEventMsg(@Param("msgOwnerId") String msgOwnerId,@Param("eventId") Long eventId,@Param("msgSenderId") String msgSenderId,@Param("content") String content,@Param("createDate")  Long createDate);

    /**
     * 查询需要将状态修改为3的消息
     * @param type
     * @param time
     * @return
     */
    List<Long> getNeedChangedIds(@Param("type") String type ,@Param("time") Long time);
}
