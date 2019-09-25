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
}
