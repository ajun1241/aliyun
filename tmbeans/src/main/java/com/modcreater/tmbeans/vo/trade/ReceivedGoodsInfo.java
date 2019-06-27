package com.modcreater.tmbeans.vo.trade;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-26
 * Time: 9:55
 */
@Data
public class ReceivedGoodsInfo {

    private String userId;

    private String appType;
    /**
     * 订单类型(time:次卡;month:月卡;year:年卡;perpetual:永久)
     */
    private String serviceType;
    /**
     * 服务ID
     */
    private String serviceId;

    private Long num;

}
