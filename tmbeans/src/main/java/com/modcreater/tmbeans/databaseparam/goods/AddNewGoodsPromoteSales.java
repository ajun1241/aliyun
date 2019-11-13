package com.modcreater.tmbeans.databaseparam.goods;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-11-13
 * Time: 11:17
 */
@Data
public class AddNewGoodsPromoteSales {

    private String id;

    private String goodsId;

    private String value;

    private Long startTime;

    private Long endTime;

    private Integer discountedType;

    private String storeId;

    private String bindingId;

}
