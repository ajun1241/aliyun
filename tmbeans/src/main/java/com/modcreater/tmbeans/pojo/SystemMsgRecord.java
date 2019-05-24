package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: AJun
 */
@Data
public class SystemMsgRecord implements Serializable {

  private long id;
  private long userId;
  private String msgContent;
  private long msgStatus;
  private long msgType;
  private java.sql.Timestamp createDate;

}
