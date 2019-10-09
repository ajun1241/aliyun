package com.modcreater.tmbeans.pojo;

import lombok.Data;

@Data
public class StoreGoodsStock {

  private Long id;
  private Long goodsId;
  private Long storeId;
  private Double goodsPrice;
  private Long stockNum;
  private Long goodsStatus;
  private String goodsBarCode;

}
