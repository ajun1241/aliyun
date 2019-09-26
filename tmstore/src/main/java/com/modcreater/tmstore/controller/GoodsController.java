package com.modcreater.tmstore.controller;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.goods.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmbeans.vo.store.GoodsInfoVo;
import com.modcreater.tmbeans.vo.store.GoodsListVo;
import com.modcreater.tmstore.config.annotation.Safety;
import com.modcreater.tmstore.service.GoodsService;
import com.modcreater.tmutils.DtoUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    @PostMapping(value = "registerGoods")
    @ApiOperation("注册商品")
    public Dto registerGoods(@RequestBody RegisterGoods registerGoods, HttpServletRequest request){
        return goodsService.registerGoods(registerGoods,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "getgoodsstocklist")
    @ApiOperation("获取我的商品库存列表()")
    public Dto getGoodsStockList(@RequestBody GetGoodsStockList getGoodsStockList, HttpServletRequest request){
        return goodsService.getGoodsStockList(getGoodsStockList,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "getgoodslist")
    @ApiOperation("查询商品列表")
    public Dto getGoodsList(@RequestBody GoodsListVo goodsListVo, HttpServletRequest request){
        return goodsService.getGoodsList(goodsListVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "getgoodsinfo")
    @ApiOperation("查询商品详情")
    public Dto getGoodsInfo(@RequestBody GoodsInfoVo goodsInfoVo, HttpServletRequest request){
        return goodsService.getGoodsInfo(goodsInfoVo,request.getHeader("token"));
    }

    @PostMapping("getbarcodeinfo")
    @ApiOperation("获取条形码内信息")
    public Dto getBarcodeInfo(@RequestBody String barcode){
        return goodsService.getBarcodeInfo(barcode);
    }

    @Safety
    @PostMapping(value = "updategoodsinfo")
    public Dto updateGoodsInfo(@RequestBody UpdateGoods updateGoods, HttpServletRequest request){
        return goodsService.updateGoodsInfo(updateGoods,request.getHeader("token"));
    }

    @PostMapping(value = "getupdatepriceinfo")
    @ApiOperation("获取修改价格信息")
    public Dto getUpdatePriceInfo(@RequestBody ReceivedGoodsId receivedGoodsId, HttpServletRequest request){
        return goodsService.getUpdatePriceInfo(receivedGoodsId,request.getHeader("token"));
    }

    @PostMapping(value = "udpategoodsprice")
    @ApiOperation("修改单价")
    public Dto updateGoodsPrice(@RequestBody UpdateGoodsPrice updateGoodsPrice,HttpServletRequest request){
        return goodsService.updateGoodsPrice(updateGoodsPrice,request.getHeader("token"));
    }

}
