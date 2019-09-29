package com.modcreater.tmbeans.vo.goods;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-29
 * Time: 9:45
 */
@Data
public class DeleteGoodsConsumables {

    private String userId;

    private String appType;

    private String[] consumableIds;

}
