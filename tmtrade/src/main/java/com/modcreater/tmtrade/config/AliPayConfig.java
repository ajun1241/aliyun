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
    public static final String RSA_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCRLYKCjvo0fArAq6JQaxIMLRrAyWfoV8mwPKjl/KUzXUjVl64eyOzea3carfaYZRRye4lU9IPR7z+b/4PJM3DvsmnRfh0BVFeB+vrXbT+9VISEWkFUCAmH3x4hhdfgOoA+1ECJU1JXBgKz2E/xuwkAtE8EUxB5bQuofC50WKFx7wy+FI8hVq4cqTE+iZ9K152BCbOd5jRdhP0FJqfRkN6STnB6cg3XwlcMSiSrIbOhcwjD0ee/+EmULAKp/wMSQP68J5LHF25KFmtTW+em1y8sSK7j9DMYQaZX+9en4F1/FP8FmksluV4+PPIwzHblYiwBaMEiluG0vS65HcEQBQXpAgMBAAECggEAPl550hMQpJmhmPJjcf79quN5udcM38FPMXpt6Rgn9LAfyTs3n5wcPtWWPoz8Aq5yIVi3QBsnwnnxLtiPylFiNGfGlCyE03xjd8DWINSbbIAxyhZoOGyXg5qz/BzfCEK5s8RF0XlNR3uaj57fgW8jx/yucaIp0rCpMIHhBzTIbwRnCeUHsM+lf3kn7XLwNZlEcHQ/3BNiJCtLkLIaLvOD5DCZ2a5eUaEpuwvV4gbCS7fUF0nEQS1b7eZktofvcM/9X3pvlmq8cSm8A03otuLo2SE/bpxkfvz1u9RofG6rd1STMBs+ZjzWb+MgYF7j66Qk/jacFSt/EO5R3s/9DPQicQKBgQDiFAvMgLhEhbICuP2oE/idO4guzUcGTkA2Nx4q+TDdqgTX1Iats9vSj6r1fxcMrOBgoi8Aqq901gNWOTkhk5CDp4AuUxQZ8n0OahNxKcvUc7ezKFx6AxwWEYQOMOS+QmusBrCPYJK6+sRstVZVL4uirNXTW8rzAEm+lBgnkR8VpQKBgQCkZGcuSMqCJcRL25TX1MbQic1+Op7HIueP/G9xg/Qj7zh5hZfxSxW4/oWER4EY+KUrvp67vVYF96Xhk/IwX/PmcOGOgedHwlEAxsAXz1N145myrPbiZUQSTzIJDNzV7bFGg73k0Yhk+SJgXwSCRKBd1BdShhIdkFmgTneFZITj9QKBgQCr9eV3mt0OOcdJ7N37z50GM7cFKl0Avdp3onsO4tY5dM4UQPJkA2+L/H1kGFQ27vQIbLRlxG6K5xJIrmP3Vx/QFEMaeVTL27cllKfPJqSEp7Qt0OBuahkd7BrPFH+Y/Dqb8caweBuDn6Ryr4fIac7DYMWP6702Ep0FGe45glfrhQKBgEzuA28gd0wyekr5hgz+sM90PWr96cHM7spt2oUnt/98+lO8Fd/AQHki+r5ta9eQvFLdUJEQyIngW4tV3bePn6bOWm+DEQV+xMN1Pv2lcywvB4Ua9in6M8HRt9uOXmXqZtRV4G6NM6P1BoZM0OJZVSazkvp2bVHSdG7VaY9N+/ZlAoGADmNyQl1QPQoB4ctqHtz1Mu7nX3alFCz9FskQn+vtVTzztpXSLpNSqStnsQvRysvnDDQV1uruSk8vaGTynj3GzDLmqFPd/lvSYaCJpE0E7WhpsltyQz/ECjhtNqIpdpncso9WBney3XWMJUxMQtACwiJKP3rB6ULoyStfb6uj+h8=";

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
