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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

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

    AlipayClient alipayClient = new DefaultAlipayClient(url, APP_ID, APP_PRIVATE_KEY, "json", "GBK", ALIPAY_PUBLIC_KEY,sign_type);
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

        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setOutTradeNo(userOrder.getId());
        model.setSubject("手机端"+userOrder.getOrderTitle()+"移动支付");
        model.setTotalAmount(userOrder.getPaymentAmount().toString());
        model.setBody("您花费"+userOrder.getPaymentAmount()+"元");
        model.setTimeoutExpress("30m");
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setNotifyUrl(NOTIFY_URL);
        System.out.println(model.toString());
        request.setBizModel(model);
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            return DtoUtil.getSuccesWithDataDto("支付宝订单创建成功",response.getBody(),100000);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return DtoUtil.getFalseDto("支付宝订单创建异常",70001);
    }

    @PostMapping(value = "/pay/appalipay2")
    public Dto aliPayOrderSubmitted2(@RequestBody ReceivedOrderInfo receivedOrderInfo, HttpServletRequest httpServletRequest) throws Exception{
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

        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setOutTradeNo(userOrder.getId());
        model.setSubject("手机端"+userOrder.getOrderTitle()+"移动支付");
        model.setTotalAmount(userOrder.getPaymentAmount().toString());
        model.setBody("您花费"+userOrder.getPaymentAmount()+"元");
        model.setTimeoutExpress("30m");
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setNotifyUrl(NOTIFY_URL);
        System.out.println(model.toString());
        request.setBizModel(model);
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            String partner = PID;
            String notify_url = NOTIFY_URL;
            String out_trade_no = userOrder.getId();
            String payment_type = "1";
            String seller_id = SELLER_ID;
            String total_fee = userOrder.getPaymentAmount().toString();
            String body = "您花费"+userOrder.getPaymentAmount()+"元";
            String private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCrOPZAzmxeSSKSdzj5YwUOrSd/GPSReqPfs93isVE6IukwTE6IMMBRhUdeKsaEJFIxCMevjlGTrCnE5pOmz/Ol6rHzwfUlguWywRb/SZkep5OX7Bu5P70H9HqW83uebclMF7P33vImTe8D13RoprSdDXy2VFNMGkts/kyGtpo37uJcpCdZJwNza2j4Tp5NyhVwhecGgF5dnHkkTnmxF5hnyfR9y6wo7PuM5WSGEqj3SMs2y7LzVYqCK6z84c/2sEd4YQS405SYTHSIeQGY4Mhc3u1QswAAXjFdL+9OxkALUBVzUuvryk105nUQT/2sQsaxD0BYDsy8w5bvAZn/s7P9AgMBAAECggEAPEHXqAb9bFEgsZrzmhCW/wtBEyGdMHWQpnGObPEqPON8XVr9aEB/7jCRZTp4kpiVtFv6qnXTj7zPFlRvMg2NGZ728WoY9BKU88uwZDdqg7dF4fld6FveEqafXfBiWOcaKIVO+LSlU4Wp0BhLB2ljtsHAWgpjJi06zijQlbg9neEHPaEnojxiJu00q0a5boy1l0pCwuj2xJGUPwnTMpye3VhOS88MVxUt5HnG6aa+7Y1aZrioJynDsAXR07yFKKe6H3xc9Ymk3DP4u2AH3YYu7uy+F0pijfDp+xe3ucCTBJAB8dJ9FyP8I4j+pQQIy+gTD+mUTy0cmSwAmCDtwYH/AQKBgQDxYAznY3KCDuppxa2A5bvadhioePeJPqZZ/m+fV/XAsT5w1UoSz0SGSUZan6bX1VpQjQUUs8Am04d5xjJvmdzaxo72HQLQasbHIW0CQn/uWMLfG2hQwl6VS/a3xIgfo4jixPc5hnqpg3RG7NN+2GlUqMKMASQ0PvnchVe3NU8eCQKBgQC1mMd/JSqlDVAecuFZ6DWcKnecWm9rCWFKNRN7/0WzMiiku832Vz5hRD8Z2GrVb5S03k8zErhX8PShCZhHh+07xssU5GCtvHmik507es53N7+QEpErHAotYeuqmpPwk26vPftGqupgSR6v+yScfHbN2RjjqNwUeF1ggUQ8zZejVQKBgBYN1J//siK4AisP6L5yPyvCxpX+F3/uvXxThxdkDgxBJZdhpZb6YSRz5X26QKBP2iSp/eDaW6Awi5xQw6L2x/slUUKje/JXDzp6j185fD7m1UdVVb6rQ8EYY75+soFRi9xMatpSszpxzFn+oYRheZ5GnR/1qr0rU+EWPeF7eXMhAoGAXMoPDdGg46tUx+otabI0SnKMoZC4I7osQy2xogdCxxiXGe14hSn5Dtw/XeZuWFbmZjC9yTwEg7L0XKC5dbtnpcajGC7Fk6Cdikvg6HaMrvAGawiBssRmEuBY+dh4RVIgGV0TWv2UOtTB4Gv0Ph1+2xnWFOXAZplGJTyH8IWguO0CgYB83pByE7bEROLhuNc3DqIXD3RIjnhr/BKlM1ePQCsBj8MQL8qsgwDqYRE8SSUE30UMMQC+AdnJbhJ0OHSi2VHHAkd3kkXfTGryuWBsAckdHxXWQUAZOkTE3SlLc0kGm/mBUmXhbMEKk8yY3K9Tex0WLO3WXQWPY/00vGjIh1B8aw==";
            String timeout_express = "30m";
            Map<String,Object> map = new HashMap<>();
            map.put("partner",partner);
            map.put("notify_url",notify_url);
            map.put("out_trade_no",out_trade_no);
            map.put("payment_type",payment_type);
            map.put("seller_id",seller_id);
            map.put("total_fee",total_fee);
            map.put("body",body);
            map.put("private_key",private_key);
            map.put("timeout_express",timeout_express);
            return DtoUtil.getSuccesWithDataDto("支付宝订单创建成功",map,100000);
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
