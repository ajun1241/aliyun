package com.modcreater.tmtrade.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.modcreater.tmdao.mapper.OrderMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static com.alipay.api.AlipayConstants.APP_ID;
import static com.alipay.api.AlipayConstants.CHARSET;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-24
 * Time: 17:42
 */
@RestController
@RequestMapping(value = "/AliPay/")
public class AliPayController {
    private AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", "2016092400587194", "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCRLYKCjvo0fArAq6JQaxIMLRrAyWfoV8mwPKjl/KUzXUjVl64eyOzea3carfaYZRRye4lU9IPR7z+b/4PJM3DvsmnRfh0BVFeB+vrXbT+9VISEWkFUCAmH3x4hhdfgOoA+1ECJU1JXBgKz2E/xuwkAtE8EUxB5bQuofC50WKFx7wy+FI8hVq4cqTE+iZ9K152BCbOd5jRdhP0FJqfRkN6STnB6cg3XwlcMSiSrIbOhcwjD0ee/+EmULAKp/wMSQP68J5LHF25KFmtTW+em1y8sSK7j9DMYQaZX+9en4F1/FP8FmksluV4+PPIwzHblYiwBaMEiluG0vS65HcEQBQXpAgMBAAECggEAPl550hMQpJmhmPJjcf79quN5udcM38FPMXpt6Rgn9LAfyTs3n5wcPtWWPoz8Aq5yIVi3QBsnwnnxLtiPylFiNGfGlCyE03xjd8DWINSbbIAxyhZoOGyXg5qz/BzfCEK5s8RF0XlNR3uaj57fgW8jx/yucaIp0rCpMIHhBzTIbwRnCeUHsM+lf3kn7XLwNZlEcHQ/3BNiJCtLkLIaLvOD5DCZ2a5eUaEpuwvV4gbCS7fUF0nEQS1b7eZktofvcM/9X3pvlmq8cSm8A03otuLo2SE/bpxkfvz1u9RofG6rd1STMBs+ZjzWb+MgYF7j66Qk/jacFSt/EO5R3s/9DPQicQKBgQDiFAvMgLhEhbICuP2oE/idO4guzUcGTkA2Nx4q+TDdqgTX1Iats9vSj6r1fxcMrOBgoi8Aqq901gNWOTkhk5CDp4AuUxQZ8n0OahNxKcvUc7ezKFx6AxwWEYQOMOS+QmusBrCPYJK6+sRstVZVL4uirNXTW8rzAEm+lBgnkR8VpQKBgQCkZGcuSMqCJcRL25TX1MbQic1+Op7HIueP/G9xg/Qj7zh5hZfxSxW4/oWER4EY+KUrvp67vVYF96Xhk/IwX/PmcOGOgedHwlEAxsAXz1N145myrPbiZUQSTzIJDNzV7bFGg73k0Yhk+SJgXwSCRKBd1BdShhIdkFmgTneFZITj9QKBgQCr9eV3mt0OOcdJ7N37z50GM7cFKl0Avdp3onsO4tY5dM4UQPJkA2+L/H1kGFQ27vQIbLRlxG6K5xJIrmP3Vx/QFEMaeVTL27cllKfPJqSEp7Qt0OBuahkd7BrPFH+Y/Dqb8caweBuDn6Ryr4fIac7DYMWP6702Ep0FGe45glfrhQKBgEzuA28gd0wyekr5hgz+sM90PWr96cHM7spt2oUnt/98+lO8Fd/AQHki+r5ta9eQvFLdUJEQyIngW4tV3bePn6bOWm+DEQV+xMN1Pv2lcywvB4Ua9in6M8HRt9uOXmXqZtRV4G6NM6P1BoZM0OJZVSazkvp2bVHSdG7VaY9N+/ZlAoGADmNyQl1QPQoB4ctqHtz1Mu7nX3alFCz9FskQn+vtVTzztpXSLpNSqStnsQvRysvnDDQV1uruSk8vaGTynj3GzDLmqFPd/lvSYaCJpE0E7WhpsltyQz/ECjhtNqIpdpncso9WBney3XWMJUxMQtACwiJKP3rB6ULoyStfb6uj+h8=", "json", CHARSET, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtqeK0KpKP/xujYGPzm1OmyCZ8aqOKqHHHiw6kGxToxQm7J9XnGYaFwh6GkIrlHOlaa0P9/wTNuTiWSYX/RAoE+KwGYZPsFbXI7A5ah25LV4yhy36rYJKfLpfVe9nKdWpNrxttSYll6BKoDIc/Y3f2mGORB2t/99+G2w5wv8vSWdbmYuoZi3/rF4ptxG3axCvRH031LClAi2hjGf4XmNGwprbdVlIJVYz7Yts1nutmrY0o91Tm72aQ00Dy+oKseykfYJbzO2WwvjNRCKO6ln78RrVzSJ75ftquykRlR02JVDvHMOh9g7nf2l1W/6k1HcR1/PRkm4e7VZOGCUWeOxSOQIDAQAB", "RSA2");

    @Resource
    private OrderMapper orderMapper;

    @PostMapping(value = "alipay")
    public void aliPayOrderSubmitted(){
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        //订单详情
        model.setBody("我是测试数据");
        //订单标题
        model.setSubject("App支付测试Java");
        //商家订单号
        model.setOutTradeNo("");
        //订单超时时间(时间到后自动关闭订单)
        model.setTimeoutExpress("30m");
        //订单总支付金额
        model.setTotalAmount("0.01");
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl("商户外网可以访问的异步地址");
        request.setNotifyUrl("");
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            System.out.println(response.getBody());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }



}
