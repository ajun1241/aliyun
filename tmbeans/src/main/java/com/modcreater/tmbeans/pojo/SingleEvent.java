package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class SingleEvent implements Serializable {

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
    /**
     * 持续时间
     */
    private Long duration;
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

    @Override
    public String toString() {
        return "SingleEvent{" +
                "userid=" + userid +
                ", eventid=" + eventid +
                ", eventname='" + eventname + '\'' +
                ", starttime='" + starttime + '\'' +
                ", endtime='" + endtime + '\'' +
                ", address='" + address + '\'' +
                ", level=" + level +
                ", duration=" + duration +
                ", flag=" + flag +
                ", person='" + person + '\'' +
                ", remarks='" + remarks + '\'' +
                ", repeaTtime='" + repeaTtime + '\'' +
                ", isOverdue=" + isOverdue +
                ", remindTime='" + remindTime + '\'' +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", type=" + type +
                '}';
    }

    //将对象中的属性存入集合作比较
    public static Map<String,String> toMap(SingleEvent singleEvent){
        Map<String,String> map=new HashMap<>();
        map.put("事件名称",singleEvent.getEventname());
        map.put("开始时间",singleEvent.getStarttime());
        map.put("结束时间",singleEvent.getEndtime());
        map.put("地址",singleEvent.getAddress());
        map.put("优先级",singleEvent.getLevel().toString());
        map.put("备注",singleEvent.getRemarks());
        map.put("重复次数",singleEvent.getRepeaTtime());
        map.put("提醒时间",singleEvent.getRemindTime());
        map.put("年",singleEvent.getYear().toString());
        map.put("月",singleEvent.getMonth().toString());
        map.put("日",singleEvent.getDay().toString());
        map.put("事件类型",singleEvent.getType().toString());
        return map;
    }
}
