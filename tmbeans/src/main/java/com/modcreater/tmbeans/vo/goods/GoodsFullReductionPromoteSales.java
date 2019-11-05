package com.modcreater.tmbeans.vo.goods;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-11-05
 * Time: 10:41
 */
@Data
public class GoodsFullReductionPromoteSales {

    private String userId;

    private String appType;

    private String storeId;

    private String[] goodsId;

    private Double fullValue;

    private Double disValue;

}
