package com.modcreater.tmbeans.pojo;

import lombok.Data;

@Data
public class StorePurchaseRecords {

  private Long id;
  private Long orderNumber;
  private Long goodsId;
  private Long targetStoreId;
  private Long sourceStoreId;
  private Double transactionPrice;
  private String createDate;
  private Long goodsCount;
  private Long status;

}
