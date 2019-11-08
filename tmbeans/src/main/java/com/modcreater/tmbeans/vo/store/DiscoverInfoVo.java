package com.modcreater.tmbeans.vo.store;

import lombok.Data;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/10/15 15:00
 */
@Data
public class DiscoverInfoVo {
    private String appType;
    private String userId;
    /**
     * 商铺种类编号
     */
    private String storeTypeId;
    /**
     * 商铺营业状态编号
     */
    private String storeStatusId;
    /**
     * 商铺筛选条件编号
     */
    private String screenTypeId;
    private String city;
    private Double longitude;
    private Double latitude;
}
