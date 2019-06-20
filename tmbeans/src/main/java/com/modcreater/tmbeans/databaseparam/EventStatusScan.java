package com.modcreater.tmbeans.databaseparam;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-04
 * Time: 17:31
 */
@Data
public class EventStatusScan {

    private Long lastYear;

    private Long lastMonth;

    private Long yesterday;

    private Long thisYear;

    private Long thisMonth;

    private Long today;

    private Long time;

}
