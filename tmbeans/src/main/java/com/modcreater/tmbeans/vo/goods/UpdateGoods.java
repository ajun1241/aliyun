package com.modcreater.tmbeans.vo.goods;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-25
 * Time: 16:38
 */
@Data
public class UpdateGoods {

    private String goodsId;

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
     * 商品类型
     */
    private String goodsTypeId;

    private String goodsUnit;
    /**
     * 初始库存
     */
    private Long goodsNum;
    /**
     * 父单位
     */
    private String goodsFUnit;
    /**
     * 大单位换算到小单位的数量
     */
    private Long faUnitNum;
    /**
     * 转换商品Id
     */
    private String corGoodsId;

}
