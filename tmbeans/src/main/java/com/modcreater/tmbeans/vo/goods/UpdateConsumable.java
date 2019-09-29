package com.modcreater.tmbeans.vo.goods;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-29
 * Time: 10:26
 */
@Data
public class UpdateConsumable {

    private String userId;

    private String appType;

    private String consumableId;

    private Long registeredRatioIn;

    private Long registeredRatioOut;

    private Double consumptionRate;

}
