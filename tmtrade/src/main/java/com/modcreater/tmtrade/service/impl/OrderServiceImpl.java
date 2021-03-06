package com.modcreater.tmtrade.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import com.modcreater.tmbeans.pojo.*;
import com.modcreater.tmbeans.show.goods.ShowConsumable;
import com.modcreater.tmbeans.show.order.ShowUserOrders;
import com.modcreater.tmbeans.values.FinalValues;
import com.modcreater.tmbeans.vo.trade.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmdao.mapper.*;
import com.modcreater.tmutils.pay.PayUtil;
import com.modcreater.tmutils.payconfig.WxPayConfig;
import com.modcreater.tmtrade.service.OrderService;
import com.modcreater.tmutils.DateUtil;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmutils.payconfig.wxconfig.WxConfig;
import com.modcreater.tmutils.payconfig.wxconfig.WxMD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.modcreater.tmutils.payconfig.AliPayConfig.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-27
 * Time: 14:04
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private UserServiceMapper userServiceMapper;

    @Resource
    private GroupMapper groupMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final Logger logger = LoggerFactory.getLogger("trade");

    private static final Double FALSE_GOODS_PRICE = 1000000.00;

    @Override
    public Dto createNewOrder(ReceivedOrderInfo receivedOrderInfo) {
        if ("1".equals(receivedOrderInfo.getServiceId()) && !ObjectUtils.isEmpty(userServiceMapper.getServiceRemainingTime(receivedOrderInfo.getUserId(), receivedOrderInfo.getServiceId()))) {
            System.out.println("该用户已经开通了好友服务");
            return DtoUtil.getFalseDto("该用户已经开通了好友服务", 60017);
        }
        if ("perpetual".equals(receivedOrderInfo.getServiceType())) {
            if (!"1".equals(receivedOrderInfo.getServiceId())) {
                logger.info("只有好友功能才能开通永久");
                return DtoUtil.getFalseDto("服务类型错误", 60015);
            }
            if (receivedOrderInfo.getNumber() == null || receivedOrderInfo.getNumber() == 0) {
                receivedOrderInfo.setNumber(1L);
            } else if (receivedOrderInfo.getNumber() != 1) {
                return DtoUtil.getFalseDto("数量错误", 60018);
            }
        }
        if ("month".equals(receivedOrderInfo.getServiceType()) || receivedOrderInfo.getServiceType().equals("year")) {
            if ("1".equals(receivedOrderInfo.getServiceId())) {
                logger.info("好友功能不能开通月/年卡");
                return DtoUtil.getFalseDto("服务类型错误", 600015);
            }
        }
        if ("quarter".equals(receivedOrderInfo.getServiceType())) {
            if (!"5".equals(receivedOrderInfo.getServiceId())) {
                return DtoUtil.getFalseDto("服务类型错误", 600015);
            }
        }
        Long remainingTime = userServiceMapper.getTimeRemaining(receivedOrderInfo.getUserId(), receivedOrderInfo.getServiceId());
        if (remainingTime != null) {
            if (receivedOrderInfo.getServiceType().equals("time")) {
                if (receivedOrderInfo.getServiceId().equals("1") || receivedOrderInfo.getServiceId().equals("3") || receivedOrderInfo.getServiceId().equals("5")) {
                    logger.info("好友/数据反馈/商铺服务功能没有次卡");
                    return DtoUtil.getFalseDto("服务类型错误", 600015);
                }
                if (remainingTime != 0 && remainingTime > System.currentTimeMillis() / 1000) {
                    return DtoUtil.getFalseDto("请于年/季/月卡过期后再开通", 60014);
                }
            }
        }
        UserOrders userOrder = new UserOrders();
        userOrder.setUserId(receivedOrderInfo.getUserId());
        userOrder.setServiceId(receivedOrderInfo.getServiceId());
        userOrder.setOrderTitle(receivedOrderInfo.getOrderTitle());
        userOrder.setId("" + System.currentTimeMillis() / 1000 + receivedOrderInfo.getUserId());
        userOrder.setNumber(receivedOrderInfo.getNumber());
        userOrder.setServiceType(receivedOrderInfo.getServiceType());
        double unitPrice = orderMapper.getUnitPrice(receivedOrderInfo.getServiceId(), receivedOrderInfo.getServiceType());
        /*if (unitPrice != 0 && receivedOrderInfo.getPaymentAmount() / userOrder.getNumber() - (unitPrice) != 0) {
            return DtoUtil.getFalseDto("订单金额错误", 60001);
        }*/
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setRoundingMode(RoundingMode.HALF_UP);
        nf.setMaximumFractionDigits(2);
        if (!receivedOrderInfo.getDiscountUserId().equals("0")) {
            DiscountUser discountUser = orderMapper.getDiscountUser(receivedOrderInfo.getDiscountUserId());
            if (ObjectUtils.isEmpty(discountUser)) {
                return DtoUtil.getFalseDto("优惠券已过期", 61002);
            }
            DiscountCoupon discountCoupon = orderMapper.getDiscountCoupon(discountUser.getDiscountId().toString());
            if ((!ObjectUtils.isEmpty(discountCoupon)) && discountCoupon.getCouponType().toString().equals(receivedOrderInfo.getServiceId())) {
                userOrder.setPaymentAmount(Double.valueOf(nf.format(unitPrice * userOrder.getNumber() - discountCoupon.getCouponMoney())));
                orderMapper.setDiscountCouponOrderId(discountUser.getId(), userOrder.getId(), "1");
            } else {
                return DtoUtil.getFalseDto("优惠券使用失败", 61001);
            }
        } else {
            userOrder.setPaymentAmount(unitPrice);
        }
        userOrder.setCreateDate(System.currentTimeMillis() / 1000);
        userOrder.setRemark(receivedOrderInfo.getUserRemark());
        if (orderMapper.addNewOrder(userOrder) == 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getFalseDto("订单生成失败", 60002);
        }
        return DtoUtil.getSuccesWithDataDto("success", userOrder, 100000);
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

    /**
     * 此处最重要的一点!!! : 年报的storageTime是用来记录用户最近一次开通年费报表的时间
     *
     * @param receivedVerifyInfo
     * @param token
     * @return
     */
    @Override
    public Dto payInfoVerify(ReceivedVerifyInfo receivedVerifyInfo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedVerifyInfo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!StringUtils.hasText(receivedVerifyInfo.getId())) {
            return DtoUtil.getFalseDto("未获取到订单号", 60004);
        }
        if (receivedVerifyInfo.getId().startsWith("sot")){
            StoreOfflineOrders offlineOrders = goodsMapper.getOfflineOrder(receivedVerifyInfo.getId());
            if (offlineOrders.getOrderStatus() == 3) {
                return DtoUtil.getFalseDto("订单已过期", 60021);
            }
            if (offlineOrders.getOrderStatus() == 2) {
                return DtoUtil.getSuccessDto("订单支付成功", 100000);
            }
        }else {
            UserOrders userOrders = orderMapper.getUserOrder(receivedVerifyInfo.getId());
            if ("3".equals(userOrders.getOrderStatus())) {
                return DtoUtil.getFalseDto("订单已过期", 60021);
            }
            if ("2".equals(userOrders.getOrderStatus())) {
                return DtoUtil.getSuccessDto("订单支付成功", 100000);
            }
        }
        return DtoUtil.getFalseDto("订单异常", 60003);
    }

    @Override
    public String alipayNotify(HttpServletRequest request) {
        logger.info("支付宝异步回调");
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
                if (outTradeNo.startsWith("sot")){
                    //此处处理商铺模块用户线下扫码支付
                    StoreOfflineOrders offlineOrders = goodsMapper.getOfflineOrder(outTradeNo);
                    offlineOrders.setOrderStatus(2L);
                    offlineOrders.setPayTime(System.currentTimeMillis());
                    offlineOrders.setPayChannel("支付宝支付");
                    offlineOrders.setOutTradeNo(tradeNo);
                    goodsMapper.updateOfflineOrder(offlineOrders);
                    //支付成功增加商铺余额
                    storeMapper.updWallet(offlineOrders.getPaymentAmount(),offlineOrders.getSourceStoreId());
                    //订单成功后解析已卖出商品并将数量记录到销量表中
                    //(因为要根据时间计算商品销量,所以该表中同一商铺下会有多个商品及对应数量)
                    List<Map> goodsList = JSONObject.parseArray(goodsMapper.getTemStock(outTradeNo),Map.class);
                    for (Map goods : goodsList){
                        StoreSalesVolume storeSalesVolume = new StoreSalesVolume();
                        storeSalesVolume.setGoodsId(goodsMapper.getGoodsStockByGoodsBarCode(offlineOrders.getSourceStoreId().toString(),goods.get("goodsBarCode").toString()).getGoodsId().toString());
                        storeSalesVolume.setNum(Long.valueOf(goods.get("num").toString()));
                        storeSalesVolume.setStoreId(offlineOrders.getSourceStoreId().toString());
                        storeSalesVolume.setOrderNumber(outTradeNo);
                        goodsMapper.addNewSalesVolume(storeSalesVolume);
                    }
                    return "success";
                }else {
                    UserOrders userOrders = getUserOrderById(outTradeNo);
                    userOrders.setOrderStatus("1");
                    userOrders.setPayTime(String.valueOf(System.currentTimeMillis() / 1000));
                    userOrders.setPayChannel("支付宝支付");
                    userOrders.setOutTradeNo(tradeNo);
                    //更新交易表中状态
                    int returnResult = updateOrderStatusToPrepaid(userOrders);
                    if (returnResult > 0) {
                        if (!makeOrderSuccess(outTradeNo)) {
                            orderMapper.updateOrderStatus(outTradeNo, 4);
                        }
                        //将用户优惠券状态改为已使用
                        orderMapper.updateDiscountStatus(outTradeNo);
                        return "success";
                    } else {
                        logger.info("订单状态修改失败");
                        return "fail";
                    }
                }
            } else {
                logger.info("支付失败");
                return "fail";
            }
        } else {
            //验签不通过
            logger.info("验签失败");
            return "fail";
        }
    }

    @Override
    public Dto alipay(ReceivedOrderInfo receivedOrderInfo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedOrderInfo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        Dto dto = createNewOrder(receivedOrderInfo);
        if (dto.getResCode() != 100000) {
            return dto;
        }
        UserOrders userOrder = (UserOrders) dto.getData();
        return PayUtil.aliOrderMaker(userOrder.getId(),userOrder.getOrderTitle(),userOrder.getPaymentAmount());
    }

    @Override
    public Dto wxPayOrderSubmitted(ReceivedOrderInfo receivedOrderInfo, String token) throws Exception {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedOrderInfo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        Dto dto = createNewOrder(receivedOrderInfo);
        if (dto.getResCode() != 100000) {
            return dto;
        }
        UserOrders userOrder = (UserOrders) dto.getData();
        return PayUtil.wxOrderMaker(userOrder.getId(),userOrder.getPaymentAmount());
    }

    @Override
    public String wxPayNotify(HttpServletRequest request) {
        logger.info("微信支付回调成功");
        String resXml = "";
        try {
            InputStream inputStream = request.getInputStream();
            //将InputStream转换成xmlString
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            resXml = sb.toString();
            return payBack(resXml);
        } catch (Exception e) {
            logger.info("微信手机支付失败:" + e.getMessage());
            return "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
        }
    }

    @Override
    public String payBack(String wxNotifyData) {
        WxConfig config = null;
        try {
            config = new WxConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
        WXPay wxpay = new WXPay(config);
        String xmlBack = "";
        Map<String, String> notifyMap = null;
        try {
            // 调用官方SDK转换成map类型数据
            notifyMap = WXPayUtil.xmlToMap(wxNotifyData);
            //验证签名是否有效，有效则进一步处理
            if (wxpay.isPayResultNotifySignatureValid(notifyMap)) {
                //状态
                String returnCode = notifyMap.get("return_code");
                //商户订单号
                String tradeNo = notifyMap.get("out_trade_no");
                //微信支付流水号
                String transactionId = notifyMap.get("transaction_id");
                if (returnCode.equals("SUCCESS")) {
                    //此处填写验签成功后的业务逻辑
                    xmlBack = wxPaySignSuccess(tradeNo,transactionId);
                }
                return xmlBack;
            } else {
                // 签名错误，如果数据里没有sign字段，也认为是签名错误
                //失败的数据要不要存储？
                logger.error("手机支付回调通知签名错误");
                xmlBack = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
                return xmlBack;
            }
        } catch (Exception e) {
            logger.error("手机支付回调通知失败", e);
            xmlBack = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
        }
        return xmlBack;
    }

    @Override
    public Dto isFriendServiceOpened(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (ObjectUtils.isEmpty(userServiceMapper.getServiceRemainingTime(receivedId.getUserId(), "1"))) {
            return DtoUtil.getSuccessDto("该用户尚未开通好友服务", 100000);
        }
        return DtoUtil.getSuccessDto("该用户已开通好友服务", 200000);
    }

    @Override
    public Dto searchUserService(ReceivedServiceIdUserId receivedServiceIdUserId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedServiceIdUserId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        ServiceRemainingTime serviceRemainingTime = userServiceMapper.getServiceRemainingTime(receivedServiceIdUserId.getUserId(), receivedServiceIdUserId.getServiceId());
        if (ObjectUtils.isEmpty(serviceRemainingTime)) {
            return DtoUtil.getSuccesWithDataDto("查询成功", "小主暂未开通该服务", 100000);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (serviceRemainingTime.getServiceId().equals("2")) {
            if (serviceRemainingTime.getResidueDegree() == 0) {
                String time = simpleDateFormat.format(DateUtil.stampToDate(serviceRemainingTime.getTimeRemaining().toString()));
                if (serviceRemainingTime.getTimeRemaining() > System.currentTimeMillis() / 1000) {
                    return DtoUtil.getSuccesWithDataDto("查询成功", time + "到期", 100000);
                } else if (serviceRemainingTime.getTimeRemaining() == 0) {
                    return DtoUtil.getSuccesWithDataDto("searchService", "小主暂未开通该服务", 100000);
                } else {
                    return DtoUtil.getSuccesWithDataDto("searchService", "已过期", 100000);
                }
            } else {
                if (serviceRemainingTime.getStorageTime() != 0) {
                    return DtoUtil.getSuccesWithDataDto("searchService", "次卡剩" + serviceRemainingTime.getResidueDegree().toString() + "次,月/年卡待使用", 100000);
                } else {
                    return DtoUtil.getSuccesWithDataDto("searchService", "次卡剩" + serviceRemainingTime.getResidueDegree().toString() + "次", 100000);
                }
            }
        }
        if (serviceRemainingTime.getServiceId().equals("3")) {
            String time = simpleDateFormat.format(DateUtil.stampToDate(serviceRemainingTime.getTimeRemaining().toString()));
            if (serviceRemainingTime.getTimeRemaining() == 0) {
                return DtoUtil.getSuccesWithDataDto("annualReportingService", "小主暂未开通该服务", 100000);
            } else if (serviceRemainingTime.getTimeRemaining() > System.currentTimeMillis() / 1000) {
                return DtoUtil.getSuccesWithDataDto("annualReportingService", time + "到期", 100000);
            } else {
                return DtoUtil.getSuccesWithDataDto("annualReportingService", "已过期", 100000);
            }
        }
        if (serviceRemainingTime.getServiceId().equals("4")) {
            if (serviceRemainingTime.getResidueDegree() == 0) {
                String time = simpleDateFormat.format(DateUtil.stampToDate(serviceRemainingTime.getTimeRemaining().toString()));
                if (serviceRemainingTime.getTimeRemaining() > System.currentTimeMillis() / 1000) {
                    return DtoUtil.getSuccesWithDataDto("backupService", time + "到期", 100000);
                } else if (serviceRemainingTime.getTimeRemaining() == 0) {
                    return DtoUtil.getSuccesWithDataDto("backupService", "小主暂未开通该服务", 100000);
                } else {
                    return DtoUtil.getSuccesWithDataDto("backupService", "已过期", 100000);
                }
            } else {
                if (serviceRemainingTime.getStorageTime() == 0) {
                    return DtoUtil.getSuccesWithDataDto("backupService", "次卡剩" + serviceRemainingTime.getResidueDegree().toString(), 100000);
                }
            }
        }
        if (serviceRemainingTime.getServiceId().equals("5")) {
            String time = simpleDateFormat.format(DateUtil.stampToDate(serviceRemainingTime.getTimeRemaining().toString()));
            if (serviceRemainingTime.getTimeRemaining() > System.currentTimeMillis() / 1000) {
                return DtoUtil.getSuccesWithDataDto("storeService", time + "到期", 100000);
            } else if (serviceRemainingTime.getTimeRemaining() == 0) {
                return DtoUtil.getSuccesWithDataDto("storeService", "小主暂未开通该服务", 100000);
            } else {
                return DtoUtil.getSuccesWithDataDto("storeService", "已过期", 100000);
            }
        }
        return DtoUtil.getFalseDto("参数有误", 60020);
    }

    @Override
    public Dto searchUserOrders(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        List<ShowUserOrders> userOrdersList = orderMapper.getUserAllOrders(receivedId.getUserId());
        if (userOrdersList.size() == 0) {
            return DtoUtil.getSuccessDto("未查询到订单", 200000);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功", userOrdersList, 100000);
    }

    @Override
    public Dto getServicePrice(ReceivedGoodsInfo receivedGoodsInfo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedGoodsInfo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        Double price = orderMapper.getUnitPrice(receivedGoodsInfo.getServiceId(), receivedGoodsInfo.getServiceType());
        if (price != 0) {
            price *= receivedGoodsInfo.getNum();
            return DtoUtil.getSuccesWithDataDto("", price, 100000);
        } else if (price.equals(FALSE_GOODS_PRICE)) {
            return DtoUtil.getFalseDto("商品类型错误", 200000);
        }
        return DtoUtil.getFalseDto("价格获取失败", 200000);
    }

    @Override
    public Dto searchAllUserService(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        ReceivedServiceIdUserId receivedServiceIdUserId = new ReceivedServiceIdUserId();
        receivedServiceIdUserId.setUserId(receivedId.getUserId());
        Map<String, String> result = new HashMap<>();
        result.put("searchService", "");
        result.put("annualReportingService", "");
        result.put("backupService", "");
        result.put("storeService", "");
        String key;
        for (int i = 2; i <= 5; i++) {
            if (i == 2) {
                key = "searchService";
            } else if (i == 3) {
                key = "annualReportingService";
            } else if (i == 4) {
                key = "backupService";
            } else {
                key = "storeService";
            }
            receivedServiceIdUserId.setServiceId(i + "");
            Dto dto = searchUserService(receivedServiceIdUserId, token);
            result.put(key, dto.getResCode() == 100000 ? dto.getData().toString() : "");
        }
        return DtoUtil.getSuccesWithDataDto("获取成功", result, 100000);
    }

    @Override
    public Dto getAllUserServiceForIOS(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        List<Map<String, String>> result = new ArrayList<>();
        String[] serviceIds = {"1", "5", "2", "3", "4"};
        for (int i = 0; i <= 4; i++) {
            Map<String, String> service = new HashMap<>();
            service.put("message", "小主暂未开通该服务");
            service.put("status", "0");
            String serviceId = serviceIds[i];
            service.put("serviceId", serviceId);
            ServiceRemainingTime serviceRemainingTime = userServiceMapper.getServiceRemainingTime(receivedId.getUserId(), serviceId);
            if (ObjectUtils.isEmpty(serviceRemainingTime)) {
                result.add(service);
                continue;
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if ("1".equals(serviceId)) {
                service.put("message", "您已永久开通好友服务");
                service.put("status", "1");
            } else if ("2".equals(serviceRemainingTime.getServiceId())) {
                if (serviceRemainingTime.getResidueDegree() == 0) {
                    String time = simpleDateFormat.format(DateUtil.stampToDate(serviceRemainingTime.getTimeRemaining().toString()));
                    if (serviceRemainingTime.getTimeRemaining() > System.currentTimeMillis() / 1000) {
                        service.put("message", time + "到期");
                        service.put("status", "1");
                    }
                } else {
                    if (serviceRemainingTime.getStorageTime() != 0) {
                        service.put("message", "次卡剩" + serviceRemainingTime.getResidueDegree().toString() + "次,月/年卡待使用");
                        service.put("status", "1");
                    } else {
                        service.put("message", "次卡剩" + serviceRemainingTime.getResidueDegree().toString() + "次");
                        service.put("status", "1");
                    }
                }
            } else if ("3".equals(serviceRemainingTime.getServiceId())) {
                String time = simpleDateFormat.format(DateUtil.stampToDate(serviceRemainingTime.getTimeRemaining().toString()));
                if (serviceRemainingTime.getTimeRemaining() != 0) {
                    if (serviceRemainingTime.getTimeRemaining() > System.currentTimeMillis() / 1000) {
                        service.put("message", time + "到期");
                        service.put("status", "1");
                    } else {
                        service.put("message", "已过期");
                    }
                }
            } else if ("4".equals(serviceRemainingTime.getServiceId())) {
                if (serviceRemainingTime.getResidueDegree() == 0) {
                    String time = simpleDateFormat.format(DateUtil.stampToDate(serviceRemainingTime.getTimeRemaining().toString()));
                    if (serviceRemainingTime.getTimeRemaining() > System.currentTimeMillis() / 1000) {
                        service.put("message", time + "到期");
                        service.put("status", "1");
                    } else if (serviceRemainingTime.getTimeRemaining() != 0) {
                        service.put("message", "已过期");
                    }
                } else {
                    if (serviceRemainingTime.getStorageTime() == 0) {
                        service.put("message", "次卡剩" + serviceRemainingTime.getResidueDegree().toString());
                        service.put("status", "1");
                    }
                }
            } else if ("5".equals(serviceRemainingTime.getServiceId())) {
                String time = simpleDateFormat.format(DateUtil.stampToDate(serviceRemainingTime.getTimeRemaining().toString()));
                if (serviceRemainingTime.getTimeRemaining() > System.currentTimeMillis() / 1000) {
                    service.put("message", time + "到期");
                    service.put("status", "1");
                } else if (serviceRemainingTime.getTimeRemaining() != 0) {
                    service.put("message", "已过期");
                }
            }
            result.add(service);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
    }

    @Override
    public Dto orderCancel(ReceivedTradeId receivedTradeId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedTradeId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        UserOrders userOrders = orderMapper.getUserOrder(receivedTradeId.getTradeId());
        if (!ObjectUtils.isEmpty(userOrders) && "0".equals(userOrders.getOrderStatus())) {
            userOrders.setOrderStatus("5");
            if (orderMapper.updateUserOrder(userOrders) > 0) {
                DiscountUser discountUser = orderMapper.getBindingDiscountCoupon(receivedTradeId.getTradeId());
                if (!ObjectUtils.isEmpty(discountUser)) {
                    if (orderMapper.setDiscountCouponOrderId(discountUser.getId(), "0", "0") > 0) {
                        return DtoUtil.getSuccessDto("订单已取消", 100000);
                    }
                }else {
                    return DtoUtil.getSuccessDto("订单已取消", 100000);
                }
            }
        }
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return DtoUtil.getSuccessDto("订单状态异常", 61023);
    }

    @Override
    public Dto addCreateLimit(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }

        return null;
    }

    /**
     * 为ServiceRemainingTime赋值
     *
     * @param userId        用户ID
     * @param serviceId     服务类型
     * @param residueDegree 剩余次数
     * @param timeRemaining 剩余时间
     * @return ServiceRemainingTime对象
     */
    private ServiceRemainingTime setServiceRemainingTime(String userId, String serviceId, Long residueDegree, Long timeRemaining, Long storageTime, Long timeCardDuration) {
        ServiceRemainingTime serviceRemainingTime = new ServiceRemainingTime();
        serviceRemainingTime.setUserId(userId);
        serviceRemainingTime.setServiceId(serviceId);
        serviceRemainingTime.setResidueDegree(residueDegree);
        serviceRemainingTime.setTimeRemaining(timeRemaining);
        serviceRemainingTime.setStorageTime(storageTime);
        serviceRemainingTime.setTimeCardDuration(timeCardDuration);
        return serviceRemainingTime;
    }

    /**
     * 返回false则为成功
     *
     * @param orderNo
     * @return
     */
    public boolean makeOrderSuccess(String orderNo) {
        UserOrders userOrders = orderMapper.getUserOrder(orderNo);
        if ("1".equals(userOrders.getOrderStatus())) {
            ServiceRemainingTime time = userServiceMapper.getServiceRemainingTime(userOrders.getUserId(), userOrders.getServiceId());
            if ("time".equals(userOrders.getServiceType())) {
                if (ObjectUtils.isEmpty(time)) {
                    if (userServiceMapper.addNewServiceRemainingTime(setServiceRemainingTime(userOrders.getUserId(), userOrders.getServiceId(), userOrders.getNumber(), 0L, 0L, 0L)) == 0) {
                        return false;
                    }
                } else {
                    Long residueDegree = time.getResidueDegree() + userOrders.getNumber();
                    time.setResidueDegree(residueDegree);
                    if (userServiceMapper.updateServiceRemainingTime(time) == 0) {
                        return false;
                    }
                }
            } else if ("month".equals(userOrders.getServiceType())) {
                if (ObjectUtils.isEmpty(time)) {
                    if (userServiceMapper.addNewServiceRemainingTime(setServiceRemainingTime(userOrders.getUserId(), userOrders.getServiceId(), 0L, userOrders.getNumber() * FinalValues.MONTH + System.currentTimeMillis() / 1000, 0L, 0L)) == 0) {
                        return false;
                    }
                } else {
                    if (time.getResidueDegree() == 0) {
                        Long timeRemaining = time.getTimeRemaining() + FinalValues.MONTH * userOrders.getNumber();
                        time.setTimeRemaining(timeRemaining);
                    } else {
                        Long storageTime = time.getStorageTime() + FinalValues.MONTH * userOrders.getNumber();
                        time.setStorageTime(storageTime);
                    }
                    if (userServiceMapper.updateServiceRemainingTime(time) == 0) {
                        return false;
                    }
                }
            } else if ("quarter".equals(userOrders.getServiceType())) {
                if (ObjectUtils.isEmpty(time)) {
                    if (userServiceMapper.addNewServiceRemainingTime(setServiceRemainingTime(userOrders.getUserId(), userOrders.getServiceId(), 0L, userOrders.getNumber() * FinalValues.QUARTER + System.currentTimeMillis() / 1000, 0L, 0L)) == 0) {
                        return false;
                    }
                } else {
                    if (time.getResidueDegree() == 0) {
                        Long timeRemaining = time.getTimeRemaining() + FinalValues.QUARTER * userOrders.getNumber();
                        time.setTimeRemaining(timeRemaining);
                    } else {
                        Long storageTime = time.getStorageTime() + FinalValues.QUARTER * userOrders.getNumber();
                        time.setStorageTime(storageTime);
                    }
                    if (userServiceMapper.updateServiceRemainingTime(time) == 0) {
                        return false;
                    }
                }
            } else if ("year".equals(userOrders.getServiceType())) {
                long reportStorageTime = 0L;
                if (ObjectUtils.isEmpty(time)) {
                    if ("3".equals(userOrders.getServiceId())) {
                        reportStorageTime = System.currentTimeMillis() / 1000;
                    }
                    if (userServiceMapper.addNewServiceRemainingTime(setServiceRemainingTime(userOrders.getUserId(), userOrders.getServiceId(), 0L, userOrders.getNumber() * FinalValues.YEAR + System.currentTimeMillis() / 1000, reportStorageTime, 0L)) == 0) {
                        return false;
                    }
                } else {
                    if (time.getResidueDegree() == 0) {
                        Long timeRemaining = time.getTimeRemaining() + FinalValues.YEAR * userOrders.getNumber();
                        time.setTimeRemaining(timeRemaining);
                        if ("3".equals(userOrders.getServiceId())) {
                            time.setStorageTime(System.currentTimeMillis() / 1000);
                        }
                    } else {
                        Long storageTime = time.getStorageTime() + FinalValues.YEAR * userOrders.getNumber();
                        time.setStorageTime(storageTime);
                    }
                    if (userServiceMapper.updateServiceRemainingTime(time) == 0) {
                        return false;
                    }
                }
            } else if ("perpetual".equals(userOrders.getServiceType())) {
                if (userServiceMapper.addNewServiceRemainingTime(setServiceRemainingTime(userOrders.getUserId(), userOrders.getServiceId(), 0L, 0L, 0L, 0L)) == 0) {
                    return false;
                }
            }else if ("addCreateLimit".equals(userOrders.getServiceType())){
                if (groupMapper.addCreateLimit(userOrders.getUserId(),"1") == 0){
                    return false;
                }
            }
            userOrders.setOrderStatus("2");
            return orderMapper.updateUserOrder(userOrders) > 0;
        }
        return false;
    }

    private String wxPaySignSuccess(String tradeNo,String transactionId){
        if (tradeNo.startsWith("sot")){
            StoreOfflineOrders offlineOrder = goodsMapper.getOfflineOrder(tradeNo);
            if (!ObjectUtils.isEmpty(offlineOrder)){
                if (offlineOrder.getOrderStatus() == 1) {
                    return "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
                }
                offlineOrder.setOrderStatus(2L);
                offlineOrder.setPayTime(System.currentTimeMillis());
                offlineOrder.setPayChannel("微信支付");
                offlineOrder.setOutTradeNo(transactionId);
                goodsMapper.updateOfflineOrder(offlineOrder);
                //支付成功增加商铺余额
                storeMapper.updWallet(offlineOrder.getPaymentAmount(),offlineOrder.getSourceStoreId());
                //订单成功后解析已卖出商品并将数量记录到销量表中
                //(因为要根据时间计算商品销量,所以该表中同一商铺下会有多个商品及对应数量)
                List<Map> goodsList = JSONObject.parseArray(goodsMapper.getTemStock(tradeNo),Map.class);
                for (Map goods : goodsList){
                    StoreSalesVolume storeSalesVolume = new StoreSalesVolume();
                    storeSalesVolume.setGoodsId(goodsMapper.getGoodsStockByGoodsBarCode(offlineOrder.getSourceStoreId().toString(),goods.get("goodsBarCode").toString()).getGoodsId().toString());
                    storeSalesVolume.setNum(Long.valueOf(goods.get("num").toString()));
                    storeSalesVolume.setStoreId(offlineOrder.getSourceStoreId().toString());
                    storeSalesVolume.setOrderNumber(tradeNo);
                    goodsMapper.addNewSalesVolume(storeSalesVolume);
                }
                logger.info("支付成功");
                logger.info("微信手机支付回调成功订单号:{}", tradeNo);
                return "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
            } else {
                logger.info("微信手机支付回调失败订单号:{}", tradeNo);
                return "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
            }
        }else {
            UserOrders userOrders = orderMapper.getUserOrder(tradeNo);
            if (!ObjectUtils.isEmpty(userOrders)) {
                // 注意特殊情况：订单已经退款，但收到了支付结果成功的通知，不应把商户的订单状态从退款改成支付成功
                // 注意特殊情况：微信服务端同样的通知可能会多次发送给商户系统，所以数据持久化之前需要检查是否已经处理过了，处理了直接返回成功标志
                //业务数据持久化
                if (userOrders.getOrderStatus().equals("1")) {
                    return "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
                }
                userOrders.setOrderStatus("1");
                userOrders.setPayTime(String.valueOf(System.currentTimeMillis() / 1000));
                userOrders.setPayChannel("微信支付");
                userOrders.setOutTradeNo(transactionId);
                //更新交易表中状态
                int returnResult = updateOrderStatusToPrepaid(userOrders);
                //将用户优惠券状态改为已使用
                orderMapper.updateDiscountStatus(tradeNo);
                if (returnResult == 0) {
                    return "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
                }
                if (!makeOrderSuccess(userOrders.getId())) {
                    orderMapper.updateOrderStatus(tradeNo, 4);
                    return "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
                }
                logger.info("支付成功");
                logger.info("微信手机支付回调成功订单号:{}", tradeNo);
                return "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
            } else {
                logger.info("微信手机支付回调失败订单号:{}", tradeNo);
                return "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
            }
        }
    }
}
