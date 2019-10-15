package com.modcreater.tmbeans.vo.goods;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-10-15
 * Time: 13:52
 */
@Data
public class GetGoodsTrackingInStore {

    private String userId;

    private String storeId;

    private String appType;

    private String targetStoreId;

}
