package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.*;
import com.modcreater.tmbeans.show.goods.*;
import com.modcreater.tmbeans.vo.goods.*;
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
    List<Map<String,Object>> getGoodsList(String storeId, String goodsName, String goodsTypeId, int pageIndex, int pageSize);

    /**
     * 添加商品库存
     * @param id
     * @param goodsNum
     * @param goodsStatus
     * @param goodsBarCode
     * @return
     */
    Long addNewGoodsStock(@Param("goodsId") String id,@Param("storeId") String storeId,
                          @Param("stockNum") Long goodsNum, @Param("goodsStatus") String goodsStatus,
                          @Param("goodsBarCode") String goodsBarCode);

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
     * @param storeId
     * @return
     */
    List<Map<String,Object>> getGoodsTypeList(String storeId);

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
     * @param goodsBarcode
     * @return
     */
    int updateGoodsStock(@Param("goodsId") String goodsId,@Param("goodsStock") Long goodsNum,@Param("goodsBarCode") String goodsBarcode);

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

    /**
     * 获取消耗商品
     * @param getGoodsStockList
     * @return
     */
    List<ShowConsumableGoods> getConsumableGoods(GetGoodsStockList getGoodsStockList);

    /**
     * 获取商品单位
     * @param userId
     * @return
     */
    List<Map<String,Object>> getGoodsUnit(String userId);

    /**
     * 获取商品关系
     * @param corGoodsId
     * @return
     */
    int getCorRelation(String corGoodsId);

    /**
     * 扣减商品库存
     * @param sourceStoreId
     * @param sourceGoods
     * @return
     */
    int deductionStock(String sourceStoreId, List<Map<String, String>> sourceGoods);


    /**
     * 获取转换商品列表
     * @param getGoodsStockList
     * @param corIds
     * @return
     */
    List<ShowConsumableGoods> getCorGoods(@Param("getGoodsStockList") GetGoodsStockList getGoodsStockList,@Param("corIds")String corIds);

    /**
     * 获取具有绑定关系的所有商品Id
     * @return
     */
    List<String> getCorGoodsId();

    /**
     * 绑定商品转换关系
     * @param id
     * @param corGoodsId
     * @return
     */
    int bindingGoods(@Param("id") String id,@Param("corId") String corGoodsId);

    /**
     * 通过获取查询结果数量判断条形码是否已被商家录入过
     * @param storeId
     * @param goodsBarCode
     * @return
     */
    int isBarCodeExists(@Param("storeId") String storeId, @Param("goodsBarCode") String goodsBarCode);

    /**
     * 查询商品库存信息
     * @param goodsId
     * @return
     */
    StoreGoodsStock getGoodsStock(String goodsId);

    /**
     * 判断商品库存
     * @param goodsId
     * @param storeId
     * @return
     */
    Long queryGoodsStock(String goodsId, String storeId);

    /**
     * 获取子商品信息
     * @param goodsId
     * @return
     */
    StoreGoodsCorrelation getSonGoodsInfo(String goodsId);

    /**
     * 获取父商品信息
     * @param goodsId
     * @return
     */
    StoreGoodsCorrelation getParentGoodsInfo(String goodsId);

    /**
     * 根据商品Id查询商品详情(修改商品信息)
     * @param goodsId
     * @return
     */
    GoodsInfoToUpdate getGoodsInfoToUpdate(String goodsId);

    /**
     * 获取商品对应的消耗品
     * @param goodsId
     * @return
     */
    List<ShowConsumable> getGoodsConsumablesList(String goodsId);

    /**
     * 删除商品对应消耗品
     * @param consumableId
     * @return
     */
    int deleteGoodsConsumable(String consumableId);

    /**
     * 获取修改消耗品信息
     * @param consumableId
     * @return
     */
    ShowUpdateConsumableInfo getUpdateConsumableInfo(String consumableId);

    /**
     * 修改消耗品
     * @param updateConsumable
     * @return
     */
    int updateConsumable(UpdateConsumable updateConsumable);

    /**
     * 修改商品绑定关系
     * @param goodsParentId
     * @param goodsSonId
     * @return
     */
    int updateCorRelation(@Param("goodsParentId") String goodsParentId,@Param("goodsSonId") String goodsSonId);

    /**
     * 删除商品绑定关系
     * @param goodsParentId
     * @return
     */
    int deleteCorRelation(String goodsParentId);
}
