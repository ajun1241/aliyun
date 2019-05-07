package com.modcreater.tmbeans.vo;

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
     *
     */
    private String type;

}
