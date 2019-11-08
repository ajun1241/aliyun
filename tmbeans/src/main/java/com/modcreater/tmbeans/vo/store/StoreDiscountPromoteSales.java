package com.modcreater.tmbeans.vo.store;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-11-07
 * Time: 17:14
 */
@Data
public class StoreDiscountPromoteSales {

    private String userId;

    private String appType;

    private String storeId;

    private Double value;

    private Long startTime;

    private Long endTime;

}
