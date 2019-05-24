package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: AJun
 */
@Data
public class SystemMsgRecord implements Serializable {

  private Long id;
  private Long userId;
  private Long fromId;
  private String msgContent;
  private Long msgStatus;
  private String msgType;
  private Date createDate;

}
