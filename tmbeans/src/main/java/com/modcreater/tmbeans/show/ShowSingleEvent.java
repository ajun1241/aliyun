package com.modcreater.tmbeans.show;

import com.modcreater.tmbeans.pojo.BacklogList;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-08
 * Time: 16:24
 */
@Data
public class ShowSingleEvent {

    private Long userid;  //用户id
    private Long eventid;  //事件id
    private String eventname;  //事件名称
    private String starttime;  //开始时间
    private String endtime;    //结束时间
    private String address;     //地址
    private Long level;      //级别
//    private Long duration;       //持续时间
    private Long flag;       //标识：是否为空白事件，0为空白事件，1为其他事件
    private String person;  //人物
    private String remarks;  //备注
    private Boolean[] repeaTtime;  //重复次数

    private Long isOverdue;   //是否过期，即已经完成，1代表已经完成，0代表未完成
    private String remindTime;  //提醒时间

    private Long year;       //年
    private Long month;      //月
    private Long day;        //日
    private Long type;    //事件分类

    private List<BacklogList> backlogList;
}
