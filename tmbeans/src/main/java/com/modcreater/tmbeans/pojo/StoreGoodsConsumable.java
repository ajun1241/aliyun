package com.modcreater.tmbeans.pojo;


import lombok.Data;

@Data
public class StoreGoodsConsumable {

  private long id;
  private long goodsId;
  private long consumableGoodsId;
  private Double registeredRatioIn;
  private String registeredRationInUnit;
  private long registeredRatioOut;
  private String registeredRationOutUnit;
  private long registeredTime;
  private Double consumptionRate;

}
