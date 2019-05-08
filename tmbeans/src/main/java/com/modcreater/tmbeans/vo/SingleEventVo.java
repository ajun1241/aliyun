package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class SingleEventVo implements Serializable {
    private String userid;  //用户id
    private String eventid;  //事件id
    private String eventname;  //事件名称
    private String starttime;  //开始时间
    private String endtime;    //结束时间
    private String address;     //地址
    private String level;      //级别
    private String duration;       //持续时间
    private String flag;       //标识：是否为空白事件，0为空白事件，1为其他事件
    private String person;  //人物
    private String remarks;  //备注
    private String repeaTtime;  //重复次数

    private String isOverdue;   //是否过期，即已经完成，1代表已经完成，0代表未完成
    private String remindTime;  //提醒时间

    private String year;       //年
    private String month;      //月
    private String day;        //日
    private String type;    //事件分类
}
