package com.modcreater.tmstore.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.store.ApproveInfoVo;
import com.modcreater.tmbeans.vo.store.CollectStoreVo;
import com.modcreater.tmbeans.vo.store.DiscoverInfoVo;
import com.modcreater.tmbeans.vo.store.SearchDiscoverVo;
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

}
