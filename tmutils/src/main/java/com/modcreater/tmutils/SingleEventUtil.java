package com.modcreater.tmutils;

import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.show.ShowSingleEvent;
import com.modcreater.tmbeans.show.userinfo.ShowCompletedEvents;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.*;

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
    private static final int LENGTH44 = 44;
    private static final int LENGTH51 = 51;

    /**
     * 创建一个SingleEvent对象并仅赋值day,month,year和userId
     *
     * @param userId
     * @param dayEventId
     * @return
     */
    public static SingleEvent getSingleEvent(String userId, String dayEventId) {
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
     *
     * @param singleEventText
     * @param clazz
     * @return
     */
    public static SingleEvent jsonToSingleEvent(String singleEventText, Class<SingleEvent> clazz) {
        return JSONObject.parseObject(singleEventText, clazz);
    }

    /**
     * 将传进来的SingleEvent集合转换成用来返回的ShowSingleEvent的集合
     *
     * @param singleEventList
     * @return
     */
    public static List<ShowSingleEvent> getShowSingleEventList(List<SingleEvent> singleEventList) {
        List<ShowSingleEvent> showSingleEventList = new ArrayList<>();
        for (SingleEvent singleEvent1 : singleEventList) {
            showSingleEventList.add(getShowSingleEvent(singleEvent1));
        }
        return showSingleEventList;
    }

    /**
     * 将传进来的SingleEvent转换成ShowSingleEvent
     *
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
     *
     * @param singleEvent
     * @return
     */
    public static Boolean[] getRepeatTime(SingleEvent singleEvent) {
        Boolean[] booleans = new Boolean[7];
        String x = singleEvent.getRepeaTtime().substring(1, (singleEvent.getRepeaTtime().length() - 1));
        String[] s = x.split(",");
        for (int i = 0; i <= 6; i++) {
            booleans[i] = "true".equals(s[i]);
        }
        return booleans;
    }

    /**
     * 对repeatTime字符串解析并判断是否是一个重复事件,如果是则返回true
     *
     * @param repeatTime
     * @return
     */
    public static boolean isLoopEvent(String repeatTime) {
        String x = repeatTime.substring(1, (repeatTime.length() - 1));
        for (String s : x.split(",")) {
            if ("true".equals(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断事件对象中属性是否都符合标准
     *
     * @param singleEvent
     * @return
     */
    public static Dto isSingleEventStandard(SingleEvent singleEvent) {
        if (singleEvent.getEventid() >= VALUE10) {
            return DtoUtil.getFalseDto("eventId不规范", 21010);
        }
        if (singleEvent.getUserid() >= VALUE11) {
            return DtoUtil.getFalseDto("userId不规范", 21010);
        }
        if (singleEvent.getEventname().length() >= LENGTH10) {
            return DtoUtil.getFalseDto("eventName不规范", 21010);
        }
        if (singleEvent.getAddress().length() >= LENGTH51) {
            return DtoUtil.getFalseDto("address不规范", 21010);
        }
        if (singleEvent.getPerson().length() >= LENGTH51) {
            return DtoUtil.getFalseDto("Person不规范", 21010);
        }
        if (singleEvent.getRemarks().length() >= LENGTH51) {
            return DtoUtil.getFalseDto("Remarks不规范", 21010);
        }
        if (singleEvent.getRepeaTtime().length() >= LENGTH44) {
            return DtoUtil.getFalseDto("RepeaTtime不规范", 21010);
        }
        return null;
    }

    /**
     * 判断对象中除指定外的属性是否全部为空
     * 返回真:
     *
     * @param object
     * @param excludesFields
     * @return
     */
    public static boolean isAllPropertiesEmpty(Object object, List<String> excludesFields) {
        /// 取到obj的class, 并取到所有属性
        Field[] fs = object.getClass().getDeclaredFields();
        // 定义一个flag, 标记是否所有属性值为空
        boolean flag = true;
        // 遍历所有属性
        for (Field f : fs) {
            // 设置私有属性也是可以访问的
            f.setAccessible(true);
            // 1.排除不包括的属性名, 2.属性值不为空, 3.属性值转换成String不为""
            try {
                if (!excludesFields.contains(f.getName()) && f.get(object) != null && !"".equals(f.get(object).toString())) {
                    // 有属性满足3个条件的话, 那么说明对象属性不全为空
                    flag = false;
                    break;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 为ShowCompletedEvents对象赋值
     *
     * @param singleEvent
     * @return
     */
    public static ShowCompletedEvents getShowCompleted(SingleEvent singleEvent) {
        ShowCompletedEvents showCompletedEvents = new ShowCompletedEvents();
        showCompletedEvents.setEventId(singleEvent.getEventid().toString());
        showCompletedEvents.setUserId(singleEvent.getUserid().toString());
        showCompletedEvents.setEventName(singleEvent.getEventname());
        showCompletedEvents.setDate(singleEvent.getYear().toString() + "-" + singleEvent.getMonth() + "-" + singleEvent.getDay());
        showCompletedEvents.setAddress(singleEvent.getAddress());
        showCompletedEvents.setType(singleEvent.getType().toString());
        return showCompletedEvents;
    }

    /**
     * 用于比较两个事件差异的部分，转化为描述语句
     *
     * @return
     */
    public static List<Map<String,String>> eventDifferent(Map<String, String> newMap, Map<String, String> oldMap) {
        //比较差异
        List<Map<String,String>> different = new ArrayList<>();

        for (String key : newMap.keySet()) {
            Map<String,String> map=new HashMap<>();
            if (!newMap.get(key).equals(oldMap.get(key))) {
                if (("a").equals(key)){
                    map.put("title","名称更改为：");
                    map.put("content",newMap.get(key));
                }else if (("b").equals(key)) {
                    map.put("title","开始时间更改为：");
                    String h=Long.parseLong(newMap.get(key)) / 60+"";
                    String m=Long.parseLong(newMap.get(key)) % 60+"";
                    if (h.length()==1){
                        h="0"+h;
                    }
                    if (m.length()==1){
                        m="0"+m;
                    }
                    map.put("content",h +":"+ m);
                } else if (("c").equals(key)) {
                    map.put("title","结束时间更改为：");
                    String h=Long.parseLong(newMap.get(key)) / 60+"";
                    String m=Long.parseLong(newMap.get(key)) % 60+"";
                    if (h.length()==1){
                        h="0"+h;
                    }
                    if (m.length()==1){
                        m="0"+m;
                    }
                    map.put("content",h +":"+ m);
                } else if (("d").equals(key)) {
                    map.put("title","优先级更改为：");
                    //2：不紧迫也不重要；3：紧迫但不重要；4：重要又不紧迫；5：重要又紧迫
                    if ("2".equals(newMap.get(key))) {
                        map.put("content","不紧迫也不重要");
                    } else if ("3".equals(newMap.get(key))) {
                        map.put("content","紧迫但不重要");
                    } else if ("4".equals(newMap.get(key))) {
                        map.put("content","重要又不紧迫");
                    } else if ("5".equals(newMap.get(key))) {
                        map.put("content","重要又紧迫");
                    }
                } else if (("e").equals(key)) {
                    map.put("title","重复时间更改为：");
                    String[] arr = newMap.get(key).replace("[", "").replace("]", "").split(",");
                    StringBuffer stringBuffer = new StringBuffer();
                    String week="";
                    for (int i = 0; i < arr.length; i++) {
                        if ("true".equals(arr[i])) {
                            if (i==0){
                                week="日";
                            }else if (i==1){
                                week="一";
                            }else if (i==2){
                                week="二";
                            }else if (i==3){
                                week="三";
                            }else if (i==4){
                                week="四";
                            }else if (i==5){
                                week="五";
                            }else if (i==6){
                                week="六";
                            }
                            stringBuffer.append(week + "、");
                        }
                    }
                    if (ObjectUtils.isEmpty(stringBuffer)) {
                        map.put("content","不重复事件");
                    } else {
                        map.put("content","每周" + stringBuffer.replace(stringBuffer.length() - 1, stringBuffer.length(), "") + "重复");
                    }
                } else if (("f").equals(key)) {
                    map.put("title","提醒时间更改为：");
                    map.put("content","事件开始前" + newMap.get(key) + "分钟");
                } else if (("g").equals(key) || ("h").equals(key) || ("i").equals(key)) {
                    map.put("title","日期更改为：");
                    map.put("content",newMap.get("年") + "年"+newMap.get("月") + "月"+newMap.get("日") + "日");
                }  else if (("j").equals(key)) {
                    map.put("title","类型更改为：");
                    //0：学习；1：工作；2：商务；3：休闲；4：家庭；5：节日；6：假期；7：其他
                    if ("0".equals(newMap.get(key))) {
                        map.put("content","学习");
                    } else if ("1".equals(newMap.get(key))) {
                        map.put("content","工作");
                    } else if ("2".equals(newMap.get(key))) {
                        map.put("content","商务");
                    } else if ("3".equals(newMap.get(key))) {
                        map.put("content","休闲");
                    } else if ("4".equals(newMap.get(key))) {
                        map.put("content","家庭");
                    } else if ("5".equals(newMap.get(key))) {
                        map.put("content","节日");
                    } else if ("6".equals(newMap.get(key))) {
                        map.put("content","假期");
                    } else if ("7".equals(newMap.get(key))) {
                        map.put("content","其他");
                    }
                }else if (("k").equals(key)){
                    map.put("title","地址更改为：");
                    map.put("content",newMap.get(key));
                }else if (("l").equals(key)){
                    map.put("title", "备注更改为：");
                    map.put("content",newMap.get(key));
                }
                different.add(map);
            }
        }
        return different;
    }

    /**
     * 判断时间冲突
     *
     * @param singleEventList
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean eventTime(List<SingleEvent> singleEventList, Long startTime, Long endTime) {
        if (singleEventList.size() == 0) {
            return true;
        }
        for (SingleEvent singleEvent1 : singleEventList) {
            boolean res = ((Long.valueOf(singleEvent1.getStarttime()) > startTime && Long.valueOf(singleEvent1.getEndtime()) < endTime)
                    || (Long.valueOf(singleEvent1.getStarttime()) > startTime && Long.valueOf(singleEvent1.getStarttime()) < endTime)
                    || (Long.valueOf(singleEvent1.getEndtime()) > startTime && Long.valueOf(singleEvent1.getEndtime()) < endTime)
                    || (Long.valueOf(singleEvent1.getStarttime()) <= startTime && Long.valueOf(singleEvent1.getEndtime()) >= endTime));
            if (res) {
                //冲突了,返回false
                return false;
            }
        }
        return true;
    }

    public static boolean loopEventTime(List<SingleEvent> singleEventList, SingleEvent loopEvent){
        if (singleEventList.size() == 0) {
            return true;
        }
        for (SingleEvent singleEvent :  singleEventList){
            Boolean[] rep = getRepeatTime(singleEvent);
            Boolean[] repLoop = getRepeatTime(loopEvent);
            for (int i = 0; i <= 6; i++){
                if (rep[i] && repLoop[i]){
                    boolean res = ((Long.valueOf(singleEvent.getStarttime()) > Long.valueOf(loopEvent.getStarttime()) && Long.valueOf(singleEvent.getEndtime()) < Long.valueOf(loopEvent.getEndtime()))
                            || (Long.valueOf(singleEvent.getStarttime()) > Long.valueOf(loopEvent.getStarttime()) && Long.valueOf(singleEvent.getStarttime()) < Long.valueOf(loopEvent.getEndtime()))
                            || (Long.valueOf(singleEvent.getEndtime()) > Long.valueOf(loopEvent.getStarttime()) && Long.valueOf(singleEvent.getEndtime()) < Long.valueOf(loopEvent.getEndtime()))
                            || (Long.valueOf(singleEvent.getStarttime()) <= Long.valueOf(loopEvent.getStarttime()) && Long.valueOf(singleEvent.getEndtime()) >= Long.valueOf(loopEvent.getEndtime())));
                    if (res) {
                        //冲突了,返回false
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
