package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.StoreAttestation;
import com.modcreater.tmbeans.pojo.StoreInfo;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/9/19 16:08
 */
public interface StoreMapper {

    /**
     * 上传实名信息
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
}
