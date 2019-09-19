package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * @Author: AJun
 */
@Data
public class StoreInfo {

  private Long id;
  private Long userId;
  private String storeName;
  private String storePicture;
  private String storeAddress;
  private String businessScope;
  private String createDate;
  private Long status;

}
