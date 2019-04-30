package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.SingleEvent;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-04-29
 * Time: 9:20
 */
@Mapper
public interface EventMapper {
    /**
     * 上传新事件
     * @param singleEventsList
     * @return
     */
    int uploadingEvents(List<SingleEvent> singleEventsList);

    /**
     * 撤销事件
     * @param singleEventsList
     * @return
     */
    int withdrawEventsByUserId(List<SingleEvent> singleEventsList);

    /**
     * 更新事件
     * @param singleEventsList
     * @return
     */
    int alterEventsByUserId(List<SingleEvent> singleEventsList);

    /**
     * 查询事件
     * @param singleEvents
     * @return
     */
    List<SingleEvent> queryEvents(SingleEvent singleEvents);

    /**
     * 删除事件(在同步更新接口下)
     * @param singleEvent
     * @return
     */
    int deleteEventsBySynchronousUpdate(SingleEvent singleEvent);

}
