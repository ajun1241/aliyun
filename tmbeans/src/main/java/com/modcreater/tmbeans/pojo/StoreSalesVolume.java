package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-10-14
 * Time: 10:13
 */
@Data
public class StoreSalesVolume {

    private String id;

    private String orderNumber;

    private String storeId;

    private String goodsId;

    private Long num;

    private Date createTime;

}
