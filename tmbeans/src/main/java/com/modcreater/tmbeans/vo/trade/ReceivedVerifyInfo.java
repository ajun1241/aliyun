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

    private String id;

    private String userId;

    private Double paymentAmount;

    private String outTradeNo;

    private String appId;

    private String sellerId;

    private String appType;
}
