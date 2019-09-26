package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.StoreGoods;
import com.modcreater.tmbeans.pojo.StoreGoodsConsumable;
import com.modcreater.tmbeans.show.goods.ShowGoodsPriceInfo;
import com.modcreater.tmbeans.show.goods.ShowGoodsStockInfo;
import com.modcreater.tmbeans.pojo.StoreGoodsType;
import com.modcreater.tmbeans.vo.goods.ConsumablesList;
import com.modcreater.tmbeans.vo.goods.GetGoodsStockList;
import com.modcreater.tmbeans.vo.goods.RegisterGoods;
import com.modcreater.tmbeans.vo.goods.UpdateGoods;
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
     * @param goodsTypeId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    List<StoreGoods> getGoodsList(String storeId, String goodsName, String goodsTypeId, int pageIndex, int pageSize);

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
     * 获取商铺老板数量(判断身份)
     * @param userId
     * @param storeId
     * @return
     */
    Long getStoreMaster(@Param("userId") String userId,@Param("storeId") String storeId);

    /**
     * 获取商铺库存列表
     * @param getGoodsStockList
     * @return
     */
    List<ShowGoodsStockInfo> getGoodsStockList(GetGoodsStockList getGoodsStockList);

    /**
     * 获取商铺价格列表
     * @param getGoodsStockList
     * @return
     */
    List<ShowGoodsPriceInfo> getGoodsPriceList(GetGoodsStockList getGoodsStockList);

    /**
     * 查询商品类型表
     * @return
     */
    List<StoreGoodsType> getGoodsTypeList();

    /**
     * 修改商品基本信息
     * @param updateGoods
     * @return
     */
    int updateGoods(UpdateGoods updateGoods);

    /**
     * 修改商品库存
     * @param goodsId
     * @param goodsNum
     * @return
     */
    int updateGoodsStock(@Param("goodsId") String goodsId,@Param("goodsStock") Long goodsNum);

    /**
     * 清除商品的所有消耗品
     * @param goodsId
     * @return
     */
    int cleanConsumablesList(String goodsId);

    /**
     * 修改商品单价
     * @param goodsId
     * @param unitPrice
     * @return
     */
    int updateGoodsUnitPrice(@Param("goodsId") String goodsId,@Param("goodsPrice") Double unitPrice);

    /**
     * 修改商品状态
     * @param goodsId
     * @param status
     * @return
     */
    int updateGoodsStatus(@Param("goodsId") String goodsId,@Param("status") int status);

    /**
     * 获取商品全部
     * @return
     */
    List<Map<String,String>> getGoodsAllTypeList();
}
