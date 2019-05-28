package com.modcreater.tmtrade.config;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-28
 * Time: 8:58
 */
public class AliPayConfig {

    /**
     * 商户appid
     */
    public static final String APPID = "2019052065168786";
    /**
     * 商家ID
     */
    public static final String SELLER_ID = "2088531247419714";

    /**
     * 私钥 pkcs8格式的
     */
    public static final String RSA_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC0nBUFVNBCsWX3cmmoSFNO9Wqo8L6PYKsONJXwAn670kfx8DoHnO6KaSsMKwKIO/HhsA8k591cZcsH3+0RlJ/gzLCR1IUUPN99E++vlS8T4VGUR6P9bnycvikJjDcMnMCUzUgH3SwEQ0bpisevnZPnWyZxYs+tvsMZLSrcX0lS3ffZ8wSLvwlNq1/azP9v8sIJ6jbBbIrYToUp8Ve2Iz9eI+rkglkPQwO0+1YyZlg2UuahQ2ylYP7vyd0YvrRTdZeU7RiPleGXVnpNVLag5y5G+HDz9QXMsNaBjw1PaSJHMhGiQPyF/zZEu9rXzrIMcbYreFJINj43np41m7DoBc1pAgMBAAECggEAbYUcZiA5LcgfOf8CPQ6mngHKDOn31D/dWn7e0SziJwjrobV3qvgp2nrNpNJL9crL8XxuooWLwSTlCkBqpLeIf6wAAl5p2IzVRSaauNFxO8/tRUpwEhU2sWHKxsRk+VvwmAsne8VNlMQrMyF5129pqeinFijCIOzFp/WwwPNzJjD4NMoZEN/6+JSYzeUi+YmCf3YmJ29SvleRD0Eb6iwbSm84KQmDAQj9ngistp3w+R81bF9nATTZiE5RXkRVfRHnmwVkEQyk4MgDac2WNZSgScVj+tGgWfLfR2E1XusPZ9fX93F5Z/PBnMyyUcaStuWMi9eqgKAFaLPUAgGclH0ndQKBgQDoRcETOFj2Onglr986/fMSc7a3YRB2lr2YgZY8Ef3MkvJK25grFp76whthsupKpE6hctMQumU+vptJeMDoo126PknglOFJP37ky7Pn41TPDbqACd49stiflT3c+ixH4eQ4sc8F//ufZL5O92VdIbu+uyIGenGCNeEDxsM+g9hqSwKBgQDHD0ZeyjjjDGaDfZHiNuzX+fdY7A0xWwCfAQS8hwyHZbOQQEBMbJQlPzukPSTFPhEwVRCEUP7JVp2dUOOclwUBX7N6G2lwrB6lz78seQbOhVpTKXpbQ7omTUfz94Vb4ZNx46zDjc60qg71IzZyCxDZi/h9XUr5FRpXCNFU17EWmwKBgFB74zQ6wTDI/9rnPy3c0z5glD6kxqShBR90P8+e2Ffrz0M7JlY/52Syn/RRn7eviYOOGy7ft7dvKrXhvs6d1Rt0+/py/EF6XAkBwNwoPLnmYup9AOJWN5PMjfwlP/TEhGVUz4yfr9wCd6M7PIdx6fFkPJ0MDor1CnxZgkwGOZZTAoGBAJHPyJZKvxzhyn3rD1+LKDaUbfD/CDLtHClcz6Fhs3XCs6OozPCuYF5gUUnfV/37lw+2X+Dsi5XkEpRX2kKsBQial5eMWNIl4lVHNhxXkvrYh+9+5JfRppvD5D77Qv6o4B5+zeB5Eb8ZCtfaMVmcbX1NoHoESpTmNmMmnF/vl1KdAoGBAJIprE7jhB/w8qWp7gzjtjYg8F3cLs/AzWqZxA/IBI5aFabNDV9nRw0aeIW6e8gjmYt4KZbAtU3ymYphaN9qraQKQDrUED7fHDtWXMu4RTsYfEtV3z7PwUByu2Z/Ukq1xWFayaru7YrpL8byqrsgDw1+LZ0fqvdSABfEokXmVdK+";

    /**
     * 支付宝公钥
     */
    public static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq0v1G5XmG71grmkRpmY8rac2rxpIZtPOAZcuBjsYtleo34jlsTqzjVU6Wif6VRGmyVluEno81KvxbDOcT/dKgmP7P/HW+EbmaenyWnvugAlI0KRFmIOPG5S8hFBkyyT8kG2smsSQ/ZEp0npCJrXYeMpVn0sAD2CJaGL3pumtgrwAk7r8LFg1r5bdbTvaeuHSXGucOAebgmoEad5bKQpq9i6NVI1GLqsgbCP9vgqKvHfGOvpLnxBzySzEn7ptKlMi9d0C7HCbReDXaTtM6inf+te0uv6qMgAiF9N5IhuFUTDtE80U9sE5BB5hl8nDGeARTDHNNWB6Ix6nnM8p+R8u7QIDAQAB";

    /**
     * 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
     */
    public static final String NOTIFY_URL = "http:// http://bkb23i.natappfree.cc/alipay/notify_url.do";

    /**
     * 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
     */
    public static final String RETURN_URL = "http:// http://bkb23i.natappfree.cc/alipay/return_url.do";

    /**
     * 请求网关地址
     */
    public static final String URL = "https://openapi.alipay.com/gateway.do";

    /**
     * 编码
     */
    public static final String CHARSET = "UTF-8";

    /**
     * 返回格式
     */
    public static final String FORMAT = "json";

    /**
     * 加密类型
     */
    public static final String SIGNTYPE = "RSA2";

}
