package com.modcreater.tmdao.mapper;

import com.alipay.api.domain.GoodsInfo;
import com.modcreater.tmbeans.pojo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/9/19 16:08
 */
public interface StoreMapper {

    /**
     * 上传商铺信息
     * @param storeAttestation
     * @return
     */
    int insertStoreAttestation(StoreAttestation storeAttestation);

    /**
     * 根据商铺Id查询商铺信息
     * @param storeId
     * @return
     */
    StoreInfo getStoreInfo(String storeId);

    /**
     * 查询商铺认证状态
     * @param userId
     * @return
     */
    StoreAttestation getDisposeStatus(String userId);

    /**
     * 根据认证信息查询商铺详情
     * @param storeAttestationId
     * @return
     */
    StoreInfo getStoreInfoByAttestationId(Long storeAttestationId);

    /**
     * 保存交易记录
     * @param sourceGoods
     * @param sourceStoreId
     * @param targetStoreId
     * @param transactionPrice
     * @param orderNumber
     * @param status
     * @return
     */
    int saveTradingRecord(List<Map<String, String>> sourceGoods, String sourceStoreId, String targetStoreId, String transactionPrice, String orderNumber, int status);

    /**
     * 根据条码查询商品信息
     * @param goodsBarCode
     * @return
     */
    Map<String,Object> getStoreInfoByBarCode(String goodsBarCode,String storeId);

    /**
     * 查询商铺列表
     * @return
     */
    List<StoreInfo> getStoreList();

    /**
     * 修改商铺余额
     * @param paymentAmount
     * @param storeId
     * @return
     */
    int updWallet(Double paymentAmount, Long storeId);

    /**
     * 根据用户Id查询商户Id
     * @param userId
     * @return
     */
    Long getStoreIdByUserId(String userId);

    /**
     * 查询所有商品规格
     * @param goodsKeyWords
     * @param screenType
     * @return
     */
    List<StoreGoods> getGoodsAllType(String goodsKeyWords,String screenType);

    /**
     * 查询包含此商品的店铺
     * @param goodsId
     * @return
     */
    List<Map<String,String>> getStoreListByGoods(Long goodsId);

    /**
     * 收藏店铺
     * @param userId
     * @param storeId
     * @return
     */
    int collectStore(String userId, String storeId);

    /**
     * 移除收藏
     * @param userId
     * @param storeId
     * @return
     */
    int deleteCollectStore(String userId, String storeId);

    /**
     * 查询收藏店铺列表
     * @param userId
     * @return
     */
    List<StoreInfo> getCollectStoreList(String userId);

    /**
     * 查询商铺认证状态（0：未收藏；1：已收藏）
     * @param userId
     * @param storeId
     * @return
     */
    int getStoreCollectStatus(String userId, Long storeId);

    /**
     * 查询商铺收藏量
     * @param storeId
     * @return
     */
    int getStoreCollectNum(Long storeId);

    /**
     * 按条件获取商店列表
     * @param storeTypeId
     * @param storeStatusId
     * @return
     */
    List<StoreInfo> getStoreListByCondition(String storeTypeId, String storeStatusId);

    /**
     * 通过搜索条件获取​​商店列表
     * @param goodsKeyWords
     * @param screenType
     * @return
     */
    List<Map<String, String>> getStoreListBySearch(String goodsKeyWords, String screenType);

    /**
     * 通过搜索获取​​商品清单
     * @param goodsKeyWords
     * @param screenType
     * @param storeId
     * @return
     */
    List<StoreGoods> getGoodsListBySearch(String goodsKeyWords, String screenType, String storeId);

    /**
     * 查询商铺周销量
     * @param storeId
     * @param toDay
     * @param targetDay
     * @return
     */
    Long getStoreWeekSalesVolume(String storeId, String toDay, String targetDay);

    /**
     * 添加新的商铺满减促销
     * @param storeId
     * @param fullValue
     * @param disValue
     * @param startTime
     * @param endTime
     * @param share
     * @return
     */
    int addNewStoreFullReduction(@Param("storeId") String storeId, @Param("fullValue") Double fullValue, @Param("disValue") Double disValue,
                                 @Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("share") String share);

    /**
     * 添加新的商铺打折促销
     * @param storeId
     * @param value
     * @param startTime
     * @param endTime
     * @return
     */
    int addNewStoreDiscountPromoteSales(@Param("storeId") String storeId,@Param("value") Double value,
                                        @Param("startTime") Long startTime,@Param("endTime") Long endTime);

    /**
     * 查看商家是否正在做促销
     * @param storeId
     * @param curTime
     * @return
     */
    int verifyStoreExistInSFR(@Param("storeId") String storeId,@Param("curTime") Long curTime);

    /**
     * 验证商店促销时间冲突
     * @param startTime
     * @param endTime
     * @param storeId
     * @param curTime
     * @return
     */
    int verStorePromoteSales(@Param("startTime") Long startTime,@Param("endTime") Long endTime,
                             @Param("storeId") String storeId,@Param("curTime") long curTime);
}
