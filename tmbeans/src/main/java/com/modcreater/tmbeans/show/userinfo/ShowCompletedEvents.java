package com.modcreater.tmbeans.show.userinfo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-17
 * Time: 15:15
 */
@Data
public class ShowCompletedEvents {
    /**
     * 事件ID
     */
    private String eventId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 事件开始日期
     */
    private String date;

}
