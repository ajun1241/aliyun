package com.modcreater.tmbeans.pojo;


import lombok.Data;

@Data
public class StoreGoodsConsumable {

  private Long id;
  private Long goodsId;
  private Long consumableGoodsId;
  private Double registeredRatioIn;
  private String registeredRationInUnit;
  private Long registeredRatioOut;
  private String registeredRationOutUnit;
  private Long registeredTime;
  private Double consumptionRate;

}
