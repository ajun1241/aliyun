package com.modcreater.tmbeans.vo.goods;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-26
 * Time: 9:44
 */
@Data
public class UpdateGoodsPrice {

    private String userId;

    private String goodsId;

    private String appType;

    private Double unitPrice;

    private String storeId;

}
