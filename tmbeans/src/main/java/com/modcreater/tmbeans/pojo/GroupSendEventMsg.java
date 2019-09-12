package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * @Author: AJun
 */

@Data
public class GroupSendEventMsg {

  private Long id;
  private Long groupId;
  private Long eventId;
  private Long senderId;
  private Long processState;
  private Long processDate;
  private Long processBy;

}
