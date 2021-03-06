package com.modcreater.tmbeans.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-27
 * Time: 9:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOrders {
    /**
     * 订单ID
     */
    private String id;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 服务类型(服务ID)
     */
    private String serviceId;
    /**
     * 订单标题(详情)
     */
    private String orderTitle;
    /**
     * 服务种类(次卡月卡年卡永久)
     */
    private String serviceType;
    /**
     * 支付金额
     */
    private Double paymentAmount;
    /**
     * 数量
     */
    private Long number;
    /**
     * 创建时间
     */
    private Long createDate;
    /**
     * 支付时间
     */
    private String payTime;
    /**
     * 支付类型
     */
    private String payChannel;
    /**
     * 外部订单号
     */
    private String outTradeNo;
    /**
     *
     */
    private String orderStatus;
    /**
     *
     */
    private String remark;

}
