package com.modcreater.tmbeans.vo.goods;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-20
 * Time: 10:06
 */
@Data
public class RegisterGoods {

    private String id;

    private String userId;

    private String appType;


    private String storeId;
    /**
     * 商品品牌
     */
    private String goodsBrand;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品图片
     */
    private String goodsPicture;
    /**
     * 商品条形码
     */
    private String goodsBarCode;
    /**
     * 商品规格
     */
    private String goodsSpecifications;
    /**
     * 是否有过敏源0没有1有
     */
    private String goodsAllergen;
    /**
     * 商品单价
     */
    private Double goodsPrice;
    /**
     * 商品类型
     */
    private String goodsType;

    private String goodsUnit;
    /**
     * 初始库存
     */
    private Long goodsNum;
    /**
     * 来源0手动添加1进货
     */
    private String source;
    /**
     * 大单位
     */
    private String bigUnit;
    /**
     * 大单位换算到小单位的数量
     */
    private Long smallUnitNum;
    /**
     * 消耗品清单
     */
    private ConsumablesList[] consumablesLists;

}