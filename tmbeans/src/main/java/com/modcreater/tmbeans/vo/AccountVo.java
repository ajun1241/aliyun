package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
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
    private String birthday;
    private long userType;
    private String time;
    private String type;
//    private String headImgUrl;
}
