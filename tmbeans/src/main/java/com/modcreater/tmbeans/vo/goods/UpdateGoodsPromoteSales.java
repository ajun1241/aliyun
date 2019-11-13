package com.modcreater.tmbeans.vo.goods;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-11-13
 * Time: 15:19
 */
@Data
public class UpdateGoodsPromoteSales {

    private String userId;

    private String appType;

    private String storeId;

    private String[] goodsId;

    private String promoteSalesId;

    private Integer discountedType;

    private Double value;

    private Double[] fullValues;

    private Double[] disValues;

    private Integer share;

    private Long startTime;

    private Long endTime;

}
