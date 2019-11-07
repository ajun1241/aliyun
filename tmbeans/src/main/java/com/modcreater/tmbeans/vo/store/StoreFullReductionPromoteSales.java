package com.modcreater.tmbeans.vo.store;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-11-07
 * Time: 13:58
 */
@Data
public class StoreFullReductionPromoteSales {

    private String userId;

    private String appType;

    private String storeId;

    private Double[] fullValue;

    private Double[] disValue;

    private Long startTime;

    private Long endTime;

    private String share;

}
