package com.modcreater.tmbeans.vo.eventvo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/27 17:07
 */
@Data
public class FeedbackEventBackerVo implements Serializable {
    private String appType;
    /**
     * 0 ：时间没到；1：时间到了
     */
    private String timeUp;
    private String choose;
    private String userId;
    /**
     * 发送邀请时的扩展数据
     */
    private String extraData;
}
