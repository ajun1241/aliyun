package com.modcreater.tmbeans.vo.goods;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-20
 * Time: 15:58
 */
@Data
public class ConsumablesList {

    private String id;
    /**
     * 商品Id(消耗品清单所属商品)
     */
    private String goodsId;
    /**
     * 消耗品Id
     */
    private String consumablesId;
    /**
     * 消耗数量
     */
    private Double consumablesNum;
    /**
     * 可合成数量
     */
    private String finishedNum;

}
