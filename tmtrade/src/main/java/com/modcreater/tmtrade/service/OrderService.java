package com.modcreater.tmtrade.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.UserOrders;
import com.modcreater.tmbeans.vo.trade.ReceivedOrderInfo;
import com.modcreater.tmbeans.vo.trade.ReceivedUserIdTradeId;
import com.modcreater.tmbeans.vo.trade.ReceivedVerifyInfo;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-27
 * Time: 14:04
 */
public interface OrderService {

    /**
     * 生成新的订单
     * @param receivedOrderInfo
     * @param token
     * @return
     */
    Dto createNewOrder(ReceivedOrderInfo receivedOrderInfo, String token);

    /**
     * 通过订单号查询订单
     * @param tradeId
     * @return
     */
    UserOrders getUserOrderById(String tradeId);

    /**
     * 修改订单
     * @param userOrders
     * @return
     */
    int updateOrderStatusToPrepaid(UserOrders userOrders);

    /**
     * 支付信息校验
     * @param receivedVerifyInfo
     * @param token
     * @return
     */
    Dto payInfoVerify(ReceivedVerifyInfo receivedVerifyInfo, String token);
}
