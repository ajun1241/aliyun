package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
public class Account implements Serializable {

  private Long id;
  private String userCode;
  private String userPassword;
  private String verificationCode;
  private String userName;
  private Long gender;
  private String birthday;
  private String IDcard;
  private Date createDate;
  private Date modifyDate;
  private String offlineTime;
  private Long userType;
  private String headImgUrl;
  private String time;

}
