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
public class GetUserEventsGroupByPriority {
    /**
     * 事件类型
     */
    private Long priority;
    /**
     * 该事件的数量
     */
    private Long num;

}
