package com.modcreater.tmutils;

import com.modcreater.tmbeans.pojo.SingleEvent;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-06
 * Time: 10:37
 */
public class SingleEventUtil {
    /**
     * 创建一个SingleEvent对象并根据参数对象类型是否为SingleEvent子类判断是否为SingleEvent赋值
     * @param object
     * @return
     */
    public static SingleEvent getSingleEvent(Object object) {
            SingleEvent singleEvent = new SingleEvent();
            try {
                singleEvent.setEventid((Long) object.getClass().getMethod("getEventid").invoke(object));
                singleEvent.setUserid((Long) object.getClass().getMethod("getUserid").invoke(object));
                singleEvent.setEventname((String) object.getClass().getMethod("getEventname").invoke(object));
                singleEvent.setStarttime((String) object.getClass().getMethod("getStarttime").invoke(object));
                singleEvent.setEndtime((String) object.getClass().getMethod("getEndtime").invoke(object));
                singleEvent.setAddress((String) object.getClass().getMethod("getAddress").invoke(object));
                singleEvent.setLevel((Long) object.getClass().getMethod("getLevel").invoke(object));
                singleEvent.setFlag((Long) object.getClass().getMethod("getFlag").invoke(object));
                singleEvent.setPerson((String) object.getClass().getMethod("getPerson").invoke(object));
                singleEvent.setRemarks((String) object.getClass().getMethod("getRemarks").invoke(object));
                singleEvent.setRepeaTtime((String) object.getClass().getMethod("getRepeaTtime").invoke(object));
                singleEvent.setIsOverdue((Long) object.getClass().getMethod("getIsOverdue").invoke(object));
                singleEvent.setRemindTime((String) object.getClass().getMethod("getRemindTime").invoke(object));
                singleEvent.setDay((Long) object.getClass().getMethod("getDay").invoke(object));
                singleEvent.setMonth((Long) object.getClass().getMethod("getMonth").invoke(object));
                singleEvent.setYear((Long) object.getClass().getMethod("getYear").invoke(object));
                singleEvent.setType((Long) object.getClass().getMethod("getType").invoke(object));
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
            System.out.println(singleEvent.toString());
            return singleEvent;
    }

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

}
