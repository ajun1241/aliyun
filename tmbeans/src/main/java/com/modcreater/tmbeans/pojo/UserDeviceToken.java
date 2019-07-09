package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * @Author: AJun
 */
@Data
public class UserDeviceToken {

  private Long id;
  private Long userId;
  private String deviceToken;
  private Long appType;

}
