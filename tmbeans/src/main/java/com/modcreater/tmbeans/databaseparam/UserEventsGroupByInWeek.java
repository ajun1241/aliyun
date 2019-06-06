package com.modcreater.tmbeans.databaseparam;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-06
 * Time: 9:26
 */
@Data
public class UserEventsGroupByInWeek {

    private String userId;

    private String todayYear;
    private String todayMonth;
    private String todayDay;

    private String yesterdayYear;
    private String yesterdayMonth;
    private String yesterdayDay;

    private String thirdDayYear;
    private String thirdDayMonth;
    private String thirdDayDay;

    private String fourthDayYear;
    private String fourthDayMonth;
    private String fourthDayDay;

    private String fifthDayYear;
    private String fifthDayMonth;
    private String fifthDayDay;

    private String sixthDayYear;
    private String sixthDayMonth;
    private String sixthDayDay;

    private String seventhDayYear;
    private String seventhDayMonth;
    private String seventhDayDay;

}
