package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-11-11
 * Time: 14:25
 */
@Data
public class StoreGoodsFullReduction {

  private long id;
  private long storeId;
  private double fullValue;
  private double disValue;
  private long bindingId;
  private long startTime;
  private long endTime;
}
