package com.modcreater.tmtrade.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayObject;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.UserOrders;
import com.modcreater.tmbeans.vo.trade.ReceivedOrderInfo;
import com.modcreater.tmbeans.vo.trade.ReceivedUserIdTradeId;
import com.modcreater.tmbeans.vo.trade.ReceivedVerifyInfo;
import com.modcreater.tmdao.mapper.OrderMapper;
import com.modcreater.tmtrade.config.AliPayConfig;
import com.modcreater.tmtrade.service.OrderService;
import com.modcreater.tmutils.AliPayUtil;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmutils.RandomNumber;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.alipay.api.AlipayConstants.*;
import static com.modcreater.tmtrade.config.AliPayConfig.*;
import static com.modcreater.tmtrade.config.AliPayConfig.APP_ID;
import static com.modcreater.tmtrade.config.AliPayConfig.CHARSET;
import static com.modcreater.tmtrade.config.AliPayConfig.NOTIFY_URL;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-24
 * Time: 17:42
 */
@RestController
public class AliPayController {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderService orderService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    AlipayClient alipayClient = new DefaultAlipayClient(url, APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY,sign_type);
    AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();

    @PostMapping(value = "/pay/appalipay")
    public Dto aliPayOrderSubmitted(@RequestBody ReceivedOrderInfo receivedOrderInfo, HttpServletRequest httpServletRequest) throws Exception{
        String token = httpServletRequest.getHeader("token");
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("操作失败,token未获取到",21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedOrderInfo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        UserOrders userOrder = new UserOrders();
        userOrder.setUserId(receivedOrderInfo.getUserId());
        userOrder.setServiceId(receivedOrderInfo.getServiceId());
        userOrder.setOrderTitle(receivedOrderInfo.getOrderTitle());
        userOrder.setId(""+System.currentTimeMillis()/1000+ RandomNumber.getFour());
        double amount = orderMapper.getPaymentAmount(receivedOrderInfo.getServiceId(),receivedOrderInfo.getOrderType());
        if (amount != 0 && receivedOrderInfo.getPaymentAmount() - (amount) != 0){
            return DtoUtil.getFalseDto("订单金额错误",60001);
        }
        userOrder.setPaymentAmount(amount);
        userOrder.setCreateDate(System.currentTimeMillis()/1000);
        userOrder.setRemark(receivedOrderInfo.getUserRemark());
        if (orderMapper.addNewOrder(userOrder) == 0){
            return DtoUtil.getFalseDto("订单生成失败",60002);
        }

        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            if (response.isSuccess()){
                Map<String ,String> map = AliPayUtil.buildOrderParamMap(APP_ID,userOrder.getId(),userOrder.getOrderTitle(),"您花费"+userOrder.getPaymentAmount()+"元",userOrder.getPaymentAmount().toString());
                String orderInfo = AliPayUtil.getSign(map,APP_PRIVATE_KEY);
                return DtoUtil.getSuccesWithDataDto("支付宝订单创建成功",orderInfo,100000);
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return DtoUtil.getFalseDto("支付宝订单创建异常",70001);
    }


    @PostMapping(value = "/notify_url")
    public String notify(HttpServletRequest request, HttpServletResponse response){
        System.out.println("调用了异步接口");
        return orderService.alipayNotify(request);
    }

    @PostMapping(value = "/pay/payinfoverify")
    public Dto payInfoVerify(@RequestBody ReceivedVerifyInfo receivedVerifyInfo, HttpServletRequest request){
        return orderService.payInfoVerify(receivedVerifyInfo,request.getHeader("token"));
    }

}
