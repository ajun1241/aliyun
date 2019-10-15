package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.*;
import com.modcreater.tmbeans.show.goods.*;
import com.modcreater.tmbeans.vo.goods.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
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
     * @param storeId
     * @return
     */
    int updateGoodsStock(@Param("goodsId") String goodsId,@Param("goodsStock") Long goodsNum,
                         @Param("goodsBarCode") String goodsBarcode,@Param("storeId") String storeId);

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
     * @param storeId
     * @return
     */
    int updateGoodsUnitPrice(@Param("goodsId") String goodsId,@Param("goodsPrice") Double unitPrice,@Param("storeId") Long storeId);

    /**
     * 修改商品状态
     * @param goodsId
     * @param status
     * @param storeId
     * @return
     */
    int updateGoodsStatus(@Param("goodsId") String goodsId,@Param("status") int status,@Param("storeId") String storeId);

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
     * @param storeId
     * @return
     */
    StoreGoodsStock getGoodsStock(@Param("goodsId") String goodsId,@Param("storeId") String storeId);

    /**
     * 判断商品库存
     * @param goodsId
     * @param storeId
     * @return
     */
    StoreGoodsStock queryGoodsStock(String goodsId, String storeId);

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
     * 按商品条形码获取商品库存信息
     * @param storeId
     * @param goodsBarCode
     * @return
     */
    StoreGoodsStock getGoodsStockByGoodsBarCode(String storeId, String goodsBarCode);

    /**
     * 添加商品库存信息
     * @param storeGoodsStock
     * @return
     */
    int insertStoreGoodsStock(StoreGoodsStock storeGoodsStock);

    /**
     * 修改商品库存
     * @param storeGoodsStock
     * @return
     */
    int updGoodsStock(StoreGoodsStock storeGoodsStock);

    /**
     * 根据商品Id查询商品详情(修改商品信息)
     * @param goodsId
     * @param storeId
     * @return
     */
    GoodsInfoToUpdate getGoodsInfoToUpdate(@Param("goodsId") String goodsId,@Param("storeId") Long storeId);

    /**
     * 获取商品对应的消耗品
     * @param goodsId
     * @param goodsName
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<ShowConsumable> getGoodsConsumablesList(@Param("goodsId") String goodsId,@Param("goodsName") String goodsName,
                                                 @Param("pageNum") Long pageNum,@Param("pageSize") Long pageSize);

    /**
     * 根据条形码修改商品库存
     * @param storeGoodsStock
     * @return
     */
    int updateGoodsStockByBarCode(StoreGoodsStock storeGoodsStock);

    /**
     * 保存二维码
     * @param storeQrCode
     * @return
     */
    int saveQrCode(StoreQrCode storeQrCode);

    /**
     * 查询二维码内容
     * @param code
     * @return
     */
    String queryQrCodeContent(String code);

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

    /**
     * 生成线下交易订单
     * @param storeOfflineOrders
     * @return
     */
    int saveStoreOfflineOrders(StoreOfflineOrders storeOfflineOrders);

    /**
     * 根据单号查询订单
     * @param tradeNo
     * @return
     */
    StoreOfflineOrders getOfflineOrder(String tradeNo);

    /**
     * 修改订单信息
     * @param offlineOrder
     * @return
     */
    int updateOfflineOrder(StoreOfflineOrders offlineOrder);

    /**
     * 获取线下交易商品清单
     * @param goodsListId
     * @return
     */
    String getCodeContent(String goodsListId);

    /**
     * 获取商品下的所有消耗品
     * @param goodsId
     * @return
     */
    List<StoreGoodsConsumable> getGoodsAllConsumablesList(String goodsId);

    /**
     * 修改库存数量
     * @param goodsId
     * @param resNum
     * @param storeId
     * @return
     */
    int updateGoodsStockNum(@Param("goodsId") String goodsId,@Param("stockNum") long resNum,@Param("storeId") String storeId);

    /**
     * 添加
     * @param temStock
     * @return
     */
    int addNewTemStock(String temStock,String offlineOrderNum);

    /**
     * 获取超时订单
     * @param seconds
     * @return
     */
    List<StoreOfflineOrders> getTimeOutOrders(@Param("seconds") Long seconds);

    /**
     * 修改超时订单状态
     * @param seconds
     * @param orderNum
     * @return
     */
    int makeOrderFailed(@Param("seconds")Long seconds,@Param("orderNum") String orderNum);

    /**
     * 查询临时库存表
     * @param orderNumber
     * @return
     */
    String getTemStock(String orderNumber);

    /**
     * 恢复商品库存
     * @param storeId
     * @param goodsBarCode
     * @param num
     * @return
     */
    int resumeStock(@Param("storeId") String storeId,@Param("goodsBarCode") String goodsBarCode,@Param("num") Object num);

    /**
     * 查询与该商店有交易来往的店铺Id
     * @param storeId
     * @param storeName
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<String> getTradedStoreIds(@Param("storeId") String storeId,@Param("storeName") String storeName,
                                   @Param("pageNum") Long pageNum,@Param("pageSize") Long pageSize);

    /**
     * 根据条件查询订单内的商品列表
     * @param sourceStoreId
     * @param targetStoreId
     * @param condition
     * @return
     */
    List<StorePurchaseRecords> getOrderGoodsList(@Param("sourceStoreId") String sourceStoreId,@Param("targetStoreId") String targetStoreId,@Param("condition") String condition);

    /**
     * 获取商店最新的订单
     * @param orderNumber
     * @param goodsName
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<StorePurchaseRecords> getCurrentOrderGoodsList(@Param("orderNumber") String orderNumber,@Param("goodsName") String goodsName,
                                                        @Param("pageNum") Long pageNum,@Param("pageSize") Long pageSize);

    /**
     * 获取商店最新的订单
     * @param sourceStoreId
     * @param targetStoreId
     * @return
     */
    StorePurchaseRecords getCurrentOrder(@Param("sourceStoreId") String sourceStoreId,@Param("targetStoreId") String targetStoreId);

    /**
     * 获取订单列表内改商店进货的商品Id
     * @param storeId
     * @return
     */
    List<Map<String,Object>> getOrderGoodsIds(String storeId);

    /**
     * 添加商品销量
     * @param storeSalesVolume
     * @return
     */
    int addNewSalesVolume(StoreSalesVolume storeSalesVolume);

    /**
     * 根据时间获取销量
     * @param goodsId
     * @param time
     * @return
     */
    Map getSalesVolumeByCreateTime(@Param("goodsId") String goodsId,@Param("time") Date time);

    /**
     * 根据商店Id和商品Id获取两个商店第一次交易该商品的时间
     * @param sourceStoreId
     * @param targetStoreId
     * @param goodsId
     * @return
     */
    Date getGoodsFirstPurchaseTime(@Param("sourceStoreId") String sourceStoreId,@Param("targetStoreId") String targetStoreId,@Param("goodsId") Long goodsId);

    /**
     * 根据订单号查询出货单
     * @param orderNumber
     * @return
     */
    StorePurchaseRecords getPurchaseRecordsByOrderNumber(String orderNumber);

    /**
     * 获取订单列表
     * @param sourceStoreId
     * @param targetStoreId
     * @return
     */
    List<StorePurchaseRecords> getCurrentOrders(@Param("sourceStoreId") String sourceStoreId,@Param("targetStoreId") String targetStoreId);
}
