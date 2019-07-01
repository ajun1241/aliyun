package com.modcreater.tmbeans.vo.eventvo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *  回应邀请视图对象
 * @Author: AJun
 * @Date: 2019/6/25 11:00
 */
@Data
public class FeedbackInviteVo implements Serializable {
    /**
     * 你的id
     */
    private String userId;
    /**
     * 发送端
     */
    private String appType;
    /**
     * 选择（0：不同意；1：同意; ）
     */
    private String choose;
    /**
     * 忽略冲突任然添加(0:拒绝；1：确认)
     */
    private String isHold;
    /**
     * 消息来源Id
     */
    private String fromId;
    /**
     * 事件的id
     */
    private String eventId;
    /**
     * 回应的消息id
     */
    private String msgId;
}
