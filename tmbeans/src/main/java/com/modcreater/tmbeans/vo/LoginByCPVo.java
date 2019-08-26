package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/8/19 14:04
 */
@Data
public class LoginByCPVo implements Serializable {
    private String userCode;
    private String userPassword;
    private String appType;
    private String deviceToken;
}
