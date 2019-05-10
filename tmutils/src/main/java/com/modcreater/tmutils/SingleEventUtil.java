package com.modcreater.tmutils;

import com.alibaba.fastjson.JSONObject;
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
@SuppressWarnings({"ALL", "AlibabaUndefineMagicConstant"})
public class SingleEventUtil {

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
        Boolean[] booleans = new Boolean[7];
        if (StringUtils.hasText(singleEvent1.getRepeaTtime())) {
            String[] s = singleEvent1.getRepeaTtime().split(",");
            //noinspection AlibabaUndefineMagicConstant
            for (int i = 0; i <= 6; i++) {
                booleans[i] = "true".equals(s[i]);
            }
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
            showSingleEvent.setRepeaTtime(booleans);
            return showSingleEvent;
        }
        return new ShowSingleEvent();
    }
}
