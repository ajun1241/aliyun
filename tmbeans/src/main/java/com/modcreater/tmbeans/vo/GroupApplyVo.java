package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/9/10 8:59
 */
@Data
public class GroupApplyVo implements Serializable {
    private String appType;
    private String userId;

    private String validationContent;//验证消息内容
    private String sourceId;//消息来源Id
    private String groupId;//团队Id

}
