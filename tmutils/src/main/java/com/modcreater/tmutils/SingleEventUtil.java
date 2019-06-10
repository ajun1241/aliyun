package com.modcreater.tmutils;

import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.show.ShowSingleEvent;
import com.modcreater.tmbeans.show.userinfo.ShowCompletedEvents;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        String x=repeatTime.substring(1,(repeatTime.length()-1));
        for (String s : x.split(",")) {
            if ("true".equals(s)){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断事件对象中属性是否都符合标准
     * @param singleEvent
     * @return
     */
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
        if (singleEvent.getAddress().length() >= LENGTH51){
            return DtoUtil.getFalseDto("address不规范",21010);
        }
        if (singleEvent.getPerson().length() >= LENGTH51){
            return DtoUtil.getFalseDto("Person不规范",21010);
        }
        if (singleEvent.getRemarks().length() >= LENGTH51){
            return DtoUtil.getFalseDto("Remarks不规范",21010);
        }
        if (singleEvent.getRepeaTtime().length() >= LENGTH44){
            return DtoUtil.getFalseDto("RepeaTtime不规范",21010);
        }
        return null;
    }

    /**
     * 判断对象中除指定外的属性是否全部为空
     * 返回真:
     * @param object
     * @param excludesFields
     * @return
     */
    public static boolean isAllPropertiesEmpty(Object object,List<String> excludesFields){
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
                if(!excludesFields.contains(f.getName()) && f.get(object) != null && !"".equals(f.get(object).toString())) {
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

    public static ShowCompletedEvents getShowCompleted(SingleEvent singleEvent) {
        ShowCompletedEvents showCompletedEvents = new ShowCompletedEvents();
        showCompletedEvents.setEventId(singleEvent.getEventid().toString());
        showCompletedEvents.setUserId(singleEvent.getUserid().toString());
        showCompletedEvents.setEventName(singleEvent.getEventname());
        showCompletedEvents.setDate(singleEvent.getYear().toString()+"-"+singleEvent.getMonth()+"-"+singleEvent.getDay());
        return showCompletedEvents;
    }

    /**
     * 用于比较两个事件差异的部分，转化为描述语句
     * @return
     */
    public static StringBuffer eventDifferent(Map<String,Object> m1,Map<String,Object> m2){
        //比较差异
        StringBuffer different=new StringBuffer();
        for (String key: m1.keySet()) {
            if (!m1.get(key).equals(m2.get(key))){
                if (("开始时间").equals(key)){
                    different.append(key+"更改为："+Long.parseLong(m1.get(key).toString())/60+":"+Long.parseLong(m1.get(key).toString())%60+"；");
                }else if (("结束时间").equals(key)){
                    different.append(key+"更改为："+Long.parseLong(m1.get(key).toString())/60+":"+Long.parseLong(m1.get(key).toString())%60+"；");
                }else if (("优先级").equals(key)){
                    //2：不紧迫也不重要；3：紧迫但不重要；4：重要又不紧迫；5：重要又紧迫
                    if ("2".equals(m1.get(key))){
                        different.append(key+"更改为：不紧迫也不重要；");
                    }else if ("3".equals(m1.get(key))){
                        different.append(key+"更改为：紧迫但不重要；");
                    }else if ("4".equals(m1.get(key))){
                        different.append(key+"更改为：重要又不紧迫；");
                    }else if ("5".equals(m1.get(key))){
                        different.append(key+"更改为：重要又紧迫；");
                    }
                }else if (("重复次数").equals(key)){
                    String[] arr=m1.get(key).toString().replace("[","").replace("]","").split(",");
                    StringBuffer stringBuffer=new StringBuffer();
                    for (int i = 0; i <arr.length ; i++) {
                        if ("true".equals(arr[i])){
                            stringBuffer.append(i+"、");
                        }
                    }
                    if (StringUtils.isEmpty(stringBuffer)) {
                        different.append(key + "更改为：不重复事件；");
                    } else {
                        different.append(key + "更改为：每周" + stringBuffer.replace(stringBuffer.length()-1,stringBuffer.length(),"")+ "重复；");
                    }
                }else if (("提醒时间").equals(key)){
                    different.append(key + "更改为：时间开始前"+m1.get(key)+"分钟；");
                }else if (("年").equals(key)){
                    different.append(key + "更改为："+m1.get(key)+"年；");
                }else if (("月").equals(key)){
                    different.append(key + "更改为："+m1.get(key)+"月；");
                }else if (("日").equals(key)){
                    different.append(key + "更改为："+m1.get(key)+"日；");
                }else if (("事件类型").equals(key)){
                    //0：学习；1：工作；2：商务；3：休闲；4：家庭；5：节日；6：假期；7：其他
                    if ("0".equals(m1.get(key))){
                        different.append(key+"更改为：学习；");
                    }if ("1".equals(m1.get(key))){
                        different.append(key+"更改为：工作；");
                    }if ("2".equals(m1.get(key))){
                        different.append(key+"更改为：商务；");
                    }if ("3".equals(m1.get(key))){
                        different.append(key+"更改为：休闲；");
                    }if ("4".equals(m1.get(key))){
                        different.append(key+"更改为：家庭；");
                    }if ("5".equals(m1.get(key))){
                        different.append(key+"更改为：节日；");
                    }if ("6".equals(m1.get(key))){
                        different.append(key+"更改为：假期；");
                    }if ("7".equals(m1.get(key))){
                        different.append(key+"更改为：其他；");
                    }
                }
                different.append(key+"更改为："+m1.get(key)+"；");
            }
        }
        return different;
    }

    /**
     * 判断时间冲突
     * @param singleEventList
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean eventTime(List<SingleEvent> singleEventList, Long startTime, Long endTime) {
        if (singleEventList.size() == 0){
            return true;
        }
        for (SingleEvent singleEvent1 : singleEventList) {
            boolean res =  ((Long.valueOf(singleEvent1.getStarttime()) > startTime && Long.valueOf(singleEvent1.getEndtime()) < endTime)
                    || (Long.valueOf(singleEvent1.getStarttime()) > startTime && Long.valueOf(singleEvent1.getStarttime()) < endTime)
                    || (Long.valueOf(singleEvent1.getEndtime()) > startTime && Long.valueOf(singleEvent1.getEndtime()) < endTime)
                    || (Long.valueOf(singleEvent1.getStarttime()) <= startTime && Long.valueOf(singleEvent1.getEndtime()) >= endTime));
            if (res){
                return false;
            }
        }
        return true;
    }
}
