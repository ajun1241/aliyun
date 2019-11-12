package com.modcreater.tmbeans.vo.store;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-11-12
 * Time: 13:51
 */
@Data
public class UpdateStorePromoteSales {

    private String userId;

    private String appType;

    private String storeId;

    private String promoteSalesId;

    private Integer discountedType;

    private Double value;

    private Double[] fullValues;

    private Double[] disValues;

    private Integer share;

    private Long startTime;

    private Long endTime;

}
