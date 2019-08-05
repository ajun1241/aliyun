package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 好友关系表
 * @Author: AJun
 * id
 * userId
 * friendId
 * invite
 * sustain
 * hide
 * status
 * cerateDate
 * remark
 */
@Data
public class Friendship implements Serializable {

  private Long id;
  private Long userId;
  private Long friendId;
  private Long invite;
  private Long sustain;
  private Long hide;
  private Long diary;
  private Long status;
  private Long flag;
  private Date cerateDate;
  private String remark;

}
