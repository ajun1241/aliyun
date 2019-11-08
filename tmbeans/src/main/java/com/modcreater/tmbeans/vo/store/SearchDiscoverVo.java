package com.modcreater.tmbeans.vo.store;

import lombok.Data;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/10/17 9:40
 */
@Data
public class SearchDiscoverVo {
    private String appType;
    private String userId;
    private String goodsKeyWords;
    private String storeKeyWords;
    /**
     * 筛选类型（1、库存数量；2、距离最近；3、销量最多；4、收藏最多）
     */
    private String screenType;
    /**
     * 排序类型（     3  酒水冲调
     *      4  粮油调味
     *      5  休闲零食
     *      6  美妆个护
     *      7  居家清洁
     *      8  母婴用品
     *      9  百货商品
     *     10  烘焙速食
     *     11  牛奶乳品
     *     12  肉蛋水产
     *     13  蔬菜菌菇
     *     14  时令水果
     *     15  卫生纸巾
     *     16  其他 ）
     */
    private String sortType;
    /**
     * 活动类型
     */
    private String activityType;
    private Double longitude;
    private Double latitude;
}
