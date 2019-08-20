package com.modcreater.tmtrade.config;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-30
 * Time: 15:37
 */
public class WxPayConfig {
    /**
     * 应用ID(微信开放平台审核通过的应用APPID)
     */
    public static String APP_ID = "wx32c50fdca073afb0";
    /**
     * 商户ID(微信支付分配的商户号)
     */
    public static String MCH_ID = "1537117381";
    /**
     * 终端IP
     */
    public static String SPBILL_CREATE_IP = "101.67.123.248";
    /**
     * 通知地址
     */
    public static String NOTIFY_URL = "http://2wr6ii.natappfree.cc/pay/wxpay/notify_url";
    /**
     * 交易类型
     */
    public static String TRADE_TYPE = "APP";
}
