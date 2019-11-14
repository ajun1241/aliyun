package com.modcreater.tmstore.controller;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.goods.*;
import com.modcreater.tmbeans.vo.store.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
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
    @ApiOperation("获取我的商品库存列表")
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

    @Safety
    @PostMapping("getbarcodeinfo")
    @ApiOperation("获取条形码内信息")
    public Dto getBarcodeInfo(@RequestBody String barcode){
        return goodsService.getBarcodeInfo(barcode);
    }

    @Safety
    @PostMapping(value = "updategoodsinfo")
    @ApiOperation("修改商品信息")
    public Dto updateGoodsInfo(@RequestBody UpdateGoods updateGoods, HttpServletRequest request){
        return goodsService.updateGoodsInfo(updateGoods,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "saveorderinfo")
    @ApiOperation("保存订单二维码信息")
    public Dto saveOrderInfo(@RequestBody OrderInfoVo orderInfoVo, HttpServletRequest request){
        return goodsService.saveOrderInfo(orderInfoVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "queryorderqrinfo")
    @ApiOperation("查询订单二维码信息")
    public Dto queryOrderQrInfo(@RequestBody OrderInfoVo orderInfoVo, HttpServletRequest request){
        return goodsService.queryOrderQrInfo(orderInfoVo,request.getHeader("token"));
    }

    /*@Safety
    @PostMapping(value = "conversionunit")
    @ApiOperation("单位转换")
    public Dto conversionUnit(@RequestBody ConversionUnitVo conversionUnitVo, HttpServletRequest request){
        return goodsService.conversionUnit(conversionUnitVo,request.getHeader("token"));
    }*/

    @Safety
    @PostMapping(value = "claimgoods")
    @ApiOperation("收货 完成交易")
    public Dto claimGoods(@RequestBody ClaimGoodsVo claimGoodsVo, HttpServletRequest request){
        return goodsService.claimGoods(claimGoodsVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "getupdatepriceinfo")
    @ApiOperation("获取修改价格信息")
    public Dto getUpdatePriceInfo(@RequestBody ReceivedGoodsId receivedGoodsId, HttpServletRequest request){
        return goodsService.getUpdatePriceInfo(receivedGoodsId,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "udpategoodsprice")
    @ApiOperation("修改单价")
    public Dto updateGoodsPrice(@RequestBody UpdateGoodsPrice updateGoodsPrice,HttpServletRequest request){
        return goodsService.updateGoodsPrice(updateGoodsPrice,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "getgoodstypes")
    @ApiOperation("获取商品类型")
    public Dto getGoodsTypes(@RequestBody ReceivedId receivedId,HttpServletRequest request){
        return goodsService.getGoodsTypes(receivedId,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "goodsdownshelf")
    @ApiOperation("商品下架")
    public Dto goodsDownShelf(@RequestBody GoodsDownShelf goodsDownShelf,HttpServletRequest request){
        return goodsService.goodsDownShelf(goodsDownShelf,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "getupdategoodsinfo")
    @ApiOperation("获取我的商品详细信息")
    public Dto getUpdateGoodsInfo(@RequestBody ReceivedGoodsId receivedGoodsId,HttpServletRequest request){
        return goodsService.getUpdateGoodsInfo(receivedGoodsId,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "getgoodsconsumables")
    @ApiOperation("获取商品对应消耗品")
    public Dto getGoodsConsumables(@RequestBody GetGoodsConsumables getGoodsConsumables,HttpServletRequest request){
        return goodsService.getGoodsConsumable(getGoodsConsumables,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "deletegoodsconsumables")
    @ApiOperation("删除商品对应消耗品")
    public Dto deleteGoodsConsumables(@RequestBody DeleteGoodsConsumables deleteGoodsConsumables,HttpServletRequest request){
        return goodsService.deleteGoodsConsumables(deleteGoodsConsumables,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "getupdateconsumableinfo")
    @ApiOperation("获取修改消耗品信息")
    public Dto getUpdateConsumableInfo(@RequestBody ReceivedConsumableId receivedConsumableId,HttpServletRequest request){
        return goodsService.getUpdateConsumableInfo(receivedConsumableId,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "updateconsumable")
    @ApiOperation("修改消耗品信息")
    public Dto updateConsumable(@RequestBody UpdateConsumable updateConsumable, HttpServletRequest request){
        return goodsService.updateConsumable(updateConsumable,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "getstorelist")
    @ApiOperation("查询商店列表")
    public Dto getStoreList(@RequestBody GetStoreListVo getStoreListVo, HttpServletRequest request){
        return goodsService.getStoreList(getStoreListVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "getgoodsinfobybarcode")
    @ApiOperation("根据条码获取商品信息")
    public Dto getGoodsInfoByBarCode(@RequestBody GetGoodsInfoVo getGoodsInfoVo, HttpServletRequest request){
        return goodsService.getGoodsInfoByBarCode(getGoodsInfoVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "createofflineorder")
    @ApiOperation("线下交易生成订单")
    public Dto createOfflineOrder(@RequestBody CreateOfflineOrderVo createOfflineOrderVo, HttpServletRequest request){
        return goodsService.createOfflineOrder(createOfflineOrderVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "checkgoodslist")
    @ApiOperation("商家确认商品信息")
    public Dto checkGoodsList(@RequestBody OrderInfoVo orderInfoVo, HttpServletRequest request){
        return goodsService.checkGoodsList(orderInfoVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "checkorder")
    @ApiOperation("商家确认订单")
    public Dto checkOrder(@RequestBody CheckOrderVo checkOrderVo, HttpServletRequest request){
        return goodsService.checkOrder(checkOrderVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "merchantgathering")
    @ApiOperation("商家扫描付款码完成交易")
    public Dto merchantGathering(@RequestBody MerchantGatheringVo merchantGatheringVo, HttpServletRequest request){
        return goodsService.merchantGathering(merchantGatheringVo,request.getHeader("token"));
    }


    @Safety
    @PostMapping(value = "addnewconsumable")
    @ApiOperation("添加消耗品")
    public Dto addNewConsumable(@RequestBody AddNewConsumable addNewConsumable,HttpServletRequest request){
        return goodsService.addNewConsumable(addNewConsumable,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "wxofflinepay")
    @ApiOperation("线下微信支付")
    public Dto wxOfflinePay(@RequestBody ReceivedOrderNumber receivedOrderNumber,HttpServletRequest request){
        return goodsService.wxOfflinePay(receivedOrderNumber,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "aliofflinepay")
    @ApiOperation("线下支付宝支付")
    public Dto aliOfflinePay(@RequestBody ReceivedOrderNumber receivedOrderNumber,HttpServletRequest request){
        return goodsService.aliOfflinePay(receivedOrderNumber,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "getgoodstracking")
    @ApiOperation("出货跟踪")
    public Dto getGoodsTracking(@RequestBody GetGoodsTracking getGoodsTracking ,HttpServletRequest request){
        return goodsService.getGoodsTracking(getGoodsTracking,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "getgoodstrackinginstore")
    @ApiOperation("出货跟踪商店")
    public Dto getGoodsTrackingInStore(@RequestBody GetGoodsTrackingInStore getGoodsTrackingInStore, HttpServletRequest request){
        return goodsService.getGoodsTrackingInStore(getGoodsTrackingInStore,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "getmanagegoods")
    @ApiOperation("获取商品管理主界面")
    public Dto getManageGoods(@RequestBody ReceivedStoreId receivedStoreId,HttpServletRequest request){
        return goodsService.getManageGoods(receivedStoreId,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "getmanagegoodsbytype")
    @ApiOperation("根据类型获取商品管理列表")
    public Dto getManageGoodsByType(@RequestBody GetManageGoodsByType getManageGoodsByType,HttpServletRequest request){
        return goodsService.getManageGoodsByType(getManageGoodsByType,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "getmanagegoodsgroupbygoodstype")
    @ApiOperation("根据商品类型分类查询")
    public Dto getManageGoodsGroupByGoodsType(@RequestBody ReceivedStoreId receivedStoreId,HttpServletRequest request){
        return goodsService.getManageGoodsGroupByGoodsType(receivedStoreId,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "getmanagegoodswithgoodstype")
    @ApiOperation("根据商品类型获取商品列表")
    public Dto getManageGoodsWithGoodsType(@RequestBody GetManageGoodsWithGoodsType getManageGoodsWithGoodsType,HttpServletRequest request){
        return goodsService.getManageGoodsWithGoodsType(getManageGoodsWithGoodsType,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "getmanagepricebytype")
    @ApiOperation("根据查看类型获取商品价格列表")
    public Dto getManagePriceByType(@RequestBody GetManagePriceByType getManagePriceByType,HttpServletRequest request){
        return goodsService.getManagePriceByType(getManagePriceByType,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "deletegoods")
    @ApiOperation("删除商品")
    public Dto deleteGoods(@RequestBody DeleteGoods deleteGoods,HttpServletRequest request){
        return goodsService.deleteGoods(deleteGoods,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "goodspromotesalesverify")
    @ApiOperation("商品促销验证")
    public Dto goodsPromoteSalesVerify(@RequestBody GoodsPromoteSalesVerify goodsPromoteSalesVerify,HttpServletRequest request){
        /*return goodsService.goodsPromoteSalesVerify(goodsPromoteSalesVerify,request.getHeader("token"));*/
        return DtoUtil.getSuccessDto("请求成功",100000);
    }

    @Safety
    @PostMapping(value = "goodsdiscountpromotesales")
    @ApiOperation("商品折扣促销")
    public Dto goodsDiscountPromoteSales(@RequestBody GoodsDiscountPromoteSales goodsDiscountPromoteSales, HttpServletRequest request){
        return goodsService.goodsDiscountPromoteSales(goodsDiscountPromoteSales,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "goodsfullreductionpromotesales")
    @ApiOperation("商品满减促销")
    public Dto goodsFullReductionPromoteSales(@RequestBody GoodsFullReductionPromoteSales goodsFullReductionPromoteSales, HttpServletRequest request){
        return goodsService.goodsFullReductionPromoteSales(goodsFullReductionPromoteSales,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "showgoodspromotesales")
    @ApiOperation("展示商品促销")
    public Dto showGoodsPromoteSales(@RequestBody ReceivedStoreId receivedStoreId,HttpServletRequest request){
        return goodsService.showGoodsPromoteSales(receivedStoreId,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "showalloverduepromotesales")
    @ApiOperation("展示所有已过期的促销")
    public Dto showAllOverduePromoteSales(@RequestBody ReceivedStoreId receivedStoreId,HttpServletRequest request){
        return goodsService.showAllOverduePromoteSales(receivedStoreId,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "deletegoodspromotesales")
    @ApiOperation("删除商品促销活动")
    public Dto deleteGoodsPromoteSales(@RequestBody DeleteGoodsPromoteSales deleteGoodsPromoteSales,HttpServletRequest request){
        return goodsService.deleteGoodsPromoteSales(deleteGoodsPromoteSales,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "getupdategoodspromotesales")
    @ApiOperation("获取修改商品折扣信息")
    public Dto getUpdateGoodsPromoteSales(@RequestBody GetUpdateGoodsPromoteSales getUpdateGoodsPromoteSales,HttpServletRequest request){
        return goodsService.getUpdateGoodsPromoteSales(getUpdateGoodsPromoteSales,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "updategoodspromotesales")
    @ApiOperation("修改商品折扣信息")
    public Dto updateGoodsPromoteSales(@RequestBody UpdateGoodsPromoteSales updateGoodsPromoteSales,HttpServletRequest request){
        return goodsService.updateGoodsPromoteSales(updateGoodsPromoteSales,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "getupdatepromotesalesgoodslist")
    @ApiOperation("获取修改商品折扣的商品列表")
    public Dto getUpdatePromoteSalesGoodsList(@RequestBody GetUpdatePromoteSalesGoodsList getUpdatePromoteSalesGoodsList ,HttpServletRequest request){
        return goodsService.getUpdatePromoteSalesGoodsList(getUpdatePromoteSalesGoodsList,request.getHeader("token"));
    }
}
