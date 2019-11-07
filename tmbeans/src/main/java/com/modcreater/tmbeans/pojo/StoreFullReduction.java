package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-11-07
 * Time: 13:55
 */
@Data
public class StoreFullReduction {

    private Long id;

    private Long storeId;

    private Double fullValue;

    private Double disValue;

    private Long startTime;

    private Long endTime;

}
