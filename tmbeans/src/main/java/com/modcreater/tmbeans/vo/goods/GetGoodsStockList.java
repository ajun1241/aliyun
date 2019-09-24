package com.modcreater.tmbeans.vo.goods;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-24
 * Time: 16:52
 */
@Data
public class GetGoodsStockList {
    /**
     * 用户Id
     */
    private String userId;
    /**
     * 商户Id
     */
    private String storeId;
    /**
     * 商品名称(已拼接好)
     */
    private String goodsName;
    /**
     * 商品类型
     */
    private String type;
    /**
     * 当前页码(第一页:1)
     */
    private Long pageNum;
    /**
     * 每页显示数据量
     */
    private Long pageSize;
    /**
     * 获取类型(stock\price)
     */
    private String getType;
    /**
     *
     */
    private String appType;

}
