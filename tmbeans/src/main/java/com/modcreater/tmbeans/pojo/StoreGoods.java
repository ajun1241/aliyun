package com.modcreater.tmbeans.pojo;

import lombok.Data;

@Data
public class StoreGoods {

  private Long id;
  private Long storeId;
  private String goodsName;
  private String goodsBrand;
  private String goodsPicture;
  private String goodsBarCode;
  private String goodsSpecifications;
  private String goodsAllergen;
  private String goodsUnit;
  private Long goodsTypeId;
  private String goodsFUnit;
  private Long faUnitNum;

}
