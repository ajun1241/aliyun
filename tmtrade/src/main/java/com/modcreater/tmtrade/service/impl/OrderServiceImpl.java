package com.modcreater.tmtrade.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.UserOrders;
import com.modcreater.tmbeans.vo.trade.ReceivedOrderInfo;
import com.modcreater.tmbeans.vo.trade.ReceivedVerifyInfo;
import com.modcreater.tmdao.mapper.OrderMapper;
import com.modcreater.tmdao.mapper.UserServiceMapper;
import com.modcreater.tmtrade.config.WxPayConfig;
import com.modcreater.tmtrade.service.OrderService;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmutils.RandomNumber;
import com.modcreater.tmutils.wxconfig.WxConfig;
import com.modcreater.tmutils.wxconfig.WxMD5Util;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.modcreater.tmtrade.config.AliPayConfig.*;
import static com.modcreater.tmtrade.config.WxPayConfig.MCH_ID;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-27
 * Time: 14:04
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private UserServiceMapper userServiceMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Dto createNewOrder(ReceivedOrderInfo receivedOrderInfo, String token) {
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("操作失败,token未获取到", 21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedOrderInfo.getUserId()))) {
            return DtoUtil.getFalseDto("token过期请先登录", 21014);
        }
        /*if (receivedOrderInfo.getServiceType().equals("perpetual")){
            if (!receivedOrderInfo.getServiceId().equals("1")){
                return DtoUtil.getFalseDto("服务类型错误",60015);
            }
        }
        if (receivedOrderInfo.getServiceType().equals("month") || receivedOrderInfo.getServiceType().equals("year")){
            if (receivedOrderInfo.getServiceId().equals("1")){
                return DtoUtil.getFalseDto("服务类型错误",600015);
            }
        }
        String remainingTime = userServiceMapper.getTimeRemaining(receivedOrderInfo);
        if (receivedOrderInfo.getServiceType().equals("time")){
            if (receivedOrderInfo.getServiceId().equals("1") || receivedOrderInfo.getServiceId().equals("3")){
                return DtoUtil.getFalseDto("服务类型错误",600015);
            }
            if (StringUtils.hasText(remainingTime) && Long.valueOf(remainingTime) > System.currentTimeMillis() / 1000){
                return DtoUtil.getFalseDto("当前年/月卡尚未用完",60014);
            }
        }
        */
        UserOrders userOrders = new UserOrders();
        userOrders.setUserId(receivedOrderInfo.getUserId());
        userOrders.setServiceId(receivedOrderInfo.getServiceId());
        userOrders.setOrderTitle(receivedOrderInfo.getOrderTitle());
        userOrders.setServiceType(receivedOrderInfo.getServiceType());
        double amount = orderMapper.getPaymentAmount(receivedOrderInfo.getServiceId(), receivedOrderInfo.getServiceType());
        if (amount != 0 && receivedOrderInfo.getPaymentAmount() - (amount) != 0) {
            return DtoUtil.getFalseDto("订单金额错误", 60001);
        }
        userOrders.setPaymentAmount(amount);
        userOrders.setCreateDate(System.currentTimeMillis() / 1000);
        userOrders.setRemark(receivedOrderInfo.getUserRemark());
        if (orderMapper.addNewOrder(userOrders) > 0) {
            return DtoUtil.getSuccessDto("订单生成成功,请及时支付", 100000);
        }
        return DtoUtil.getFalseDto("订单生成失败", 60002);
    }

    @Override
    public UserOrders getUserOrderById(String tradeId) {
        if (StringUtils.hasText(tradeId)) {
            return orderMapper.getUserOrder(tradeId);
        }
        return null;
    }

    @Override
    public int updateOrderStatusToPrepaid(UserOrders userOrders) {
        if (!ObjectUtils.isEmpty(userOrders)) {
            return orderMapper.updateUserOrder(userOrders);
        }
        return 0;
    }

    @Override
    public Dto payInfoVerify(ReceivedVerifyInfo receivedVerifyInfo, String token) {
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("操作失败,token未获取到", 21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedVerifyInfo.getUserId()))) {
            return DtoUtil.getFalseDto("token过期请先登录", 21014);
        }
        if (!StringUtils.hasText(receivedVerifyInfo.getId())) {
            return DtoUtil.getFalseDto("未获取到订单号", 60004);
        }
        if (receivedVerifyInfo.getPaymentAmount() == 0) {
            return DtoUtil.getFalseDto("金额未获取到", 60005);
        }
        if (!StringUtils.hasText(receivedVerifyInfo.getOutTradeNo())) {
            return DtoUtil.getFalseDto("支付平台流水号未获取到", 60006);
        }
        if (!StringUtils.hasText(receivedVerifyInfo.getAppId())) {
            return DtoUtil.getFalseDto("APP_ID未获取到", 60007);
        }
        if (!StringUtils.hasText(receivedVerifyInfo.getSellerId())) {
            return DtoUtil.getFalseDto("SELLER_ID未获取到", 60008);
        }
        UserOrders userOrders = orderMapper.getUserOrder(receivedVerifyInfo.getId());
        if (userOrders.getOrderStatus().equals("1")) {
            if (!receivedVerifyInfo.getPaymentAmount().equals(userOrders.getPaymentAmount())) {
                return DtoUtil.getFalseDto("金额不一致", 60009);
            }
            if (!receivedVerifyInfo.getOutTradeNo().equals(userOrders.getOutTradeNo())) {
                return DtoUtil.getFalseDto("支付平台流水号不一致", 60010);
            }
            if (!receivedVerifyInfo.getAppId().equals(APP_ID)) {
                return DtoUtil.getFalseDto("金额有误", 60011);
            }
            if (!receivedVerifyInfo.getSellerId().equals(PID)) {
                return DtoUtil.getFalseDto("金额有误", 60012);
            }
            /*if (userServiceMapper.updateUserServiceStatus(userOrders.getId(),userOrders.getServiceId(),userOrders.getServiceType()) > 0) {

            }*/
            return DtoUtil.getSuccessDto("订单支付成功", 100000);
        }
        return DtoUtil.getFalseDto("订单支付失败", 60003);
    }

    @Override
    public String alipayNotify(HttpServletRequest request) {
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        //1.从支付宝回调的request域中取值
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        //2.封装必须参数
        // 商户订单号
        String outTradeNo = request.getParameter("out_trade_no");
        //交易状态
        String tradeStatus = request.getParameter("trade_status");
        //支付宝流水号
        String tradeNo = request.getParameter("trade_no");
        //卖家ID
        String sellerId = request.getParameter("seller_id");
        if (!PID.equals(sellerId)) {
            System.out.println("sellerId验证失败");
            return "fail";
        }
        //3.签名验证(对支付宝返回的数据验证，确定是支付宝返回的)
        boolean signVerified = false;
        try {
            //3.1调用SDK验证签名
            signVerified = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, CHARSET, sign_type);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //4.对验签进行处理
        //验签通过
        if (signVerified) {
            //只处理支付成功的订单: 修改交易表状态,支付成功
            //支付完成
            if (tradeStatus.equals("TRADE_SUCCESS")) {
                UserOrders userOrders = getUserOrderById(outTradeNo);
                userOrders.setOrderStatus("1");
                userOrders.setPayTime(String.valueOf(System.currentTimeMillis() / 1000));
                userOrders.setPayChannel("AliPay");
                userOrders.setOutTradeNo(tradeNo);
                //更新交易表中状态
                int returnResult = updateOrderStatusToPrepaid(userOrders);
                if (returnResult > 0) {
                    return "success";
                } else {
                    System.out.println("订单状态修改失败");
                    return "fail";
                }
            } else {
                System.out.println("支付失败");
                return "fail";
            }
        } else {
            //验签不通过
            System.out.println("验签失败");
            return "fail";
        }
    }

    @Override
    public Dto alipay(ReceivedOrderInfo receivedOrderInfo, String token) {
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("操作失败,token未获取到", 21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedOrderInfo.getUserId()))) {
            return DtoUtil.getFalseDto("token过期请先登录", 21014);
        }
        AlipayClient alipayClient = new DefaultAlipayClient(url, APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, sign_type);
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        UserOrders userOrder = new UserOrders();
        userOrder.setUserId(receivedOrderInfo.getUserId());
        userOrder.setServiceId(receivedOrderInfo.getServiceId());
        userOrder.setOrderTitle(receivedOrderInfo.getOrderTitle());
        userOrder.setId("" + System.currentTimeMillis() / 1000 + RandomNumber.getFour());
        double amount = orderMapper.getPaymentAmount(receivedOrderInfo.getServiceId(), receivedOrderInfo.getServiceType());
        if (amount != 0 && receivedOrderInfo.getPaymentAmount() - (amount) != 0) {
            return DtoUtil.getFalseDto("订单金额错误", 60001);
        }
        userOrder.setPaymentAmount(amount);
        userOrder.setCreateDate(System.currentTimeMillis() / 1000);
        userOrder.setRemark(receivedOrderInfo.getUserRemark());
        if (orderMapper.addNewOrder(userOrder) == 0) {
            return DtoUtil.getFalseDto("订单生成失败", 60002);
        }
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setOutTradeNo(userOrder.getId());
        model.setSubject("手机端" + userOrder.getOrderTitle() + "移动支付");
        model.setTotalAmount(userOrder.getPaymentAmount().toString());
        model.setBody("您花费" + userOrder.getPaymentAmount() + "元");
        model.setTimeoutExpress("30m");
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setNotifyUrl(NOTIFY_URL);
        System.out.println(model.toString());
        request.setBizModel(model);
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            if (response.isSuccess()) {
                /*Map<String ,String> map = AliPayUtil.buildOrderParamMap(APP_ID,userOrders.getId(),userOrders.getOrderTitle(),"您花费"+userOrders.getPaymentAmount()+"元",userOrders.getPaymentAmount().toString());
                String orderInfo = AliPayUtil.getSign(map,APP_PRIVATE_KEY);*/
                return DtoUtil.getSuccesWithDataDto("支付宝订单创建成功", response.getBody(), 100000);
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return DtoUtil.getFalseDto("支付宝订单创建异常", 70001);
    }

    @Override
    public Dto wxPayOrderSubmitted(ReceivedOrderInfo receivedOrderInfo, String token) throws Exception {
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("操作失败,token未获取到", 21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedOrderInfo.getUserId()))) {
            return DtoUtil.getFalseDto("token过期请先登录", 21014);
        }
        UserOrders userOrder = new UserOrders();
        userOrder.setUserId(receivedOrderInfo.getUserId());
        userOrder.setServiceId(receivedOrderInfo.getServiceId());
        userOrder.setOrderTitle(receivedOrderInfo.getOrderTitle());
        userOrder.setId("" + System.currentTimeMillis() / 1000 + RandomNumber.getFour());
        double amount = orderMapper.getPaymentAmount(receivedOrderInfo.getServiceId(), receivedOrderInfo.getServiceType());
        if (amount != 0 && receivedOrderInfo.getPaymentAmount() - (amount) != 0) {
            return DtoUtil.getFalseDto("订单金额错误", 60001);
        }
        userOrder.setPaymentAmount(amount);
        userOrder.setCreateDate(System.currentTimeMillis() / 1000);
        userOrder.setRemark(receivedOrderInfo.getUserRemark());
        if (orderMapper.addNewOrder(userOrder) == 0) {
            return DtoUtil.getFalseDto("订单生成失败", 60002);
        }
        /*以上为订单生成*/
        /*------------------------------------------------------------*/

        WxMD5Util md5Util = new WxMD5Util();
        Map<String, String> returnMap = new HashMap<>();
        WxConfig config = new WxConfig();
        WXPay wxpay = new WXPay(config);
        Map<String, String> data = new HashMap<>();

        /*以上为创建所需对象*/
        /*------------------------------------------------------------*/
        data.put("appid", WxPayConfig.APP_ID);
        data.put("mch_id", WxPayConfig.MCH_ID);
        data.put("nonce_str", WXPayUtil.generateNonceStr());
        data.put("body", "您花费" + userOrder.getPaymentAmount() + "元");
        data.put("out_trade_no", userOrder.getId());
        data.put("total_fee", String.valueOf(Math.round(userOrder.getPaymentAmount() * 100)));
        System.out.println(String.valueOf(Math.round(userOrder.getPaymentAmount() * 100)));
        data.put("spbill_create_ip", WxPayConfig.SPBILL_CREATE_IP);
        data.put("notify_url", WxPayConfig.NOTIFY_URL);
        data.put("trade_type", WxPayConfig.TRADE_TYPE);
        String sign = md5Util.getSign(data);
        data.put("sign", sign);
        /*以上为为调用微信支付接口设置参数*/
        /*------------------------------------------------------------*/
        try {
            //使用官方API请求预付订单
            Map<String, String> response = wxpay.unifiedOrder(data);
            System.out.println(response.toString());
            String returnCode = response.get("return_code");
            //若返回码为SUCCESS，则会返回一个result_code,再对该result_code进行判断
            if (returnCode.equals("SUCCESS")) {
                String resultCode = response.get("result_code");
                returnMap.put("appid", response.get("appid"));
                returnMap.put("mch_id", response.get("mch_id"));
                returnMap.put("nonce_str", response.get("nonce_str"));
                returnMap.put("sign", response.get("sign"));
                //resultCode 为SUCCESS，才会返回prepay_id和trade_type
                if ("SUCCESS".equals(resultCode)) {
                    //获取预支付交易回话标志
                    returnMap.put("trade_type", response.get("trade_type"));
                    returnMap.put("prepay_id", response.get("prepay_id"));
                    /*以上为从微信端获取的返回数据*/
                    /*------------------------------------------------------------*/
                    Map<String, String> appResult = new HashMap<>();
                    appResult.put("appid", returnMap.get("appid"));
                    appResult.put("partnerid", returnMap.get("mch_id"));
                    appResult.put("prepayid", returnMap.get("prepay_id"));
                    appResult.put("package", "Sign=WXPay");
                    appResult.put("noncestr", returnMap.get("nonce_str"));
                    //单位为秒
                    appResult.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
                    //这里不要使用请求预支付订单时返回的签名
                    appResult.put("sign", md5Util.getSign(appResult));
                    /*以上为返回给APP数据赋值*/
                    /*------------------------------------------------------------*/
                    return DtoUtil.getSuccesWithDataDto("获取预付单成功", appResult, 100000);
                } else {
                    //此时返回没有预付订单的数据
                    return DtoUtil.getFalseDto("没有预付订单的数据from resultCode=FAIL", 60013);
                }
            } else {
                return DtoUtil.getFalseDto("没有预付订单的数据from returnCode=FAIL", 60013);
            }
        } catch (Exception e) {
            System.out.println(e);
            //系统等其他错误的时候
        }
        return DtoUtil.getFalseDto("没有预付订单的数据from未进入微信API调用", 60013);
    }
}
