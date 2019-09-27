package com.modcreater.tmstore.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.goods.*;
import com.modcreater.tmbeans.vo.store.ClaimGoodsVo;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmbeans.vo.store.GoodsInfoVo;
import com.modcreater.tmbeans.vo.store.GoodsListVo;

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
     * @return
     */
    Dto getGoodsTypes();

    /**
     * 收货 完成交易
     * @param claimGoodsVo
     * @param token
     * @return
     */
    Dto claimGoods(ClaimGoodsVo claimGoodsVo, String token);

    /**
     * 商品下架
     * @param goodsDownShelf
     * @param token
     * @return
     */
    Dto goodsDownShelf(GoodsDownShelf goodsDownShelf, String token);
}
