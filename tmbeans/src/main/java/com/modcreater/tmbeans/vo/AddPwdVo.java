package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 添加密码用的视图
 */
@Data
public class AddPwdVo implements Serializable {
    private String userId;
    private String userPassword;
    private String userName;
    private String headImgUrl;
}
