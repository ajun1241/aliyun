package com.modcreater.tmbeans.vo.goods;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-10-31
 * Time: 9:27
 */
@Data
public class DeleteGoods {

    private String userId;

    private String appType;

    private String storeId;

    private String[] goodsId;

}
