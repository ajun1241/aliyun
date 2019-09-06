package com.modcreater.tmbeans.pojo;

import lombok.Data;

@Data
public class Informationsafety {

  private Long id;
  private Long userId;
  private String createDate;
  private Long operationType;
  private String networkSourceAddress;
  private String networkTargetAddress;
  private String appType;
  private String operationContent;

}
