package com.modcreater.tmbeans.vo;

import lombok.Data;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/9/10 9:14
 */
@Data
public class GroupApplyDisposeVo {
    private String appType;
    private String userId;

    private String memberId;//申请加入的成员Id
    private String choose;//同意还是拒绝（0：拒绝；1：同意）
    private String groupMsgId;//消息id
}
