package com.modcreater.tmbeans.pojo;

import lombok.Data;

@Data
public class StoreInfo {

  private Long id;
  private Long userId;
  private String storeName;
  private String storePicture;
  private String storeAddress;
  private Double longitude;
  private Double latitude;
  private String locationAddress;
  private Long businessScope;
  private Double wallet;
  private String createDate;
  private Long status;
  private Long attestationId;
  private String detailAddress;
  private String openStoreHours;
  private String closeStoreHours;
  private String phoneNumber;


}
