package com.modcreater.tmbeans.show.goods;

import com.modcreater.tmbeans.vo.goods.ConsumablesList;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-28
 * Time: 16:29
 */
@Data
public class GoodsInfoToUpdate {
    private String goodsId;

    private String goodsName;

    private String goodsBrand;

    private String goodsPicture;

    private String goodsType;

    private String goodsStock;

    private String goodsUnit;

    private String goodsSpecifications;

    private String goodsFUnit;

    private String faUnitNum;

    private String goodsBarCode;

    private List<ShowConsumable> showConsumables;

    private String corGoodsId;

}
