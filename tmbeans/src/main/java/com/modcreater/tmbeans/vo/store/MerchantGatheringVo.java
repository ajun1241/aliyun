package com.modcreater.tmbeans.vo.store;

import lombok.Data;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/11/5 16:09
 */
@Data
public class MerchantGatheringVo {
    private String appType;
    private String userId;
    /**
     * 用户展示的付款码
     */
    private String authCode;
    private String code;
}
