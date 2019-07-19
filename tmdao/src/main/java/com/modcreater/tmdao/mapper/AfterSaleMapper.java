package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.AfterSale;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/7/19 13:58
 */
public interface AfterSaleMapper {
    /**
     * 更换实名认证
     * @param afterSale
     * @return
     */
    int changeRealInfo(AfterSale afterSale);
}
