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
public class GroupCardVo implements Serializable {
    private String appType;
    private String userId;

    private String[] targetId;//团队名片接收者Id
    private String groupId;//团队Id



}
