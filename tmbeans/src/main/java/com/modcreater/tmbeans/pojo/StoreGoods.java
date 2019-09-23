package com.modcreater.tmbeans.pojo;

import lombok.Data;

@Data
public class StoreGoods {

  private long id;
  private long storeId;
  private String goodsName;
  private String goodsBrand;
  private String goodsPicture;
  private String goodsBarCode;
  private String goodsSpecifications;
  private String goodsAllergen;
  private double goodsPrice;
  private String goodsUnit;
  private long goodsType;
  private long goodsStatus;

}
