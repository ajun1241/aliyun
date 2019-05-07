package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.vo.SingleEventForDatabase;
import com.modcreater.tmbeans.vo.UploadingEventVo;
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
     *
     * @param singleEvent
     * @return
     */
    int uploadingEvents(SingleEvent singleEvent);

    /**
     * 撤销事件
     *
     * @param singleEvent
     * @return
     */
    int withdrawEventsByUserId(SingleEvent singleEvent);

    /**
     * 更新事件
     *
     * @param singleEvent
     * @return
     */
    int alterEventsByUserId(SingleEvent singleEvent);

    /**
     * 查询事件
     *
     * @param singleEvent
     * @return
     */
    List<SingleEvent> queryEvents(SingleEvent singleEvent);

    /**
     * 同步修改
     * @param singleEvent
     * @return
     */
    int updOldEvent(SingleEvent singleEvent);

    /**
     * 根据日查找事件并根据事件等级(level)排序
     * @param singleEvent
     * @return
     */
    List<SingleEvent> queryByDayOrderByLevel(SingleEvent singleEvent);

    /**
     * 根据日查找事件并根据事件等级(level)和事件开始时间(startTime)排序
     * @param singleEvent
     * @return
     */
    List<SingleEvent> queryByDayOrderByLevelAndDate(SingleEvent singleEvent);

}
