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
  private String msgContent;
  private String msgBody;
  private Long processState;
  private Long readStatus;
  private String sendDate;
  private Long processDate;
  private Long processBy;

}
