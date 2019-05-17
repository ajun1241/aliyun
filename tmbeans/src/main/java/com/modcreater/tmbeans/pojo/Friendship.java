package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 好友关系表
 * @Author: AJun
 */
@Data
public class Friendship implements Serializable {

  private long id;
  private long userId;
  private long friendId;
  private long invite;
  private long sustain;
  private long hide;

}
