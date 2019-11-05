package com.modcreater.tmstore.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.goods.*;
import com.modcreater.tmbeans.vo.store.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-20
 * Time: 10:02
 */
public interface GoodsService {
    /**
     * 注册商品
     * @param registerGoods
     * @param token
     * @return
     */
    Dto registerGoods(RegisterGoods registerGoods, String token);

    /**
     * 获取我的库存列表
     * @param getGoodsStockList
     * @param token
     * @return
     */
    Dto getGoodsStockList(GetGoodsStockList getGoodsStockList, String token);

    /**
     * 根据类型查询商铺商品列表
     * @param goodsListVo
     * @param token
     * @return
     */
    Dto getGoodsList(GoodsListVo goodsListVo, String token);

    /**
     * 根据类型查询商铺商品列表（一次性查所有）
     * @param goodsListVo
     * @param token
     * @return
     */
    Dto getGoodsList2(GoodsListVo goodsListVo, String token);

    /**
     * 根据Id查询商品详情
      * @param goodsInfoVo
     * @param token
     * @return
     */
    Dto getGoodsInfo(GoodsInfoVo goodsInfoVo, String token);

    /**
     * 获取条形码内信息
     * @param barcode
     * @return
     */
    Dto getBarcodeInfo(String barcode);

    /**
     * 修改商品信息
     * @param updateGoods
     * @param token
     * @return
     */
    Dto updateGoodsInfo(UpdateGoods updateGoods, String token);

    /**
     * 获取修改价格信息
     * @param receivedGoodsId
     * @param token
     * @return
     */
    Dto getUpdatePriceInfo(ReceivedGoodsId receivedGoodsId, String token);

    /**
     * 修改商品价格
     * @param updateGoodsPrice
     * @param token
     * @return
     */
    Dto updateGoodsPrice(UpdateGoodsPrice updateGoodsPrice, String token);

    /**
     * 获取商品类型
     * @param receivedId
     * @param token
     * @return
     */
    Dto getGoodsTypes(ReceivedId receivedId, String token);

    /**
     * 收货 完成交易
     * @param claimGoodsVo
     * @param token
     * @return
     */
    Dto claimGoods(ClaimGoodsVo claimGoodsVo, String token);

    /**
     * 单位转换
     * @param conversionUnitVo
     * @param token
     * @return
     */
    Dto conversionUnit(ConversionUnitVo conversionUnitVo, String token);

    /**
     * 保存订单二维码信息
     * @param orderInfoVo
     * @param token
     * @return
     */
    Dto saveOrderInfo(OrderInfoVo orderInfoVo, String token);

    /**
     * 查询订单二维码信息
     * @param orderInfoVo
     * @param token
     * @return
     */
    Dto queryOrderQrInfo(OrderInfoVo orderInfoVo, String token);

    /**
     * 商品下架
     * @param goodsDownShelf
     * @param token
     * @return
     */
    Dto goodsDownShelf(GoodsDownShelf goodsDownShelf, String token);

    /**
     * 获取商品详细信息
     * @param receivedGoodsId
     * @param token
     * @return
     */
    Dto getUpdateGoodsInfo(ReceivedGoodsId receivedGoodsId, String token);

    /**
     * 获取商品对应消耗品
     * @param getGoodsConsumables
     * @param token
     * @return
     */
    Dto getGoodsConsumable(GetGoodsConsumables getGoodsConsumables, String token);

    /**
     * 删除商品对应消耗品
     * @param deleteGoodsConsumables
     * @param token
     * @return
     */
    Dto deleteGoodsConsumables(DeleteGoodsConsumables deleteGoodsConsumables, String token);

    /**
     * 获取修改消耗品信息
     * @param receivedConsumableId
     * @param token
     * @return
     */
    Dto getUpdateConsumableInfo(ReceivedConsumableId receivedConsumableId, String token);

    /**
     * 修改消耗品
     * @param updateConsumable
     * @param token
     * @return
     */
    Dto updateConsumable(UpdateConsumable updateConsumable, String token);

