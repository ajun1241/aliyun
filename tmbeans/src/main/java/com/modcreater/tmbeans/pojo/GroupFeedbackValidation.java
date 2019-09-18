package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * @Author: AJun
 */
@Data
public class GroupFeedbackValidation {

  private Long id;
  private Long processId;
  private Long receiverId;
  private Long groupId;
  private String msgContent;
  private Long readStatus;
  private Long processState;
  private String sendDate;

}
