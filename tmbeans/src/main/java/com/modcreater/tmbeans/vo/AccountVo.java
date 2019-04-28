package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 账号信息视图对象
 */
@Data
public class AccountVo implements Serializable {
    private String userCode;
    private String userName;
    private long gender;
    private java.sql.Timestamp birthday;
    private long userType;
    private String headImgUrl;
}
