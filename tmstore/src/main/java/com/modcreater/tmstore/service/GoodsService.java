package com.modcreater.tmstore.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.goods.RegisterGoods;
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
     * 根据类型查询商铺商品列表
     * @param goodsListVo
     * @param token
     * @return
     */
    Dto getGoodsList(GoodsListVo goodsListVo, String token);

    /**
     * 根据Id查询商品详情
      * @param goodsInfoVo
     * @param token
     * @return
     */
    Dto getGoodsInfo(GoodsInfoVo goodsInfoVo, String token);
}
