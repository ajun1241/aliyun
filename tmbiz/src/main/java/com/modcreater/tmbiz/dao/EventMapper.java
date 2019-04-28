package com.modcreater.tmbiz.dao;

import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.vo.QueryEventVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EventMapper {
    /**
     * 查询事件
     * @param queryEventVo
     * @return
     */
    SingleEvent queryEvent(QueryEventVo queryEventVo);

    /**
     * 添加事件
     * @param singleEvent
     * @return
     */
    int addEvent(SingleEvent singleEvent);

    /**
     * 删除事件
     * @param eventId
     * @return
     */
    int deleteEvent(String eventId);

    /**
     * 修改事件
     * @param eventId
     * @return
     */
    int updateEvent(String eventId);
}
