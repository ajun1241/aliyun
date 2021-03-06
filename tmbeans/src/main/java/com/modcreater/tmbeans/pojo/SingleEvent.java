package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Data
public class SingleEvent implements Serializable {

    private Long id;
    /**
     * 关联的用户id
     */
    private Long userid;
    /**
     * 事件id
     */
    private Long eventid;
    /**
     * 事件名称
     */
    private String eventname;
    /**
     * 开始时间（从零点开始到事件开始时间的分钟数）
     */
    private String starttime;
    /**
     * 结束时间（从零点开始到事件结束时间的分钟数）
     */
    private String endtime;
    /**
     * 地址
     */
    private String address;
    /**
     * 级别（2：不紧迫也不重要；3：紧迫但不重要；4：重要又不紧迫；5：重要又紧迫）
     */
    private Long level;
/*    *//**
     * 持续时间
     *//*
    private Long duration;*/
    /**
     * 标识：是否为空白事件，0为空白事件，1为其他事件
     */
    private Long flag;
    /**
     * 人物
     */
    private String person;
    /**
     * 备注
     */
    private String remarks;
    /**
     * 重复次数
     */
    private String repeaTtime;
    /**
     * 是否过期，即已经完成，1代表已经完成，0代表未完成
     */
    private Long isOverdue;
    /**
     * 提醒时间
     */
    private String remindTime;
    /**
     * 年
     */
    private Long year;
    /**
     * 月
     */
    private Long month;
    /**
     * 日
     */
    private Long day;
    /**
     * 事件分类（0：学习；1：工作；2：商务；3：休闲；4：家庭；5：节日；6：假期；7：其他）
     */
    private Long type;
    /**
     * 是否重复
     */
    private int isLoop;

    public SingleEvent() {

    }

    //将对象中的属性存入集合作比较
    public static Map<String,String> toMap(SingleEvent singleEvent){
        Map<String,String> map=new TreeMap<>();
        map.put("a",singleEvent.getEventname());
        map.put("b",singleEvent.getStarttime());
        map.put("c",singleEvent.getEndtime());
        map.put("d",singleEvent.getLevel().toString());
        map.put("e",singleEvent.getRepeaTtime());
        map.put("f",singleEvent.getRemindTime());
        map.put("g",singleEvent.getYear().toString());
        map.put("h",singleEvent.getMonth().toString());
        map.put("i",singleEvent.getDay().toString());
        map.put("j",singleEvent.getType().toString());
        map.put("k",singleEvent.getAddress());
        map.put("l",singleEvent.getRemarks());
        return map;
    }
}
