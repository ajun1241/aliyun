package com.modcreater.tmutils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    //日期转时间戳
    public static String dateToStamp(Date date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String res=simpleDateFormat.format(date);
        Date time = simpleDateFormat.parse(res);
        long ts = (time.getTime())/1000;
        res = String.valueOf(ts);
        return res;
    }
    //时间戳转日期
    public static Date stampToDate(String s){
        long lt = new Long(s);
        Date date = new Date(lt);
        date.setTime(lt*1000);
        System.out.println(date);
        return date;
    }

    /**
     * 时间戳转周
     * @param s
     * @return
     */
    public static int stringToWeek(String s){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        int week = calendar.get(Calendar.DAY_OF_WEEK)-1;
        if (week == 0){
            week = 7;
        }
        return week;
    }
}
