package com.modcreater.tmbeans.vo.goods;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-10-29
 * Time: 13:49
 */
@Data
public class GetManagePriceByType {

    private String userId;

    private String appType;

    private String storeId;
    /**
     * priced/noPricing
     */
    private String getType;

    private Long pageNum;

    private Long pageSize;

}
