package com.modcreater.tmbeans.vo.goods;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-29
 * Time: 15:38
 */
@Data
public class GetGoodsConsumables {

    private String userId;

    private String appType;

    private String storeId;

    private String goodsId;

    private String goodsName;

    private Long pageNum;

    private Long pageSize;

}
