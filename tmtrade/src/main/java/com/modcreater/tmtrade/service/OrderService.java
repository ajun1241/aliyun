package com.modcreater.tmtrade.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.trade.ReceivedOrderInfo;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-27
 * Time: 14:04
 */
@Service
public interface OrderService {

    /**
     * 生成新的订单
     * @param receivedOrderInfo
     * @param token
     * @return
     */
    Dto createNewOrder(ReceivedOrderInfo receivedOrderInfo, String token);
}
