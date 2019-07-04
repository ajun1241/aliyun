package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.MsgStatus;
import org.apache.ibatis.annotations.Mapper;

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
    int updateMsgStatus(String status,String id);

    /**
     *
     * @param msgOwnerId
     * @param eventId
     * @param msgSenderId
     * @param content
     * @param createDate
     * @return
     */
    int addNewEventMsg(String msgOwnerId, Long eventId, String msgSenderId, String content,  Long createDate);
}
