package com.modcreater.tmbeans.vo.goods;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-11-14
 * Time: 10:17
 */
@Data
public class GetUpdatePromoteSalesGoodsList {

    private String userId;

    private String appType;

    private String storeId;

    private String promoteSalesId;

    private String goodsName;

    private Long pageNum;

    private Long pageSize;

}
