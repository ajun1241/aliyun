package com.modcreater.tmbeans.show.goods;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-28
 * Time: 17:07
 */
@Data
public class ShowConsumable {
    private String consumableId;

    private String goodsId;

    private String goodsName;

    private String goodsPicture;

    private String registeredRatioOut;

    private String registeredRationOutUnit;

    private String registeredRatioIn;

    private String registeredRationInUnit;

}
