package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class StorePurchaseRecords {

  private Long id;
  private Long orderNumber;
  private Long goodsId;
  private Long targetStoreId;
  private Long sourceStoreId;
  private Double transactionPrice;
  private Date createDate;
  private Long goodsCount;
  private Long status;
  private Long changeGoodsId;

}
