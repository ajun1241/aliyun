package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.SingleEvent;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/6/26 13:58
 */
@Mapper
public interface TempEventMapper {

    /**
     * 添加临时事件(只存创建者的事件)
     * @param singleEvent
     * @return
     */
    int addTempEvent(SingleEvent singleEvent);

    /**
     * 删除临时事件
     * @param eventId
     * @param userId
     * @return
     */
    int deleteTempEvent(String eventId, String userId);

    /**
     * 查询临时事件
     * @param eventId
     * @param userId
     * @return
     */
    SingleEvent queryTempEvent(String eventId, String userId);
}
