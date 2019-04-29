package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 账号信息视图对象
 */
@Data
public class AccountVo implements Serializable {
    private long id;
    private String userCode;
    private String userName;
    private long gender;
    private Date birthday;
    private long userType;
//    private String headImgUrl;
}
