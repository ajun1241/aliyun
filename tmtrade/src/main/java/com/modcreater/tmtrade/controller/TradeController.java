package com.modcreater.tmtrade.controller;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.trade.ReceivedOrderInfo;
import com.modcreater.tmbeans.vo.trade.ReceivedServiceIdUserId;
import com.modcreater.tmbeans.vo.trade.ReceivedVerifyInfo;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmtrade.service.OrderService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-24
 * Time: 17:42
 */
@RestController
@RequestMapping(value = "/pay/")
public class TradeController {

    @Resource
    private OrderService orderService;

    /**
     * 支付宝支付
     * @param receivedOrderInfo
     * @param httpServletRequest
     * @return
     * @throws Exception
     */
    @PostMapping(value = "appalipay")
    public Dto aliPayOrderSubmitted(@RequestBody ReceivedOrderInfo receivedOrderInfo, HttpServletRequest httpServletRequest) throws Exception{
        return orderService.alipay(receivedOrderInfo ,httpServletRequest.getHeader("token"));
    }

    /**
     * 支付宝异步回调
     * @param request
     * @return
     */
    @PostMapping(value = "alipay/notify_url")
    public String aliPayNotify(HttpServletRequest request){
        return orderService.alipayNotify(request);
    }

    /**
     * 微信支付
     * @param receivedOrderInfo
     * @param httpServletRequest
     * @return
     * @throws Exception
     */
    @PostMapping(value = "appwxpay")
    public Dto wxPayOrderSubmitted(@RequestBody ReceivedOrderInfo receivedOrderInfo , HttpServletRequest httpServletRequest) throws Exception{
        return orderService.wxPayOrderSubmitted(receivedOrderInfo,httpServletRequest.getHeader("token"));
    }

    /**
     * 微信支付回调
     * @param request
     * @return
     */
    @PostMapping(value = "wxpay/notify_url")
    public String wxPayNotify(HttpServletRequest request){
        return orderService.wxPayNotify(request);
    }

    /**
     * 订单最后验证
     * @param receivedVerifyInfo
     * @param request
     * @return
     */
    @PostMapping(value = "payinfoverify")
    public Dto payInfoVerify(@RequestBody ReceivedVerifyInfo receivedVerifyInfo, HttpServletRequest request){
        return orderService.payInfoVerify(receivedVerifyInfo,request.getHeader("token"));
    }

    /**
     * 判断用户是否开通了好友服务
     * @param receivedId
     * @param request
     * @return
     */
    @PostMapping(value = "isfriendserviceopened")
    public Dto isFriendServiceOpened(@RequestBody ReceivedId receivedId,HttpServletRequest request){
        return orderService.isFriendServiceOpened(receivedId,request.getHeader("token"));
    }

    /**
     * 查询用户服务开通状态
     * @param receivedServiceIdUserId
     * @param request
     * @return
     */
    @PostMapping(value = "userservice")
    public Dto searchUserService(@RequestBody ReceivedServiceIdUserId receivedServiceIdUserId, HttpServletRequest request){
        return orderService.searchUserService(receivedServiceIdUserId,request.getHeader("token"));
    }

    /**
     * 查询订单
     * @param receivedId
     * @param request
     * @return
     */
    @PostMapping(value = "searchorders")
    public Dto searchOrders(@RequestBody ReceivedId receivedId ,HttpServletRequest request){
        return orderService.searchUserOrders(receivedId,request.getHeader("token"));
    }
}
