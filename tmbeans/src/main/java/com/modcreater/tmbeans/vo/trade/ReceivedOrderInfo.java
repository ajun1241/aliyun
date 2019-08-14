package com.modcreater.tmbeans.vo.trade;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-27
 * Time: 9:45
 */
@Data
public class ReceivedOrderInfo {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 服务类型
     */
    private String serviceId;
    /**
     * 用户优惠券ID
     */
    private String discountUserId;
    /**
     * 订单标题
     */
    private String orderTitle;
    /**
     * 订单类型(time:次卡;month:月卡;year:年卡;)
     */
    private String serviceType;
    /**
     * 支付总金额
     */
    private Double paymentAmount;
    /**
     * 购买数量
     */
    private Long number;
    /**
     * 用户备注
     */
    private String userRemark;

    private String appType;

}
