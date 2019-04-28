package com.modcreater.tmbiz.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.vo.QueryEventVo;

public interface EventService {
    /**
     * 查询事件
     * @param queryEventVo
     * @return
     */
    Dto queryEvent(QueryEventVo queryEventVo);

    /**
     * 添加事件
     * @param singleEvent
     * @return
     */
    Dto addEvent(SingleEvent singleEvent);

    /**
     * 删除事件
     * @param eventId
     * @return
     */
    Dto deleteEvent(String eventId);

    /**
     * 修改事件
     * @param eventId
     * @return
     */
    Dto updateEvent(String eventId);
}
