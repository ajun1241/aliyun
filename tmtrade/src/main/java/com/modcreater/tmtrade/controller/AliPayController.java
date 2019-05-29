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

    public static String APP_ID="2019052065168786";
    public static String APP_PRIVATE_KEY="MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCrOPZAzmxeSSKSdzj5YwUOrSd/GPSReqPfs93isVE6IukwTE6IMMBRhUdeKsaEJFIxCMevjlGTrCnE5pOmz/Ol6rHzwfUlguWywRb/SZkep5OX7Bu5P70H9HqW83uebclMF7P33vImTe8D13RoprSdDXy2VFNMGkts/kyGtpo37uJcpCdZJwNza2j4Tp5NyhVwhecGgF5dnHkkTnmxF5hnyfR9y6wo7PuM5WSGEqj3SMs2y7LzVYqCK6z84c/2sEd4YQS405SYTHSIeQGY4Mhc3u1QswAAXjFdL+9OxkALUBVzUuvryk105nUQT/2sQsaxD0BYDsy8w5bvAZn/s7P9AgMBAAECggEAPEHXqAb9bFEgsZrzmhCW/wtBEyGdMHWQpnGObPEqPON8XVr9aEB/7jCRZTp4kpiVtFv6qnXTj7zPFlRvMg2NGZ728WoY9BKU88uwZDdqg7dF4fld6FveEqafXfBiWOcaKIVO+LSlU4Wp0BhLB2ljtsHAWgpjJi06zijQlbg9neEHPaEnojxiJu00q0a5boy1l0pCwuj2xJGUPwnTMpye3VhOS88MVxUt5HnG6aa+7Y1aZrioJynDsAXR07yFKKe6H3xc9Ymk3DP4u2AH3YYu7uy+F0pijfDp+xe3ucCTBJAB8dJ9FyP8I4j+pQQIy+gTD+mUTy0cmSwAmCDtwYH/AQKBgQDxYAznY3KCDuppxa2A5bvadhioePeJPqZZ/m+fV/XAsT5w1UoSz0SGSUZan6bX1VpQjQUUs8Am04d5xjJvmdzaxo72HQLQasbHIW0CQn/uWMLfG2hQwl6VS/a3xIgfo4jixPc5hnqpg3RG7NN+2GlUqMKMASQ0PvnchVe3NU8eCQKBgQC1mMd/JSqlDVAecuFZ6DWcKnecWm9rCWFKNRN7/0WzMiiku832Vz5hRD8Z2GrVb5S03k8zErhX8PShCZhHh+07xssU5GCtvHmik507es53N7+QEpErHAotYeuqmpPwk26vPftGqupgSR6v+yScfHbN2RjjqNwUeF1ggUQ8zZejVQKBgBYN1J//siK4AisP6L5yPyvCxpX+F3/uvXxThxdkDgxBJZdhpZb6YSRz5X26QKBP2iSp/eDaW6Awi5xQw6L2x/slUUKje/JXDzp6j185fD7m1UdVVb6rQ8EYY75+soFRi9xMatpSszpxzFn+oYRheZ5GnR/1qr0rU+EWPeF7eXMhAoGAXMoPDdGg46tUx+otabI0SnKMoZC4I7osQy2xogdCxxiXGe14hSn5Dtw/XeZuWFbmZjC9yTwEg7L0XKC5dbtnpcajGC7Fk6Cdikvg6HaMrvAGawiBssRmEuBY+dh4RVIgGV0TWv2UOtTB4Gv0Ph1+2xnWFOXAZplGJTyH8IWguO0CgYB83pByE7bEROLhuNc3DqIXD3RIjnhr/BKlM1ePQCsBj8MQL8qsgwDqYRE8SSUE30UMMQC+AdnJbhJ0OHSi2VHHAkd3kkXfTGryuWBsAckdHxXWQUAZOkTE3SlLc0kGm/mBUmXhbMEKk8yY3K9Tex0WLO3WXQWPY/00vGjIh1B8aw==";
    public static String ALIPAY_PUBLIC_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq0v1G5XmG71grmkRpmY8rac2rxpIZtPOAZcuBjsYtleo34jlsTqzjVU6Wif6VRGmyVluEno81KvxbDOcT/dKgmP7P/HW+EbmaenyWnvugAlI0KRFmIOPG5S8hFBkyyT8kG2smsSQ/ZEp0npCJrXYeMpVn0sAD2CJaGL3pumtgrwAk7r8LFg1r5bdbTvaeuHSXGucOAebgmoEad5bKQpq9i6NVI1GLqsgbCP9vgqKvHfGOvpLnxBzySzEn7ptKlMi9d0C7HCbReDXaTtM6inf+te0uv6qMgAiF9N5IhuFUTDtE80U9sE5BB5hl8nDGeARTDHNNWB6Ix6nnM8p+R8u7QIDAQAB";
    public static String sign_type="RSA2";
    public static String CHARSET="utf-8";
    public static String url = "https://openapi.alipay.com/gateway.do";
    public static String SELLER_ID = "2088531247419714";

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

        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setOutTradeNo(userOrder.getId());
        model.setSubject("手机端"+userOrder.getOrderTitle()+"移动支付");
        model.setTotalAmount(userOrder.getPaymentAmount().toString());
        model.setBody("您花费"+userOrder.getPaymentAmount()+"元");
        model.setTimeoutExpress("30m");
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setNotifyUrl("http://vmbb6m.natappfree.cc/alipay/notify_url.do");
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

    @PostMapping(value = "/pay/payinfoverify")
    public Dto payInfoVerify(@RequestBody ReceivedVerifyInfo receivedVerifyInfo, HttpServletRequest request){
        return orderService.payInfoVerify(receivedVerifyInfo,request.getHeader("token"));
    }

    @PostMapping(value = "api/alipay/notify_url")
    public String notify(HttpServletRequest request, HttpServletResponse response){
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        //1.从支付宝回调的request域中取值
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
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
        String out_trade_no = request.getParameter("out_trade_no");
        //交易状态
        String tradeStatus = request.getParameter("trade_status");
        //支付宝流水号
        String trade_no = request.getParameter("trade_no");
        //卖家ID
        String seller_id = request.getParameter("seller_id");
        if (!seller_id.equals(SELLER_ID)){
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
            if(tradeStatus.equals("TRADE_SUCCESS")) {
                UserOrders userOrders = orderService.getUserOrderById(out_trade_no);
                userOrders.setOrderStatus("1");
                userOrders.setPayTime(String.valueOf(System.currentTimeMillis()/1000));
                userOrders.setPayChannel("AliPay");
                userOrders.setOutTradeNo(trade_no);
                //更新交易表中状态
                int returnResult = orderService.updateOrderStatusToPrepaid(userOrders);
                if(returnResult>0){
                    return "success";
                }else{
                    return "fail";
                }
            }else{
                return "fail";
            }
        } else {
            //验签不通过
            System.err.println("验签失败");
            return "fail";
        }
    }

}
