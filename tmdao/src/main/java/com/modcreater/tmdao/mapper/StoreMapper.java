package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.StoreAttestation;

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
}
