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
     * 0 ：时间没到；1：时间到了
     */
    private String timeUp;

    /**
     * 选择（0；同意; 1：不同意; 2：未回应）
     */
    private String choose;

    private String userId;

    /**
     * 发送邀请时的扩展数据
     */

    private String extraData;

    /**
     * 拒绝理由
     */
    private String rejectContent;

    /**
     * 忽略冲突任然添加(0:拒绝；1：确认)
     */
    private String isHold;

    /**
     * 回应的消息id
     */
    private String msgId;
}
