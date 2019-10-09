package com.modcreater.tmbeans.pojo;

import lombok.Data;

@Data
public class StoreOfflineOrders {

  private String orderNumber;
  private Long sourceStoreId;
  private Long userId;
  private String goodsListId;
  private Double paymentAmount;
  private String createDate;
  private Long payTime;
  private String payChannel;
  private String outTradeNo;
  private Long orderStatus;

}
