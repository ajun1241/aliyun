package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.databaseparam.goods.AddNewGoodsPromoteSales;
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
     * @param status
     * @return
     */
    int updateGoodsUnitPrice(@Param("goodsId") String goodsId,@Param("goodsPrice") Double unitPrice,@Param("storeId") Long storeId,@Param("status") Object status);

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
     * 根据goodsCode查询订单
     * @param code
     * @return
     */
    StoreOfflineOrders getOfflineOrderByGoodsCode(String code);

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
     * @param goodsName
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<StorePurchaseRecords> getPurchaseRecordsByOrderNumber(@Param("orderNumber") String orderNumber,@Param("goodsName") String goodsName,
                                                               @Param("pageNum") Long pageNum,@Param("pageSize") Long pageSize);

    /**
     * 获取订单列表
     * @param sourceStoreId
     * @param targetStoreId
     * @return
     */
    List<StorePurchaseRecords> getCurrentOrders(@Param("sourceStoreId") String sourceStoreId,@Param("targetStoreId") String targetStoreId);

    /**
     * 根据订单号分组查询订单
     * @param storeId
     * @param storeName
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<StorePurchaseRecords> getOrderNumbersGroupByOrderNumber(@Param("storeId") String storeId,@Param("storeName") String storeName,
                                                   @Param("pageNum") Long pageNum,@Param("pageSize") Long pageSize);

    /**
     * 查询单个商品周销量
     * @param storeId
     * @param goodsId
     * @return
     */
    Long getGoodsSalesVolume(String storeId,String goodsId,String toDay,String targetDay);

    /**
     * 获取商品的库存数量
     * @param goodsId
     * @param storeId
     * @return
     */
    Long getGoodsStockNum(String goodsId, String storeId);

    /**
     * 获取出售中的商品的数量
     * @param storeId
     * @return
     */
    Long getForSaleGoodsNum(String storeId);

    /**
     * 获取已售尽商品的数量
     * @param storeId
     * @return
     */
    Long getSoldOutGoodsNum(String storeId);

    /**
     * 分页查询正在出售的商品
     * @param storeId
     * @param pageNum
     * @param pageSize
     * @param goodsName
     * @return
     */
    List<Map<String, Object>> getForSaleGoodsList(@Param("storeId") String storeId,@Param("pageNum") Long pageNum,
                                                  @Param("pageSize") Long pageSize,@Param("goodsName") String goodsName);

    /**
     * 分页查询已售空的商品
     * @param storeId
     * @param pageNum
     * @param pageSize
     * @param goodsName
     * @return
     */
    List<Map<String, Object>> getSoldOutGoodsList(@Param("storeId") String storeId,@Param("pageNum") Long pageNum,
                                                  @Param("pageSize") Long pageSize,@Param("goodsName") String goodsName);

    /**
     * 获取已有商品的类型Id
     * @param storeId
     * @return
     */
    List<Long> getMyGoodsTypes(String storeId);

    /**
     * 根据类型Id查询商品列表
     * @param storeId
     * @param typeId
     * @return
     */
    List<Map<String, Object>> getManageGoodsGroupByGoodsTypeId(@Param("storeId") String storeId,@Param("typeId") Long typeId);

    /**
     * 获取商品类型名称
     * @param typeId
     * @return
     */
    String getTypeName(Long typeId);

    /**
     * 获取已定价的商品信息
     * @param storeId
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<Map<String, Object>> getPricedGoodsList(@Param("storeId") String storeId,@Param("pageNum") Long pageNum,@Param("pageSize") Long pageSize);

    /**
     * 获取未定价的商品信息
     * @param storeId
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<Map<String, Object>> getNoPricingGoodsList(@Param("storeId") String storeId,@Param("pageNum") Long pageNum,@Param("pageSize") Long pageSize);

    /**
     * 删除商品
     * @param storeId
     * @param goodsId
     * @return
     */
    int deleteGoods(@Param("storeId") String storeId,@Param("goodsId") String goodsId);

    /**
     * 根据类型Id查询商品列表
     * @param storeId
     * @param typeId
     * @return
     */
    int getManageGoodsGroupByGoodsTypeIdNum(@Param("storeId") String storeId,@Param("typeId") Long typeId);

    /**
     * 验证商品优惠表中是否已存在即将参与促销的商品
     * @param goodsId
     * @param curTime
     * @param storeId
     * @return
     */
    int verifyGoodsExistInSGD(@Param("goodsId") String[] goodsId,@Param("curTime") Long curTime,@Param("storeId") String storeId);

    /**
     * 添加商品促销信息
     * @param addNewGoodsPromoteSales
     * @return
     */
    int addNewGoodsPromoteSales(AddNewGoodsPromoteSales addNewGoodsPromoteSales);

    /**
     * 添加商品促销信息
     * @param goodsId
     * @param value
     * @param bindingId
     * @param discountedType
     * @param startTime
     * @param endTime
     * @param storeId
     * @return
     */
    int addNewGoodsDiscountPromoteSales(@Param("goodsId") String[] goodsId,@Param("value") String value,
                                @Param("bindingId") String bindingId,@Param("discountedType") int discountedType,
                                @Param("startTime") Long startTime,@Param("endTime") Long endTime,@Param("storeId") String storeId);

    /**
     * 添加商品满减
     * @param bindingId
     * @param fullValue
     * @param disValue
     * @param startTime
     * @param endTime
     * @param storeId
     * @return
     */
    int addNewFullReduction(@Param("bindingId") String bindingId,@Param("fullValue") Double fullValue,
                            @Param("disValue") Double disValue,@Param("startTime") Long startTime,
                            @Param("endTime") Long endTime,@Param("storeId") String storeId);

    /**
     * 修改订单状态
     * @param orderNumber
     * @param status
     * @return
     */
    int updateOfflineOrderStatus(String orderNumber, int status);

    /**
     * 查询和传入goodsIds数组中相同的优惠信息
     * @param goodsIds
     * @param startTime
     * @param endTime
     * @param storeId
     * @param curTime
     * @return
     */
    int verGoodsPromoteSales(@Param("goodsIds") String[] goodsIds,@Param("startTime") Long startTime,
                                                  @Param("endTime") Long endTime,@Param("storeId") String storeId,
                                                  @Param("curTime") long curTime);

    /**
     * 查询和传入goodsIds数组中相同的优惠信息(除bindingId外的)
     * @param goodsIds
     * @param startTime
     * @param endTime
     * @param storeId
     * @param curTime
     * @param bindingId
     * @return
     */
    int verUpdateGoodsPromoteSales(@Param("goodsIds") String[] goodsIds,@Param("startTime") Long startTime,
                             @Param("endTime") Long endTime,@Param("storeId") String storeId,
                             @Param("curTime") long curTime,@Param("bindingId") String bindingId);

    /**
     * 验证商品折扣促销重复
     * @param goodsIds
     * @param startTime
     * @param endTime
     * @param storeId
     * @param curTime
     * @param type
     * @return
     */
    int verGoodsPromoteSalesRepetitive(@Param("goodsIds") String[] goodsIds,@Param("startTime") Long startTime,
                                               @Param("endTime") Long endTime,@Param("storeId") String storeId,
                                               @Param("curTime") long curTime,@Param("type") int type);

    /**
     * 验证商品折扣促销重复
     * @param goodsIds
     * @param startTime
     * @param endTime
     * @param storeId
     * @param curTime
     * @param type
     * @param bindingId
     * @return
     */
    int verUpdateGoodsPromoteSalesRepetitive(@Param("goodsIds") String[] goodsIds,@Param("startTime") Long startTime,
                                       @Param("endTime") Long endTime,@Param("storeId") String storeId,
                                       @Param("curTime") long curTime,@Param("type") int type,@Param("bindingId") String bindingId);

    /**
     * 获取商品折扣开始时间
     * @param storeId
     * @param curTime
     * @return
     */
    List<String> getGoodsPromoteSalesBindingIds(@Param("storeId") String storeId,@Param("curTime") long curTime);

    /**
     * 获取商品折扣信息
     * @param bindingId
     * @return
     */
    List<StoreGoodsDiscount> getGoodsPromoteSalesInfo(String bindingId);

    /**
     * 获取商品满减折扣信息
     * @param bindingId
     * @return
     */
    List<StoreGoodsFullReduction> getGoodsFullReduction(String bindingId);

    /**
     * 获取商品已过期的折扣信息
     * @param storeId
     * @param curTime
     * @return
     */
    List<String> getGoodsOverduePromoteSalesBindingIds(@Param("storeId") String storeId,@Param("curTime") long curTime);

    /**
     * 根据Id查询商品促销信息
     * @param promoteSalesId
     * @return
     */
    StoreGoodsDiscount getStoreGoodsDiscount(String promoteSalesId);

    /**
     * 根据Id删除商品促销活动
     * @param promoteSalesId
     * @return
     */
    int deleteGoodsPromoteSales(String promoteSalesId);

    /**
     * 删除满减促销
     * @param bindingId
     * @return
     */
    int deleteStoreGoodsFullReduction(Long bindingId);

    /**
     * 给同组促销商品添加绑定
     * @param bindingId
     * @param id
     * @return
     */
    int addBindingIdToGoodsDiscount(@Param("bindingId") String bindingId,@Param("id") String id);

    /**
     * 修改商品折扣表
     * @param updateGoodsPromoteSales
     * @return
     */
    int updateStoreGoodsDiscount(UpdateGoodsPromoteSales updateGoodsPromoteSales);

    /**
     * 查询修改商品促销商品列表
     * @param storeId
     * @param goodsName
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<ShowGetUpdatePromoteSalesGoodsList> getUpdatePromoteSalesGoodsList(@Param("storeId") String storeId,@Param("goodsName") String goodsName,
                                                            @Param("pageNum") Long pageNum,@Param("pageSize") Long pageSize);

    /**
     * 根据商品价格状态或获取商品数量
     * @param storeId
     * @param status
     * @return
     */
    Long getPricedGoodsNumByStatus(@Param("storeId") String storeId,@Param("status") int status);
}
