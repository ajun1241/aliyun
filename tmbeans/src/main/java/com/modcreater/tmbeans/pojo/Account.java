package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
public class Account implements Serializable {

  private long id;
  private String userCode;
  private String verificationCode;
  private String userName;
  private long gender;
  private Date birthday;
  private String IDcard;
  private Date createDate;
  private Date modifyDate;
  private Date offlineTime;
  private long userType;
  private String headImgUrl;
  private Date time;

}
