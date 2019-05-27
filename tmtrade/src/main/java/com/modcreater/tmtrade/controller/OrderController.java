package com.modcreater.tmtrade.controller;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.UserOrders;
import com.modcreater.tmbeans.vo.trade.ReceivedOrderInfo;
import com.modcreater.tmdao.mapper.OrderMapper;
import com.modcreater.tmtrade.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-27
 * Time: 14:00
 */
@RestController
@RequestMapping(value = "/order/")
public class OrderController {

    @Resource
    private OrderService orderService;

    @PostMapping(value = "createneworder")
    public Dto createNewOrder(@RequestBody ReceivedOrderInfo receivedOrderInfo, HttpServletRequest request){
        return orderService.createNewOrder(receivedOrderInfo,request.getHeader("token"));
    }

}
