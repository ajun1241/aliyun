package com.modcreater.tmbeans.vo.store;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/9/26 14:40
 */
@Data
public class ClaimGoodsVo implements Serializable {
    private String appType;
    private String userId;
    private String targetStoreId;
    private String sourceStoreId;
    /**
     * 商品Id goodsId，数量goodsCount
     */
    private List<Map<String,String>> sourceGoods;
    private String transactionPrice;

}
