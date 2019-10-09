package com.modcreater.tmutils.pay;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmutils.payconfig.WxPayConfig;
import com.modcreater.tmutils.payconfig.wxconfig.WxConfig;
import com.modcreater.tmutils.payconfig.wxconfig.WxMD5Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-10-09
 * Time: 11:15
 */
public class PayUtil {


    /**
     * 生成微信预付单
     * @param tradeNo 商家生成的订单号
     * @param paymentAmount 金额
     * @return 返回Mod
     * @throws Exception ""
     */
    public static Dto wxOrderMaker(String tradeNo ,Double paymentAmount) throws Exception{
        WxMD5Util md5Util = new WxMD5Util();
        Map<String, String> returnMap = new HashMap<>();
        WxConfig config = new WxConfig();
        WXPay wxpay = new WXPay(config);
        Map<String, String> data = new HashMap<>();

        /*以上为创建所需对象*/
        /*------------------------------------------------------------*/
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String sTime = simpleDateFormat.format(calendar.getTime());
        calendar.set(Calendar.SECOND,300);
        String exTime = simpleDateFormat.format(calendar.getTime());
        data.put("appid", WxPayConfig.APP_ID);
        data.put("mch_id", WxPayConfig.MCH_ID);
        data.put("nonce_str", WXPayUtil.generateNonceStr());
        data.put("body", "您花费" + paymentAmount + "元");
        data.put("out_trade_no", tradeNo);
        data.put("total_fee", String.valueOf(Math.round(paymentAmount * 100)));
        data.put("spbill_create_ip", WxPayConfig.SPBILL_CREATE_IP);
        data.put("notify_url", WxPayConfig.NOTIFY_URL);
        data.put("trade_type", WxPayConfig.TRADE_TYPE);
        data.put("time_start",sTime);
        data.put("time_expire",exTime);
        String sign = md5Util.getSign(data);
        data.put("sign", sign);
        /*以上为为调用微信支付接口设置参数*/
        /*------------------------------------------------------------*/
        try {
            //使用官方API请求预付订单
            Map<String, String> response = wxpay.unifiedOrder(data);
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
                    appResult.put("tradeNo", tradeNo);
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
