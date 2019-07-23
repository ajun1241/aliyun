package com.modcreater.tmbeans.values;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-14
 * Time: 18:32
 */
public class FinalValues {

    /**
     * 事件的类型,一次按0：学习；1：工作；2：商务；3：休闲；4：家庭；5：节日；6：假期；7：其他排列
     */
    public static final String[] TYPE = {"a", "b", "c", "d", "e", "f", "g", "h"};
    /**
     * 2-5数字越大优先级越高(2：不紧迫也不重要；3：紧迫但不重要；4：重要又不紧迫；5：重要又紧迫)
     */
    public static final String[] PRIORITY = {"a","b","c","d"};
    /**
     * Mon Tue Wed Thur Fri Sat Sun(周一到周日)
     */
    public static final String[] WEEKDAY_NAME = {"mon","tue","wed","thur","fri","sat","sun"};
    /**
     * 线形图板块显示的周的数量
     */
    public static final Integer SEARCH_WEEK_NUM = 6;
    /**
     * 标准的一个月的天数
     */
    public static final Integer STANDARD_MONTH_DAY = 30;
    /**
     * 标准的一年的天数
     */
    public static final Integer STANDARD_YEAR_DAY = 365;
    /**
     * 10分钟(秒)
     */
    public static final Long TIME_CARD_DURATION = 600L;
    /**
     * 一个月(单位:秒,计算用户支付用)
     */
    public static Long MONTH = getPayTime(1);
    /**
     * 一年(单位:秒,计算用户支付用)
     */
    public static Long YEAR = getPayTime(12);


    private static Long getPayTime(int monthNum) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        long today = calendar.getTimeInMillis()/1000;
        if (monthNum == 1){
            calendar.add(Calendar.MONTH,1);
        }else {
            calendar.add(Calendar.YEAR,1);
        }

        long nextDay = calendar.getTimeInMillis()/1000;
        int day = (int)(nextDay - today)/86400;
        if (monthNum == 1){
            if (day < STANDARD_MONTH_DAY){
                day = 30;
            }
        }
        calendar.setTime(date);
        calendar.add(Calendar.DATE,day);
        return calendar.getTimeInMillis()/1000 - System.currentTimeMillis()/1000;
    }

}
