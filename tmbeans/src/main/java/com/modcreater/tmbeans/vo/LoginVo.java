package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginVo implements Serializable {
    private String userCode;
    private String appType;
    private long userType;
    private String deviceToken;
}
