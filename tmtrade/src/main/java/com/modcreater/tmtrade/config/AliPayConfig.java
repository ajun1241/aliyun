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
    public static final String APPID = "2016092400587194";
    /**
     * 商家ID
     */
    public static final String SELLER_ID = "2088102177092670";

    /**
     * 私钥 pkcs8格式的
     */
    public static final String RSA_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC0nBUFVNBCsWX3cmmoSFNO9Wqo8L6PYKsONJXwAn670kfx8DoHnO6KaSsMKwKIO/HhsA8k591cZcsH3+0RlJ/gzLCR1IUUPN99E++vlS8T4VGUR6P9bnycvikJjDcMnMCUzUgH3SwEQ0bpisevnZPnWyZxYs+tvsMZLSrcX0lS3ffZ8wSLvwlNq1/azP9v8sIJ6jbBbIrYToUp8Ve2Iz9eI+rkglkPQwO0+1YyZlg2UuahQ2ylYP7vyd0YvrRTdZeU7RiPleGXVnpNVLag5y5G+HDz9QXMsNaBjw1PaSJHMhGiQPyF/zZEu9rXzrIMcbYreFJINj43np41m7DoBc1pAgMBAAECggEAbYUcZiA5LcgfOf8CPQ6mngHKDOn31D/dWn7e0SziJwjrobV3qvgp2nrNpNJL9crL8XxuooWLwSTlCkBqpLeIf6wAAl5p2IzVRSaauNFxO8/tRUpwEhU2sWHKxsRk+VvwmAsne8VNlMQrMyF5129pqeinFijCIOzFp/WwwPNzJjD4NMoZEN/6+JSYzeUi+YmCf3YmJ29SvleRD0Eb6iwbSm84KQmDAQj9ngistp3w+R81bF9nATTZiE5RXkRVfRHnmwVkEQyk4MgDac2WNZSgScVj+tGgWfLfR2E1XusPZ9fX93F5Z/PBnMyyUcaStuWMi9eqgKAFaLPUAgGclH0ndQKBgQDoRcETOFj2Onglr986/fMSc7a3YRB2lr2YgZY8Ef3MkvJK25grFp76whthsupKpE6hctMQumU+vptJeMDoo126PknglOFJP37ky7Pn41TPDbqACd49stiflT3c+ixH4eQ4sc8F//ufZL5O92VdIbu+uyIGenGCNeEDxsM+g9hqSwKBgQDHD0ZeyjjjDGaDfZHiNuzX+fdY7A0xWwCfAQS8hwyHZbOQQEBMbJQlPzukPSTFPhEwVRCEUP7JVp2dUOOclwUBX7N6G2lwrB6lz78seQbOhVpTKXpbQ7omTUfz94Vb4ZNx46zDjc60qg71IzZyCxDZi/h9XUr5FRpXCNFU17EWmwKBgFB74zQ6wTDI/9rnPy3c0z5glD6kxqShBR90P8+e2Ffrz0M7JlY/52Syn/RRn7eviYOOGy7ft7dvKrXhvs6d1Rt0+/py/EF6XAkBwNwoPLnmYup9AOJWN5PMjfwlP/TEhGVUz4yfr9wCd6M7PIdx6fFkPJ0MDor1CnxZgkwGOZZTAoGBAJHPyJZKvxzhyn3rD1+LKDaUbfD/CDLtHClcz6Fhs3XCs6OozPCuYF5gUUnfV/37lw+2X+Dsi5XkEpRX2kKsBQial5eMWNIl4lVHNhxXkvrYh+9+5JfRppvD5D77Qv6o4B5+zeB5Eb8ZCtfaMVmcbX1NoHoESpTmNmMmnF/vl1KdAoGBAJIprE7jhB/w8qWp7gzjtjYg8F3cLs/AzWqZxA/IBI5aFabNDV9nRw0aeIW6e8gjmYt4KZbAtU3ymYphaN9qraQKQDrUED7fHDtWXMu4RTsYfEtV3z7PwUByu2Z/Ukq1xWFayaru7YrpL8byqrsgDw1+LZ0fqvdSABfEokXmVdK+";

    /**
     * 支付宝公钥
     */
    public static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtqeK0KpKP/xujYGPzm1OmyCZ8aqOKqHHHiw6kGxToxQm7J9XnGYaFwh6GkIrlHOlaa0P9/wTNuTiWSYX/RAoE+KwGYZPsFbXI7A5ah25LV4yhy36rYJKfLpfVe9nKdWpNrxttSYll6BKoDIc/Y3f2mGORB2t/99+G2w5wv8vSWdbmYuoZi3/rF4ptxG3axCvRH031LClAi2hjGf4XmNGwprbdVlIJVYz7Yts1nutmrY0o91Tm72aQ00Dy+oKseykfYJbzO2WwvjNRCKO6ln78RrVzSJ75ftquykRlR02JVDvHMOh9g7nf2l1W/6k1HcR1/PRkm4e7VZOGCUWeOxSOQIDAQAB";

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
