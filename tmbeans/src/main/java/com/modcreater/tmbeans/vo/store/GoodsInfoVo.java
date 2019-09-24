package com.modcreater.tmbeans.vo.store;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/9/24 10:20
 */
@Data
public class GoodsInfoVo implements Serializable {
    private String appType;
    private String userId;
    private String goodsId;
}
