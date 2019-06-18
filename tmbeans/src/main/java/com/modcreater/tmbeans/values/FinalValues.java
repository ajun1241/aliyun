package com.modcreater.tmbeans.values;

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
     * 线形图板块显示的周的数量
     */
    public static final Integer SEARCH_WEEK_NUM = 6;
    /**
     * 10分钟(秒)
     */
    public static final Long TIME_CARD_DURATION = 600L;
    /**
     * 一个月(单位:秒,计算用户支付用)
     */
    public static final Long MONTH = 2592000L;
    /**
     * 一年(单位:秒,计算用户支付用)
     */
    public static final Long YEAR = 31536000L;


    private Long getPayMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int month = calendar.get(Calendar.MONTH);
        return 1L;
    }

    private Long getPayYear() {
        return 1L;
    }

}
