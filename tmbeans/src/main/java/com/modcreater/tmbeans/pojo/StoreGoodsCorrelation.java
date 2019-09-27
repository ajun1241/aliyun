package com.modcreater.tmbeans.pojo;

import lombok.Data;

@Data
public class StoreGoodsCorrelation {

  private Long id;
  private Long goodsParentId;
  private Long goodsSonId;

}
