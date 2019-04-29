package com.modcreater.tmbiz.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.SingleEvent;

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
     * 添加新的事件
     * @param singleEvent 事件
     * @return
     */
    Dto addNewEvents(SingleEvent singleEvent);

    /**
     * 根据用户的ID删除事件
     * @param userId 用户ID
     * @return
     */
    Dto deleteEvents(String userId);

    /**
     * 修改事件
     * @param singleEvent 事件
     * @return
     */
    Dto updateEvents(SingleEvent singleEvent);

    /**
     * 根据用户ID查询事件
     * @param userId
     * @return
     */
    Dto searchEvents(String userId);

}
