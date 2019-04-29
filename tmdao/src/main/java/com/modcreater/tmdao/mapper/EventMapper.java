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
     * @param singleEvents 事件
     * @return
     */
    int uploadingEvents(SingleEvent singleEvents);

    /**
     * 根据用户Id撤销事件
     * @param userId 用户Id
     * @return
     */
    int withdrawEventsByUserId(String userId);

    /**
     * 根据用户Id更新事件
     * @param singleEvent 事件
     * @return
     */
    int alterEventsByUserId(SingleEvent singleEvent);

    /**
     * 同步事件
     * @param userId 用户Id
     * @return
     */
    List<SingleEvent> synchronizeEvents(String userId);

}
