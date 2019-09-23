package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * @Author: AJun
 */
@Data
public class StoreAttestation {

  private Long id;
  private String userId;
  private String businessLicense;
  private String createDate;
  private Long disposeDate;
  private Long disposeBy;
  private Long disposeStatus;
  private String refuseMsg;
}