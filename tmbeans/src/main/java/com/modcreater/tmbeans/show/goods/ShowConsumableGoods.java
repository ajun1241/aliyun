package com.modcreater.tmbeans.show.goods;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-26
 * Time: 14:41
 */
@Data
public class ShowConsumableGoods {

    private String goodsId;

    private String goodsPicture;

    private String goodsName;

    private String goodsUnit;

    private Long stockNum;

    private String goodsType;
    /**
     * 选中状态(前端用)
     */
    private Boolean selectStatus = false;

}
