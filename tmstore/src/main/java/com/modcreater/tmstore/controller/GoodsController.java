package com.modcreater.tmstore.controller;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.goods.RegisterGoods;
import com.modcreater.tmstore.config.annotation.Safety;
import com.modcreater.tmstore.service.GoodsService;
import io.swagger.annotations.ApiOperation;
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
 * @Date: 2019-09-20
 * Time: 10:01
 */
@RestController
@RequestMapping(value = "/goods/")
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    @Safety
    @RequestMapping(value = "registerGoods")
    @ApiOperation("注册商品")
    public Dto registerGoods(@RequestBody RegisterGoods registerGoods, HttpServletRequest request){
        return goodsService.registerGoods(registerGoods,request.getHeader("token"));
    }

}
