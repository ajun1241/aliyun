package com.modcreater.tmbeans.databaseresult;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-20
 * Time: 14:55
 */
@Data
public class GetUserEventsGroupByType {
    /**
     * 事件类型
     */
    private Long type;
    /**
     * 该事件的数量
     */
    private Long num;
    /**
     * 用时
     */
    private Long totalMinutes;

}
