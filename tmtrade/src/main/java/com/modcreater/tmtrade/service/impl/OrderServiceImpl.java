package com.modcreater.tmtrade.service.impl;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.UserOrders;
import com.modcreater.tmbeans.vo.trade.ReceivedOrderInfo;
import com.modcreater.tmdao.mapper.OrderMapper;
import com.modcreater.tmtrade.service.OrderService;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
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
        userOrders.setPaymentAmount();
        userOrders.setCreateDate();
        userOrders.setPayTime();
        userOrders.setPayChannel();
        userOrders.setOutTradeNo();
        userOrders.setOrderStatus();
        userOrders.setRemark();
        return null;
    }
}
