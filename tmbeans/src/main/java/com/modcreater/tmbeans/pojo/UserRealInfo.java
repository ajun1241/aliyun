package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: AJun
 */
@Data
public class UserRealInfo {

    private Long id;
    private Long userId;
    private String userRealName;
    private String userIdNo;
    private String userIdCardFront;
    private String userIdCardVerso;
    private Long realStatus;
    private Date createDate;
    private Date modifyDate;
    private String realBy;
    private Integer category;
}
