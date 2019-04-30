package com.modcreater.tmbiz.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.vo.DayEvents;
import com.modcreater.tmbeans.vo.UploadingEventVo;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-04-29
 * Time: 11:32
 */
public interface EventService {

    /**
     * 添加新的事件(线上操作)
     * @param uploadingEventVo
     * @return
     */
    Dto addNewEvents(UploadingEventVo uploadingEventVo);

    /**
     * 删除事件
     * @param dayEvents
     * @return
     */
    Dto deleteEvents(DayEvents dayEvents);

    /**
     * 修改事件
     * @param dayEvents
     * @return
     */
    Dto updateEvents(DayEvents dayEvents);

    /**
     * 查询事件
     * @param dayEvents
     * @return
     */
    Dto searchEvents(DayEvents dayEvents);

    /**
     * 同步本地数据到线上
     * @param dayEvents
     * @return
     */
    Dto synchronousUpdate(DayEvents dayEvents);

}
