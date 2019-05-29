package com.modcreater.tmtrade.service.impl;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.UserOrders;
import com.modcreater.tmbeans.vo.trade.ReceivedOrderInfo;
import com.modcreater.tmbeans.vo.trade.ReceivedUserIdTradeId;
import com.modcreater.tmbeans.vo.trade.ReceivedVerifyInfo;
import com.modcreater.tmdao.mapper.OrderMapper;
import com.modcreater.tmtrade.service.OrderService;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

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
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Dto createNewOrder(ReceivedOrderInfo receivedOrderInfo, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("操作失败,token未获取到",21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedOrderInfo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        UserOrders userOrders = new UserOrders();
        userOrders.setUserId(receivedOrderInfo.getUserId());
        userOrders.setServiceId(receivedOrderInfo.getServiceId());
        userOrders.setOrderTitle(receivedOrderInfo.getOrderTitle());
        double amount = orderMapper.getPaymentAmount(receivedOrderInfo.getServiceId(),receivedOrderInfo.getOrderType());
        if (amount != 0 && receivedOrderInfo.getPaymentAmount() - (amount) != 0){
            return DtoUtil.getFalseDto("订单金额错误",60001);
        }
        userOrders.setPaymentAmount(amount);
        userOrders.setCreateDate(System.currentTimeMillis()/1000);
        userOrders.setRemark(receivedOrderInfo.getUserRemark());
        if (orderMapper.addNewOrder(userOrders) > 0){
            return DtoUtil.getSuccessDto("订单生成成功,请及时支付",100000);
        }
        return DtoUtil.getFalseDto("订单生成失败",60002);
    }

    @Override
    public UserOrders getUserOrderById(String tradeId) {
        if (StringUtils.hasText(tradeId)){
            return orderMapper.getUserOrder(tradeId);
        }
        return null;
    }

    @Override
    public int updateOrderStatusToPrepaid(UserOrders userOrders) {
        if (!ObjectUtils.isEmpty(userOrders)){
            return orderMapper.updateUserOrder(userOrders);
        }
        return 0;
    }

    @Override
    public Dto payInfoVerify(ReceivedVerifyInfo receivedVerifyInfo, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("操作失败,token未获取到",21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedVerifyInfo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        boolean tradeStatus = StringUtils.hasText(receivedVerifyInfo.getId());
        boolean serviceIdStatus = StringUtils.hasText(receivedVerifyInfo.getServiceId());
        boolean orderTitleStatus = StringUtils.hasText(receivedVerifyInfo.getOrderTitle());
        boolean paymentAmountStatus = receivedVerifyInfo.getPaymentAmount() != 0;
        boolean payChannelStatus = StringUtils.hasText(receivedVerifyInfo.getPayChannel());
        boolean outTradeNoStatus = StringUtils.hasText(receivedVerifyInfo.getOutTradeNo());
        if (tradeStatus && serviceIdStatus && orderTitleStatus && paymentAmountStatus && payChannelStatus && outTradeNoStatus){
            UserOrders userOrders = orderMapper.getUserOrder(receivedVerifyInfo.getId());
            if (!ObjectUtils.isEmpty(userOrders)){
                tradeStatus = userOrders.getId().equals(receivedVerifyInfo.getId());
                serviceIdStatus = userOrders.getServiceId().equals(receivedVerifyInfo.getServiceId());
                orderTitleStatus = userOrders.getOrderTitle().equals(receivedVerifyInfo.getOrderTitle());
                paymentAmountStatus = userOrders.getPaymentAmount().equals(userOrders.getPaymentAmount());
                payChannelStatus = userOrders.getPayChannel().equals(receivedVerifyInfo.getPayChannel());
                outTradeNoStatus = userOrders.getOutTradeNo().equals(receivedVerifyInfo.getOutTradeNo());
                if (tradeStatus && serviceIdStatus && orderTitleStatus && paymentAmountStatus && payChannelStatus && outTradeNoStatus){
                    return DtoUtil.getSuccessDto("订单支付成功",100000);
                }
            }
        }
        return DtoUtil.getFalseDto("订单支付失败",60003);
    }
}
