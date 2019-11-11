package com.modcreater.tmutils;

import com.modcreater.tmbeans.utils.NaturalWeek;
import org.springframework.util.StringUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {
    //日期转时间戳
    public static String dateToStamp(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String res = simpleDateFormat.format(date);
        Date time = null;
        try {
            time = simpleDateFormat.parse(res);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ts = (time.getTime()) / 1000;
        res = String.valueOf(ts);
        return res;
    }

    //时间戳转日期
    public static Date stampToDate(String s) {
        long lt = new Long(s);
        Date date = new Date(lt);
        date.setTime(lt * 1000);
        System.out.println(date);
        return date;
    }

    /**
     * 20190620格式的时间转换为周几
     *
     * @param day(20190620)
     * @return
     */
    public static int stringToWeek(String day) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        if (StringUtils.hasText(day)) {
            try {
                date = simpleDateFormat.parse(day);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        calendar.setTime(date);
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (week == 0) {
            week = 7;
        }
        return week;
    }

    /**
     * 获取当前日期的前或后的某一天的日期
     *
     * @param fob 例:-1为返回前一天
     * @return
     */
    public static String getDay(int fob) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime((simpleDateFormat).parse(simpleDateFormat.format(new Date())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.DATE, fob);
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * 获取当前日期的前或后的某一天的日期
     *
     * @param fob  例:-1为返回前一天
     * @param date 开始日期
     * @return
     */
    public static String getDay(int fob, String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        try {
            if (date.length() != 8) {
                date = simpleDateFormat.format(new Date());
            }
            calendar.setTime((simpleDateFormat).parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.DATE, fob);
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * 获取自然周的开始及结束
     *
     * @param num 前几周(不能小于1)
     * @return
     */
    public static List<NaturalWeek> getLastWeekOfNatural(int num) {
        List<NaturalWeek> naturalWeeks = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        int toWeek = stringToWeek(simpleDateFormat.format(new Date()));
        int fobStart = -(toWeek - 1 + num * 7);
        System.out.println(fobStart);
        for (int i = 1; i <= 7; i++) {
            NaturalWeek naturalWeek = new NaturalWeek();
            StringBuilder whichNaturalWeekStartDay = new StringBuilder(getDay(fobStart));
            System.out.println("前第" + num + "个自然周的周" + i + ":" + whichNaturalWeekStartDay.toString());
            naturalWeek.setYear(whichNaturalWeekStartDay.substring(0, 4));
            naturalWeek.setMonth(whichNaturalWeekStartDay.substring(4, 6));
            naturalWeek.setDay(whichNaturalWeekStartDay.substring(6));
            naturalWeeks.add(naturalWeek);
            fobStart += 1;
        }
        return naturalWeeks;
    }

    /**
     * 获取当前分钟数(00:00到当前时间所有分钟数的总和)
     *
     * @return
     */
    public static int getCurrentMinutes() {
        return Integer.valueOf(new SimpleDateFormat("HH").format(new Date())) * 60 + Integer.valueOf(new SimpleDateFormat("mm").format(new Date()));
    }

    /**
     * 获取当前年
     * @return "2019"
     */
    public static String getCurrentYear(){
        StringBuffer stringBuffer = new StringBuffer(getDay(0));
        return stringBuffer.substring(0,4);
    }

    /**
     * 获取当前月
     * @return "01"
     */
    public static String getCurrentMonth(){
        StringBuffer stringBuffer = new StringBuffer(getDay(0));
        return stringBuffer.substring(4,6);
    }

    /**
     * 获取当前日
     * @return "01"
     */
    public static String getCurrentDay(){
        StringBuffer stringBuffer = new StringBuffer(getDay(0));
        return stringBuffer.substring(6);
    }

    /**
     * 将传入的时间戳
     * @param stamp
     * @return
     */
    public static String stampToDefinedFormat(Long stamp,String format){
        Date date = new Date();
        date.setTime(stamp * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }
}
