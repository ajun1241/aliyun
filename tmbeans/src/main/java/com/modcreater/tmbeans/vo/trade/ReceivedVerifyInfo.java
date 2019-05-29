package com.modcreater.tmbeans.vo.trade;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-28
 * Time: 14:49
 */
@Data
public class ReceivedVerifyInfo {

    private String userId;

    private String id;

    private String serviceId;

    private String orderTitle;

    private Double paymentAmount;

    private String payChannel;

    private String outTradeNo;
}
