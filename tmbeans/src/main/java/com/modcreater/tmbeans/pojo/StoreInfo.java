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
  private Double longitude;
  private Double latitude;
  private String businessScope;
  private String createDate;
  private Long status;
  private Long attestationId;
  private Double wallet;

}
