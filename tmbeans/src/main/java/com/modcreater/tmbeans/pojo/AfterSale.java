package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * @Author: AJun
 */
@Data
public class AfterSale {

  private Long id;
  private Long userId;
  private String serviceType;
  private String createDate;
  private String disposeDate;
  private Long disposeStatus;

}
