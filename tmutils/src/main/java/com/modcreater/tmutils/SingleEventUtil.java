package com.modcreater.tmutils;

import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.show.ShowSingleEvent;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-06
 * Time: 10:37
 */
public class SingleEventUtil {
    private static final Long VALUE2 = 100L;
    private static final Long VALUE4 = 10000L;
    private static final Long VALUE10 = 10000000000L;
    private static final Long VALUE11 = 100000000000L;

    private static final int LENGTH8 = 8;
    private static final int LENGTH10 = 11;
    private static final int LENGTH43 = 44;
    private static final int LENGTH50 = 51;

    /**
     * 创建一个SingleEvent对象并仅赋值day,month,year和userId
     * @param userId
     * @param dayEventId
     * @return
     */
    public static SingleEvent getSingleEvent(String userId,String dayEventId){
        StringBuilder date = new StringBuilder(dayEventId);
        SingleEvent singleEvent = new SingleEvent();
        singleEvent.setYear(Long.valueOf(date.substring(0, 4)));
        singleEvent.setMonth(Long.valueOf(date.substring(4, 6)));
        singleEvent.setDay(Long.valueOf(date.substring(6, 8)));
        singleEvent.setUserid(Long.valueOf(userId));
        return singleEvent;
    }

    /**
     * 将传进来的singleEvent文本转换成SingleEvent对象
     * @param singleEventText
     * @param clazz
     * @return
     */
    public static SingleEvent jsonToSingleEvent(String singleEventText ,Class<SingleEvent> clazz){
        return JSONObject.parseObject(singleEventText,clazz);
    }

    /**
     * 将传进来的SingleEvent集合转换成用来返回的ShowSingleEvent的集合
     * @param singleEventList
     * @return
     */
    public static List<ShowSingleEvent> getShowSingleEventList(List<SingleEvent> singleEventList){
        List<ShowSingleEvent> showSingleEventList = new ArrayList<>();
        for (SingleEvent singleEvent1 : singleEventList) {
            showSingleEventList.add(getShowSingleEvent(singleEvent1));
        }
        return showSingleEventList;
    }

    /**
     * 将传进来的SingleEvent转换成ShowSingleEvent
     * @param singleEvent1
     * @return
     */
    public static ShowSingleEvent getShowSingleEvent(SingleEvent singleEvent1) {
        if (StringUtils.hasText(singleEvent1.getRepeaTtime())) {
            ShowSingleEvent showSingleEvent = new ShowSingleEvent();
            showSingleEvent.setUserid(singleEvent1.getUserid());
            showSingleEvent.setEventid(singleEvent1.getEventid());
            showSingleEvent.setEventname(singleEvent1.getEventname());
            showSingleEvent.setStarttime(singleEvent1.getStarttime());
            showSingleEvent.setEndtime(singleEvent1.getEndtime());
            showSingleEvent.setFlag(singleEvent1.getFlag());
            showSingleEvent.setLevel(singleEvent1.getLevel());
            showSingleEvent.setPerson(singleEvent1.getPerson());
            showSingleEvent.setRemindTime(singleEvent1.getRemindTime());
            showSingleEvent.setRemarks(singleEvent1.getRemarks());
            showSingleEvent.setDay(singleEvent1.getDay());
            showSingleEvent.setMonth(singleEvent1.getMonth());
            showSingleEvent.setYear(singleEvent1.getYear());
            showSingleEvent.setType(singleEvent1.getType());
            showSingleEvent.setIsOverdue(singleEvent1.getIsOverdue());
            showSingleEvent.setAddress(singleEvent1.getAddress());
            showSingleEvent.setRepeaTtime(getRepeatTime(singleEvent1));
            return showSingleEvent;
        }
        return new ShowSingleEvent();
    }

    /**
     * 将SingleEvent中的repeaTtime字符串转换成ShowSingleEvent中的repeatTime数组
     * @param singleEvent
     * @return
     */
    public static Boolean[] getRepeatTime(SingleEvent singleEvent){
        Boolean[] booleans = new Boolean[7];
        String x=singleEvent.getRepeaTtime().substring(1,(singleEvent.getRepeaTtime().length()-1));
        String[] s = x.split(",");
        for (int i = 0; i <= 6; i++) {
            booleans[i] = "true".equals(s[i]);
        }
        return booleans;
    }

    /**
     * 对repeatTime字符串解析并判断是否是一个重复事件,如果是则返回true
     * @param repeatTime
     * @return
     */
    public static boolean isLoopEvent(String repeatTime){
        boolean b = false;
        String x=repeatTime.substring(1,(repeatTime.length()-1));
        for (String s : x.split(",")) {
            b = "true".equals(s);
            if (b){
                break;
            }
        }
        return b;
    }

    public static Dto isSingleEventStandard(SingleEvent singleEvent){
        if (singleEvent.getEventid() >= VALUE10){
            return DtoUtil.getFalseDto("eventId不规范",21010);
        }
        if (singleEvent.getUserid() >= VALUE11){
            return DtoUtil.getFalseDto("userId不规范",21010);
        }
        if (singleEvent.getEventname().length() >= LENGTH10){
            return DtoUtil.getFalseDto("eventName不规范",21010);
        }
        if (singleEvent.getAddress().length() >= LENGTH50){
            return DtoUtil.getFalseDto("address不规范",21010);
        }
        if (singleEvent.getPerson().length() >= LENGTH50){
            return DtoUtil.getFalseDto("Person不规范",21010);
        }
        if (singleEvent.getRemarks().length() >= LENGTH50){
            return DtoUtil.getFalseDto("Remarks不规范",21010);
        }
        if (singleEvent.getRepeaTtime().length() >= LENGTH43){
            return DtoUtil.getFalseDto("RepeaTtime不规范",21010);
        }
        return null;
    }
}
