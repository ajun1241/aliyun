package com.modcreater.tmbeans.pojo;

import lombok.Data;

@Data
public class GroupValidation {

  private Long id;
  private Long userId;
  private Long groupId;
  private String validationContent;
  private String validationSource;
  private Long processState;
  private Long processDate;
  private Long processBy;

}
