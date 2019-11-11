package com.modcreater.tmbeans.show.store;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-11-11
 * Time: 10:44
 */
@Data
public class ShowPromoteSalesInfo {
    /**
     * 促销信息Id
     */
    private Long promoteSalesId;
    /**
     * 已选商品数量信息
     */
    private String selectedInfo = "";
    /**
     * 促销信息
     */
    private String disInfo = "";
    /**
     * 开始时间
     */
    private String startTime = "";
    /**
     * 结束时间
     */
    private String endTime = "";
    /**
     * 类型(1:折扣;2:满减)
     */
    private String type = "";
    /**
     * 状态
     */
    private String status = "";

}
