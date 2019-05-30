package com.modcreater.tmtrade.controller;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.trade.ReceivedOrderInfo;
import com.modcreater.tmbeans.vo.trade.ReceivedVerifyInfo;
import com.modcreater.tmtrade.service.OrderService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @PostMapping(value = "appalipay")
    public Dto aliPayOrderSubmitted(@RequestBody ReceivedOrderInfo receivedOrderInfo, HttpServletRequest httpServletRequest) throws Exception{
        return orderService.alipay(receivedOrderInfo ,httpServletRequest.getHeader("token"));
    }

    @PostMapping(value = "alipay/notify_url")
    public String notify(HttpServletRequest request, HttpServletResponse response){
        System.out.println("调用了异步接口");
        return orderService.alipayNotify(request);
    }

    @PostMapping(value = "appwxpay")
    public Dto wxPayOrderSubmitted(@RequestBody ReceivedOrderInfo receivedOrderInfo , HttpServletRequest httpServletRequest) throws Exception{
        return orderService.wxPayOrderSubmitted(receivedOrderInfo,httpServletRequest.getHeader("token"));
    }

    @PostMapping(value = "payinfoverify")
    public Dto payInfoVerify(@RequestBody ReceivedVerifyInfo receivedVerifyInfo, HttpServletRequest request){
        return orderService.payInfoVerify(receivedVerifyInfo,request.getHeader("token"));
    }

}
