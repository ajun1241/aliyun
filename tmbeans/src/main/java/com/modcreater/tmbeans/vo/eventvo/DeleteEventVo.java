package com.modcreater.tmbeans.vo.eventvo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-04-30
 * Time: 16:31
 */
@Data
public class DeleteEventVo implements Serializable {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 事件ID(1904301700)
     */
    private String eventId;
    /**
     * 事件状态(0:未完成;1:已完成;2:已删除)
     */
    private String eventStatus;
    /**
     *
     */
    private String apptype;

}
