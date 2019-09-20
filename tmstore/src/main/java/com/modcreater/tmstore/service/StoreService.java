package com.modcreater.tmstore.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.*;
import com.modcreater.tmbeans.vo.store.ApproveInfoVo;

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
    Dto queryAccountInfo(ApproveInfoVo approveInfoVo,String token);

    /**
     * 上传商铺认证信息
     * @param approveInfoVo
     * @param token
     * @return
     */
    Dto uploadApproveInfo(ApproveInfoVo approveInfoVo,String token);

}
