package com.modcreater.tmbeans.vo.store;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/9/23 14:32
 */
@Data
public class GoodsListVo implements Serializable {
    private String appType;
    private String userId;
    private String storeId;
    private String goodsType;//1:优惠；2：热销
    private String goodsName;
    private String pageNumber;
    private String pageSize;
}
