package com.modcreater.tmstore.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.goods.RegisterGoods;

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
}
