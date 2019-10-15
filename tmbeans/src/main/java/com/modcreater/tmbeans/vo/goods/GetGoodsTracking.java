package com.modcreater.tmbeans.vo.goods;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-10-15
 * Time: 16:50
 */
@Data
public class GetGoodsTracking {

    private String userId;

    private String appType;

    private String storeId;

    private Long pageNum;

    private Long pageSize;

    private String storeName;

}
