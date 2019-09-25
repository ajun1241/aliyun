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
  private Double goodsPrice;
  private String goodsUnit;
  private String goodsTypeId;
  private Long goodsStatus;

}
