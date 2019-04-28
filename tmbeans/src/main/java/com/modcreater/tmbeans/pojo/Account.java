package com.modcreater.tmbeans.pojo;

import lombok.Data;

@Data
public class Account {

  private long id;
  private String userCode;
  private String verificationCode;
  private String userName;
  private long gender;
  private java.sql.Timestamp birthday;
  private String IDcard;
  private java.sql.Timestamp createDate;
  private java.sql.Timestamp modifyDate;
  private java.sql.Timestamp offlineTime;
  private long userType;
  private String headImgUrl;
  private java.sql.Timestamp time;

}
