package com.modcreater.tmstore.controller;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.show.goods.ShowUpdateConsumableInfo;
import com.modcreater.tmbeans.vo.goods.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmbeans.vo.store.GoodsInfoVo;
import com.modcreater.tmbeans.vo.store.GoodsListVo;
import com.modcreater.tmstore.config.annotation.Safety;
import com.modcreater.tmstore.service.GoodsService;
import io.swagger.annotations.ApiOperation;
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
 * @Date: 2019-09-20
 * Time: 10:01
 */
@RestController
@RequestMapping(value = "/goods/")
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    @Safety
    @PostMapping(value = "registergoods")
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
    @PostMapping(value = "getgoodslist2")
    @ApiOperation("查询全部商品列表")
    public Dto getGoodsList2(@RequestBody GoodsListVo goodsListVo, HttpServletRequest request){
        return goodsService.getGoodsList2(goodsListVo,request.getHeader("token"));
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

    @PostMapping(value = "getgoodstypes")
    @ApiOperation("获取商品类型")
    public Dto getGoodsTypes(@RequestBody ReceivedId receivedId,HttpServletRequest request){
        return goodsService.getGoodsTypes(receivedId,request.getHeader("token"));
    }


    @PostMapping(value = "goodsdownshelf")
    @ApiOperation("商品下架")
    public Dto goodsDownShelf(@RequestBody GoodsDownShelf goodsDownShelf,HttpServletRequest request){
        return goodsService.goodsDownShelf(goodsDownShelf,request.getHeader("token"));
    }

    @PostMapping(value = "getupdategoodsinfo")
    @ApiOperation("获取我的商品详细信息")
    public Dto getUpdateGoodsInfo(@RequestBody ReceivedGoodsId receivedGoodsId,HttpServletRequest request){
        return goodsService.getUpdateGoodsInfo(receivedGoodsId,request.getHeader("token"));
    }

    @PostMapping(value = "getgoodsconsumables")
    @ApiOperation("获取商品对应消耗品")
    public Dto getGoodsConsumables(@RequestBody ReceivedGoodsId receivedGoodsId,HttpServletRequest request){
        return goodsService.getGoodsConsumable(receivedGoodsId,request.getHeader("token"));
    }

    @PostMapping(value = "deletegoodsconsumables")
    @ApiOperation("删除商品对应消耗品")
    public Dto deleteGoodsConsumables(@RequestBody DeleteGoodsConsumables deleteGoodsConsumables,HttpServletRequest request){
        return goodsService.deleteGoodsConsumables(deleteGoodsConsumables,request.getHeader("token"));
    }

    @PostMapping(value = "getupdateconsumableinfo")
    @ApiOperation("修改消耗品信息")
    public Dto getUpdateConsumableInfo(@RequestBody ReceivedConsumableId receivedConsumableId,HttpServletRequest request){
        return goodsService.getUpdateConsumableInfo(receivedConsumableId,request.getHeader("token"));
    }

    @PostMapping(value = "updateconsumable")
    @ApiOperation("获取修改消耗品信息")
    public Dto updateConsumable(@RequestBody UpdateConsumable updateConsumable, HttpServletRequest request){
        return goodsService.updateConsumable(updateConsumable,request.getHeader("token"));
    }
}
