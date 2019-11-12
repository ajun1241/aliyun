package com.modcreater.tmstore.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.goods.ReceivedStoreId;
import com.modcreater.tmbeans.vo.store.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;

/**
 * @Author: AJun
 */
public interface StoreService {

    /**
     * 查询认证页面信息
     * @param approveInfoVo
     * @param token
     * @return
     */
    Dto queryAccountInfo(ApproveInfoVo approveInfoVo, String token);

    /**
     * 上传商铺认证信息
     * @param approveInfoVo
     * @param token
     * @return
     */
    Dto uploadApproveInfo(ApproveInfoVo approveInfoVo, String token);

    /**
     * 查询商铺信息
     * @param receivedId
     * @param token
     * @return
     */
    Dto queryStoreInfo(ReceivedId receivedId,String token);

    /**
     * 进入发现主页(条件筛选)
     * @param discoverInfoVo
     * @param token
     * @return
     */
    Dto discoverInfo(DiscoverInfoVo discoverInfoVo, String token);

    /**
     * 热销产品
     * @param searchDiscoverVo
     * @param token
     * @return
     */
    Dto hotProducts(SearchDiscoverVo searchDiscoverVo, String token);

    /**
     * 附近好店
     * @param searchDiscoverVo
     * @param token
     * @return
     */
    Dto nearByShop(SearchDiscoverVo searchDiscoverVo, String token);

    /**
     * 排序方式（搜索商品页面）
     * @param receivedId
     * @param token
     * @return
     */
    Dto getSortType(ReceivedId receivedId, String token);

    /**
     * 筛选方式（搜索商品页面）
     * @param receivedId
     * @param token
     * @return
     */
    Dto getScreenType(ReceivedId receivedId, String token);

    /**
     * 收藏店铺
     * @param collectStoreVo
     * @param token
     * @return
     */
    Dto collectStore(CollectStoreVo collectStoreVo, String token);

    /**
     * 移出收藏
     * @param collectStoreVo
     * @param token
     * @return
     */
    Dto removeCollectStore(CollectStoreVo collectStoreVo, String token);

    /**
     * 查询收藏列表
     * @param collectStoreVo
     * @param token
     * @return
     */
    Dto getCollectStoreList(CollectStoreVo collectStoreVo, String token);

    /**
     * 查询商铺分类
     * @param receivedId
     * @param token
     * @return
     */
    Dto getStoreTypeList(ReceivedId receivedId,String token);

    /**
     * 查询商铺营业状态
     * @param receivedId
     * @param token
     * @return
     */
    Dto getStoreStatusList(ReceivedId receivedId,String token);

    /**
     * 查询商铺筛选条件
     * @param receivedId
     * @param token
     * @return
     */
    Dto getStoreScreenList(ReceivedId receivedId,String token);

    /**
     * 店铺满减促销
     * @param storeFullReductionPromoteSales
     * @param token
     * @return
     */
    Dto storeFullReductionPromoteSales(StoreFullReductionPromoteSales storeFullReductionPromoteSales, String token);

    /**
     * 商铺折扣促销
     * @param storeDiscountPromoteSales
     * @param token
     * @return
     */
    Dto storeDiscountPromoteSales(StoreDiscountPromoteSales storeDiscountPromoteSales, String token);

    /**
     * 商铺促销验证(是否正在做促销)
     * @param receivedStoreId
     * @param token
     * @return
     */
    Dto storePromoteSalesVerify(ReceivedStoreId receivedStoreId, String token);

    /**
     * 展示商铺促销
     * @param receivedStoreId
     * @param token
     * @return
     */
    Dto showStorePromoteSales(ReceivedStoreId receivedStoreId, String token);

    /**
     * 删除店铺促销
     * @param deleteStorePromoteSales
     * @param token
     * @return
     */
    Dto deletePromoteSales(DeleteStorePromoteSales deleteStorePromoteSales, String token);

    /**
     * 获取修改商铺促销活动
     * @param getUpdateStorePromoteSales
     * @param token
     * @return
     */
    Dto getUpdateStorePromoteSales(GetUpdateStorePromoteSales getUpdateStorePromoteSales, String token);

    /**
     * 修改商铺促销活动
     * @param updateStorePromoteSales
     * @param token
     * @return
     */
    Dto updateStorePromoteSales(UpdateStorePromoteSales updateStorePromoteSales, String token);
}
