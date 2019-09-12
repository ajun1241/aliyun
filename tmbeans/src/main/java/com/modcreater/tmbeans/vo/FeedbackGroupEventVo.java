package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/9/12 10:53
 */
@Data
public class FeedbackGroupEventVo implements Serializable {
    private String appType;
    private String userId;
    private String choose;//0：拒绝；1：同意
    private String msgId;//消息Id
}
