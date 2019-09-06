package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/9/6 9:24
 */
@Data
public class GroupMsgVo implements Serializable {
    private String appType;
    private String userId;

    private String targetId;//接收者Id
    private String groupId;//团队Id
    private String validationContent;//验证消息内容
    private String choose;//同意还是拒绝（0：拒绝；1：同意）

}
