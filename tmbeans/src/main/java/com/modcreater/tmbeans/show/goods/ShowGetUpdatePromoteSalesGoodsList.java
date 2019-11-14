package com.modcreater.tmbeans.show.goods;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-11-14
 * Time: 10:27
 */
@Data
public class ShowGetUpdatePromoteSalesGoodsList {

    private String goodsId;
    private String goodsPicture;
    private String goodsName;
    private String goodsPrice;
    private String goodsUnit;
    private String stockNum;
    private Boolean selectStatus = false;


}
