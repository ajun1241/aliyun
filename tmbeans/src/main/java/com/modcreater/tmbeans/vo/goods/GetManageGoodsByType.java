package com.modcreater.tmbeans.vo.goods;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-10-28
 * Time: 17:11
 */
@Data
public class GetManageGoodsByType {

    /**
     * 用户Id
     */
    private String userId;
    /**
     * 商户Id
     */
    private String storeId;
    /**
     * 当前页码(第一页:1)
     */
    private Long pageNum;
    /**
     * 每页显示数据量
     */
    private Long pageSize;
    /**
     * 获取类型(forSale/soldOut)
     */
    private String getType;
    /**
     *
     */
    private String appType;



}
