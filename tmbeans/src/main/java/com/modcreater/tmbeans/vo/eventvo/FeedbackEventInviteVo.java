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
    private String timeUp;
    private String choose;
    private String userId;
    private String eventId;
    /**
     * 发送邀请时的扩展数据
     */
    private String extraData;
    /**
     * 拒绝理由
     */
    private String rejectContent;
}
