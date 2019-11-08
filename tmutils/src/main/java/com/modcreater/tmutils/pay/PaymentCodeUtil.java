package com.modcreater.tmutils.pay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeCancelRequest;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmutils.DateUtil;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmutils.RongCloudMethodUtil;
import com.modcreater.tmutils.payconfig.WxPayConfig;
import com.modcreater.tmutils.payconfig.wxconfig.WxConfig;
import com.modcreater.tmutils.payconfig.wxconfig.WxMD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.modcreater.tmutils.payconfig.AliPayConfig.*;


/**
 * Description:
 *      支付宝和微信付款码支付
 * @Author: AJun
 * @Date: 2019/11/5 13:57
 */
public class PaymentCodeUtil {
    private static final String PAY_SUCCESS = "SUCCESS";
    private static final String PAY_USERPAYING = "USERPAYING";
    private static Logger logger = LoggerFactory.getLogger(PaymentCodeUtil.class);

    /**
     * 微信付款码支付
     * @throws Exception
     */
    public static Dto wxPaymentCodeToPay(String authCode, String tradeNo, Double paymentAmount,String storeName) throws Exception {
        WxMD5Util md5Util = new WxMD5Util();
        Map<String, String> returnMap = new HashMap<>();
        WxConfig config = new WxConfig();
        WXPay wxpay = new WXPay(config);
        Map<String, String> data = new HashMap<>(12);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String sTime = simpleDateFormat.format(calendar.getTime());
        calendar.set(Calendar.SECOND,300);
        String exTime = simpleDateFormat.format(calendar.getTime());
        data.put("appid", WxPayConfig.APP_ID);
        data.put("mch_id", WxPayConfig.MCH_ID);
        data.put("nonce_str", WXPayUtil.generateNonceStr());
        data.put("body", storeName+"-商铺");
        data.put("out_trade_no", tradeNo);
        data.put("total_fee", String.valueOf(Math.round(paymentAmount * 100)));
        data.put("spbill_create_ip", WxPayConfig.SPBILL_CREATE_IP);
        data.put("auth_code",authCode);
        data.put("time_start",sTime);
        data.put("time_expire",exTime);
        String sign = md5Util.getSign(data);
        data.put("sign", sign);
        try {
            //使用官方API请求预付订单
            Map<String, String> response = wxpay.microPay(data);
            logger.info(response.toString());
            String returnCode = response.get("return_code");
            String resultCode = response.get("result_code");
            String errCode = response.get("err_code");
            String errCodeDes = response.get("err_code_des");
            logger.info(returnCode);
            //若返回码为SUCCESS，则会返回一个result_code,再对该result_code进行判断
            if(PAY_SUCCESS.equals(returnCode) && PAY_SUCCESS.equals(resultCode)){
                logger.info("微信免密支付成功！");
                return DtoUtil.getSuccessDto("微信免密支付成功",100000);
            } else if (PAY_USERPAYING.equals(errCode)){
                Map<String, String> data1=null;
                //设置每隔5秒查询一次支付状态，30秒后超时
                for(int i = 0; i < 6; i++){
                    Thread.sleep(5000);
                    data1= new HashMap<>(6);
                    data1.put("appid", WxPayConfig.APP_ID);
                    data1.put("mch_id", WxPayConfig.MCH_ID);
                    data1.put("out_trade_no", tradeNo);
                    data1.put("nonce_str", WXPayUtil.generateNonceStr());
                    data1.put("sign",md5Util.getSign(data1));
                    //调用微信的查询接口
                    Map<String, String> orderQuery = wxpay.orderQuery(data1);
                    String trade_state=orderQuery.get("trade_state");
                    if(PAY_SUCCESS.equals(trade_state)){
                        logger.info("微信加密支付成功！");
                        return DtoUtil.getSuccessDto("微信加密支付成功",100000);
                    }
                    logger.info("正在支付" + orderQuery);
                }
            }
            logger.info(errCodeDes);
        } catch (Exception e) {
            //系统等其他错误的时候
            logger.error(e.getMessage(),e);
        }
        //支付超时或者失败时调用撤销订单
        Map<String,String> data2=new HashMap<>(6);
        data2.put("appid", WxPayConfig.APP_ID);
        data2.put("mch_id", WxPayConfig.MCH_ID);
        data2.put("out_trade_no", tradeNo);
        data2.put("nonce_str", WXPayUtil.generateNonceStr());
        data2.put("sign",md5Util.getSign(data2));
        Map<String,String> reverse=wxpay.reverse(data2);
        String return_code=reverse.get("return_code");
        String result_code=reverse.get("result_code");
        String err_code_des=reverse.get("err_code_des");
        if (PAY_SUCCESS.equals(return_code) && PAY_SUCCESS.equals(result_code)){
            logger.info("订单已撤销："+reverse);
            return DtoUtil.getFalseDto("支付超时，请重新下单",60015);
        }
        logger.info(err_code_des);
        return DtoUtil.getFalseDto("撤销订单失败："+err_code_des,60016);
    }

