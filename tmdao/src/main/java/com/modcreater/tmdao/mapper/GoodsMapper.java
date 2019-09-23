package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.vo.goods.RegisterGoods;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-20
 * Time: 16:41
 */
@Mapper
public interface GoodsMapper {

    /**
     * 添加新的商品
     * @param registerGoods
     * @return
     */
    Long addNewGoods(RegisterGoods registerGoods);
}
