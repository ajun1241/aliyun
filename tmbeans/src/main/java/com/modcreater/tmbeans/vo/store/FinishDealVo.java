package com.modcreater.tmbeans.vo.store;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/10/8 14:17
 */
@Data
public class FinishDealVo {

    private String userId;
    private String storeId;
    /**
     * goodsId
     * num
     */
    private List<Map<String,String>> goodsList;
}