    /**
     * 支付宝付款码支付
     * @param authCode
     * @param tradeNo
     * @param paymentAmount
     * @param storeName
     * @param storeId
     * @return
     */
    public static Dto aliPaymentCodeToPay(String authCode,String tradeNo,Double paymentAmount,String storeName,String storeId){
            //获得初始化的AlipayClient
            AlipayClient alipayClient = new DefaultAlipayClient(url, APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, sign_type);
            //创建API对应的request类
            AlipayTradePayRequest request = new AlipayTradePayRequest();
            Map<String,String> requestMap=new HashMap<>(10);
            requestMap.put("out_trade_no",tradeNo);
            requestMap.put("scene","bar_code");
            requestMap.put("auth_code",authCode);
            requestMap.put("subject",storeName+"-商铺");
            requestMap.put("store_id","ZX"+storeId);
            requestMap.put("timeout_express","3m");
            requestMap.put("total_amount",paymentAmount.toString());
            requestMap.put("product_code","FACE_TO_FACE_PAYMENT");
            request.setBizContent(JSON.toJSONString(requestMap));
        try {
            //通过alipayClient调用API，获得对应的response类
            AlipayTradePayResponse response = alipayClient.execute(request);
            String code=response.getCode();;
            if ("10000".equals(code)){
                logger.info("支付成功");
                return DtoUtil.getSuccessDto("支付成功",100000);
            }else if ("40004".equals(code)){
                logger.info(response.getSubMsg());
            }else if ("20000".equals(code)){
                logger.info("支付异常："+response.getSubMsg());
                AlipayTradeQueryRequest request1 = new AlipayTradeQueryRequest();
                Map<String,String> map=new HashMap<>();
                map.put("out_trade_no",tradeNo);
                request1.setBizContent(JSON.toJSONString(map));
                AlipayTradeQueryResponse queryResponse=alipayClient.execute(request1);
                if ("10000".equals(queryResponse.getCode())){
                    return DtoUtil.getSuccessDto("支付成功",100000);
                }
                logger.info(queryResponse.getSubMsg());
            }else if ("10003".equals(code)){
                Map<String,String> map1=null;
                AlipayTradeQueryRequest request1 = new AlipayTradeQueryRequest();
                AlipayTradeQueryResponse queryResponse=null;
                //设置每隔5秒查询一次支付状态，30秒后超时
                for (int i = 0; i <6 ; i++) {
                    logger.info("正在支付");
                    Thread.sleep(5000);
                    map1=new HashMap<>(2);
                    map1.put("out_trade_no",tradeNo);
                    request1.setBizContent(JSON.toJSONString(map1));
                    queryResponse = alipayClient.execute(request1);
                    System.out.println(queryResponse);
                    if ("10000".equals(queryResponse.getCode())){
                        return DtoUtil.getSuccessDto("支付成功",100000);
                    }
                }
                logger.info("支付超时："+queryResponse.getSubMsg());
                //撤销订单
                AlipayTradeCancelRequest request2 = new AlipayTradeCancelRequest();
                map1=new HashMap<>(2);
                map1.put("out_trade_no",tradeNo);
                request2.setBizContent(JSON.toJSONString(map1));
                AlipayTradeCancelResponse cancelResponse = alipayClient.execute(request2);
                if ("10000".equals(cancelResponse.getCode())){
                    return DtoUtil.getSuccessDto("订单已撤销",60012);
                }
                logger.info("订单撤销失败："+cancelResponse.getSubMsg());
            }
        } catch (Exception e) {
            logger.info(e.getMessage(),e);
        }
        return DtoUtil.getFalseDto("支付失败", 60011);
    }

}
