package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.StoreGoods;
import com.modcreater.tmbeans.pojo.StoreGoodsConsumable;
import com.modcreater.tmbeans.pojo.StoreGoodsType;
import com.modcreater.tmbeans.vo.goods.ConsumablesList;
import com.modcreater.tmbeans.vo.goods.RegisterGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

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

    /**
     * 根据条件查询商品列表
     * @param storeId
     * @param goodsName
     * @param goodsType
     * @param pageIndex
     * @param pageSize
     * @return
     */
    List<Map<String, String>> getGoodsList(String storeId, String goodsName, String goodsType, int pageIndex, int pageSize);

    /**
     * 添加商品库存
     * @param id
     * @param goodsNum
     * @param source
     * @return
     */
    Long addNewGoodsStock(@Param("goodsId") String id, @Param("stockNum") Long goodsNum, @Param("goodsSource") int source);

    /**
     * 添加商品消耗品清单
     * @param storeGoodsConsumable
     * @return
     */
    Long addNewGoodsConsumable(StoreGoodsConsumable storeGoodsConsumable);

    /**
     * 查询商品详情
     * @param goodsId
     * @return
     */
    StoreGoods getGoodsInfo(String goodsId);

    /**
     * 查询商品类型表
     * @return
     */
    List<StoreGoodsType> getGoodsTypeList();

}
