package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * @Author: AJun
 */
@Data
public class GroupSystemMsg {

  private Long id;
  private Long senderId;
  private Long receiverId;
  private Long groupValidationId;
  private String msgContent;
  private Long readStatus;
  private String sendDate;

}
