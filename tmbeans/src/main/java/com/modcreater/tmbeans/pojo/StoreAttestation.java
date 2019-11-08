package com.modcreater.tmbeans.pojo;

import lombok.Data;

@Data
public class StoreAttestation {

  private Long id;
  private Long userId;
  private String businessLicense;
  private String exequatur;
  private String storeLogo;
  private String storeName;
  private String address;
  private Double longitude;
  private Double latitude;
  private String locationAddress;
  private String createDate;
  private Long modifiDate;
  private Long disposeDate;
  private Long disposeBy;
  private Long disposeStatus;
  private String refuseMsg;
  private String storefrontPicture;
  private Long businessScope;
  private String detailAddress;
  private String openStoreHours;
  private String closeStoreHours;
  private String phoneNumber;



}
