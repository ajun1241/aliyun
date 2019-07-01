package com.modcreater.tmbeans.vo.eventvo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/22 10:06
 */
@Data
public class FeedbackEventInviteVo implements Serializable {
    private String appType;
    /**
     * 选择（1；同意; 0：不同意;）
     */
    private String choose;

    private String userId;

    /**
     * 发送邀请时的事件id
     */

    private String eventId;

    /**
     * 消息来源Id
     */
    private String fromId;

    /**
     * 忽略冲突任然添加(0:拒绝；1：确认)
     */
    private String isHold;

    /**
     * 回应的消息id
     */
    private String msgId;
}
