package com.modcreater.tmtrade.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.UserOrders;
import com.modcreater.tmbeans.vo.trade.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

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
     * @return
     */
    Dto createNewOrder(ReceivedOrderInfo receivedOrderInfo);

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
     * 支付宝支付信息校验
     * @param receivedVerifyInfo
     * @param token
     * @return
     */
    Dto payInfoVerify(ReceivedVerifyInfo receivedVerifyInfo, String token);

    /**
     * 支付宝异步通知
     * @param request
     * @return
     */
    String alipayNotify(HttpServletRequest request);

    /**
     * 支付宝支付
     * @param receivedOrderInfo
     * @param token
     * @return
     */
    Dto alipay(ReceivedOrderInfo receivedOrderInfo, String token);

    /**
     * 微信生成预付单
     * @param receivedOrderInfo
     * @param token
     * @return
     * @throws Exception
     */
    Dto wxPayOrderSubmitted(ReceivedOrderInfo receivedOrderInfo, String token) throws Exception;

    /**
     * 微信支付异步通知
     * @param request
     * @return
     */
    String wxPayNotify(HttpServletRequest request);

    /**
     * 效果未知
     * @param wxNotifyData
     * @return
     */
    String payBack(String wxNotifyData);

    /**
     * 判断该用户是否开通了好友服务
     * @param receivedId
     * @param token
     * @return
     */
    Dto isFriendServiceOpened(ReceivedId receivedId, String token);

    /**
     * 查询用户服务开通状态
     * @param receivedServiceIdUserId
     * @param token
     * @return
     */
    Dto searchUserService(ReceivedServiceIdUserId receivedServiceIdUserId, String token);

    /**
     * 查询用户订单
     * @param receivedId
     * @param token
     * @return
     */
    Dto searchUserOrders(ReceivedId receivedId, String token);

    /**
     * 查询商品价格
     * @param receivedGoodsInfo
     * @param token
     * @return
     */
    Dto getServicePrice(ReceivedGoodsInfo receivedGoodsInfo, String token);

    /**
     * 查询所有服务的开通状态
     * @param receivedId
     * @param token
     * @return
     */
    Dto searchAllUserService(ReceivedId receivedId, String token);

    /**
     * 获取用户所有服务开通状态(IOS)
     * @param receivedId
     * @param token
     * @return
     */
    Dto getAllUserServiceForIOS(ReceivedId receivedId, String token);
}
