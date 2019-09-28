package com.modcreater.tmbeans.show.goods;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-24
 * Time: 10:33
 */
@Data
public class ShowGoodsStockInfo {

    private String goodsId;

    private String goodsPicture;

    private String goodsName;

    private String goodsType;

    private String goodsUnit;

    private String goodsStock;

    /**
     * 选中状态(前端用)
     */
    private Boolean selectStatus = false;

}