    /**
     * 查询商铺列表
     * @param getStoreListVo
     * @param token
     * @return
     */
    Dto getStoreList(GetStoreListVo getStoreListVo,String token);

    /**
     * 扫描条形码获取商品信息
     * @param getGoodsInfoVo
     * @param token
     * @return
     */
    Dto getGoodsInfoByBarCode(GetGoodsInfoVo getGoodsInfoVo,String token);

    /**
     * 线下交易生成订单
     * @param createOfflineOrderVo
     * @param token
     * @return
     */
    Dto createOfflineOrder(CreateOfflineOrderVo createOfflineOrderVo,String token);

    /**
     * 商家确认商品信息
     * @param orderInfoVo
     * @param token
     * @return
     */
    Dto checkGoodsList(OrderInfoVo orderInfoVo, String token);

    /**
     * 商家确认订单
     * @param checkOrderVo
     * @param token
     * @return
     */
    Dto checkOrder(CheckOrderVo checkOrderVo, String token);


    /**
     * 添加消耗品
     * @param addNewConsumable
     * @param token
     * @return
     */
    Dto addNewConsumable(AddNewConsumable addNewConsumable, String token);

    /**
     * 微信线下支付
     * @param receivedOrderNumber
     * @param token
     * @return
     */
    Dto wxOfflinePay(ReceivedOrderNumber receivedOrderNumber, String token);

    /**
     * 支付宝线下支付
     * @param receivedOrderNumber
     * @param token
     * @return
     */
    Dto aliOfflinePay(ReceivedOrderNumber receivedOrderNumber, String token);

    /**
     * 获取出货跟踪列表
     * @param getGoodsTracking
     * @param token
     * @return
     */
    Dto getGoodsTracking(GetGoodsTracking getGoodsTracking, String token);

    /**
     * 根据商店Id获取出货跟踪列表
     * @param getGoodsTrackingInStore
     * @param token
     * @return
     */
    Dto getGoodsTrackingInStore(GetGoodsTrackingInStore getGoodsTrackingInStore, String token);

    /**
     * 获取商品管理主界面信息
     * @param receivedStoreId
     * @param token
     * @return
     */
    Dto getManageGoods(ReceivedStoreId receivedStoreId, String token);

    /**
     * 根据类型查询管理商品列表
     * @param getManageGoodsByType
     * @param token
     * @return
     */
    Dto getManageGoodsByType(GetManageGoodsByType getManageGoodsByType, String token);

    /**
     * 根据商品类型分组查询商品列表
     * @param receivedStoreId
     * @param token
     * @return
     */
    Dto getManageGoodsGroupByGoodsType(ReceivedStoreId receivedStoreId, String token);

    /**
     * 根据商品类型查新商品列表
     * @param getManageGoodsWithGoodsType
     * @param token
     * @return
     */
    Dto getManageGoodsWithGoodsType(GetManageGoodsWithGoodsType getManageGoodsWithGoodsType, String token);

    /**
     * 根据查看类型获取商品价格列表
     * @param getManagePriceByType
     * @param token
     * @return
     */
    Dto getManagePriceByType(GetManagePriceByType getManagePriceByType, String token);

    /**
     * 删除商品
     * @param deleteGoods
     * @param token
     * @return
     */
    Dto deleteGoods(DeleteGoods deleteGoods, String token);

    /**
     * 商品折扣促销
     * @param goodsDiscountPromoteSales
     * @param token
     * @return
     */
    Dto goodsDiscountPromoteSales(GoodsDiscountPromoteSales goodsDiscountPromoteSales, String token);

    /**
     * 商品满减促销
     * @param goodsFullReductionPromoteSales
     * @param token
     * @return
     */
    Dto goodsFullReductionPromoteSales(GoodsFullReductionPromoteSales goodsFullReductionPromoteSales, String token);

    /**
     * 商品促销验证(验证商品是否已存在)
     * @param goodsPromoteSalesVerify
     * @param token
     * @return
     */
    Dto goodsPromoteSalesVerify(GoodsPromoteSalesVerify goodsPromoteSalesVerify, String token);
}
