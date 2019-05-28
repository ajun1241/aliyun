package com.modcreater.tmtrade.service.impl;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.UserOrders;
import com.modcreater.tmbeans.vo.trade.ReceivedOrderInfo;
import com.modcreater.tmbeans.vo.trade.ReceivedUserIdTradeId;
import com.modcreater.tmdao.mapper.OrderMapper;
import com.modcreater.tmtrade.service.OrderService;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    public UserOrders getUserOrder(ReceivedUserIdTradeId receivedUserIdTradeId, String token) {
        if (!StringUtils.hasText(token)){
            return null;
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedUserIdTradeId.getUserId()))){
            return null;
        }
        return orderMapper.getUserOrder(receivedUserIdTradeId.getUserId(),receivedUserIdTradeId.getTradeId());
    }
}
