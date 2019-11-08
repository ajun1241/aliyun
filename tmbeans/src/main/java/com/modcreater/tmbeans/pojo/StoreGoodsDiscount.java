package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-11-08
 * Time: 14:15
 */
@Data
public class StoreGoodsDiscount {

  private long id;

  private long bindingId;

  private long goodsId;

  private long storeId;

  private long discountedType;

  private Double value;

  private long startTime;

  private long endTime;
}
