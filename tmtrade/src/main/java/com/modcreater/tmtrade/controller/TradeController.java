package com.modcreater.tmtrade.controller;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.trade.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmtrade.config.annotation.Safety;
import com.modcreater.tmtrade.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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

    @Safety
    @PostMapping(value = "appalipay")
    @ApiOperation("支付宝通道")
    public Dto aliPayOrderSubmitted(@RequestBody ReceivedOrderInfo receivedOrderInfo, HttpServletRequest httpServletRequest) throws Exception{
        return orderService.alipay(receivedOrderInfo ,httpServletRequest.getHeader("token"));
    }

    @PostMapping(value = "alipay/notify_in_ali_with_mct")
    @ApiOperation("支付宝异步回调")
    public String aliPayNotify(HttpServletRequest request){
        return orderService.alipayNotify(request);
    }

    @PostMapping(value = "appwxpay")
    @ApiOperation("微信支付通道")
    public Dto wxPayOrderSubmitted(@RequestBody ReceivedOrderInfo receivedOrderInfo , HttpServletRequest httpServletRequest) throws Exception{
        return orderService.wxPayOrderSubmitted(receivedOrderInfo,httpServletRequest.getHeader("token"));
    }

    @PostMapping(value = "wxpay/notify_in_wx_with_mct")
    @ApiOperation("微信支付回调")
    public String wxPayNotify(HttpServletRequest request){
        return orderService.wxPayNotify(request);
    }

    @Safety
    @PostMapping(value = "payinfoverify")
    @ApiOperation("C/S订单同步")
    public Dto payInfoVerify(@RequestBody ReceivedVerifyInfo receivedVerifyInfo, HttpServletRequest request){
        return orderService.payInfoVerify(receivedVerifyInfo,request.getHeader("token"));
    }

    @PostMapping(value = "isfriendserviceopened")
    @ApiOperation("判断用户是否开通了好友服务")
    public Dto isFriendServiceOpened(@RequestBody ReceivedId receivedId,HttpServletRequest request){
        return orderService.isFriendServiceOpened(receivedId,request.getHeader("token"));
    }

    @PostMapping(value = "userservice")
    @ApiOperation("查询用户服务的开通状态(外显)")
    public Dto searchUserService(@RequestBody ReceivedServiceIdUserId receivedServiceIdUserId, HttpServletRequest request){
        return orderService.searchUserService(receivedServiceIdUserId,request.getHeader("token"));
    }

    @PostMapping(value = "alluserservice")
    @ApiOperation("查询用户除好友外的所有服务的开通状态(外显)")
    public Dto searchAllUserService(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return orderService.searchAllUserService(receivedId,request.getHeader("token"));
    }

    @PostMapping(value = "searchorders")
    @ApiOperation("查询订单详情")
    public Dto searchOrders(@RequestBody ReceivedId receivedId ,HttpServletRequest request){
        return orderService.searchUserOrders(receivedId,request.getHeader("token"));
    }

    @PostMapping(value = "getserviceprice")
    @ApiOperation("查询商品价格")
    public Dto getServicePrice(@RequestBody ReceivedGoodsInfo receivedGoodsInfo , HttpServletRequest request){
        return orderService.getServicePrice(receivedGoodsInfo,request.getHeader("token"));
    }

    @PostMapping(value = "getalluserserviceforios")
    @ApiOperation("查询用户所有服务(IOS)")
    public Dto getAllUserServiceForIOS(@RequestBody ReceivedId receivedId ,HttpServletRequest request){
        return orderService.getAllUserServiceForIOS(receivedId,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "ordercancel")
    @ApiOperation("取消支付")
    public Dto orderCancel(@RequestBody ReceivedTradeId receivedTradeId, HttpServletRequest request){
        return orderService.orderCancel(receivedTradeId,request.getHeader("token"));
    }
}
