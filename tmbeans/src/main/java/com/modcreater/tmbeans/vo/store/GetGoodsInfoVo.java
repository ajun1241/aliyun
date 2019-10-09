package com.modcreater.tmbeans.vo.store;

import lombok.Data;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/10/8 10:21
 */
@Data
public class GetGoodsInfoVo {
    private String userId;
    private String appType;
    private String goodsBarCode;
    private String storeId;
}
