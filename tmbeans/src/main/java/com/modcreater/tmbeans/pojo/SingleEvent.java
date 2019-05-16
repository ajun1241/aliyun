package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 关联的用户id
 * eventNamechar(10) NOT NULL事件名称
 * startTimechar(8) NOT NULL事件开始时间
 * endTimechar(8) NOT NULL事件结束时间
 * addresschar(50) NULL地址
 * levelint(2) NOT NULL事件优先级
 * flagint(2) NULL事件标识(0：空白事件，1：其他事件)
 * personchar(50) NOT NULL事件人物
 * remarkschar(50) NULL备注
 * repeatTimechar(43) NOT NULL重复次数
 * isOverdueint(2) NOT NULL是否过期，即已经完成，1代表已经完成，0代表未完成
 * remindTimechar(8) NOT NULL提醒时间
 * dayint(2) NULL日
 * monthint(2) NULL月
 * yearint(4) NULL年
 * typeint(2) NULL事件分类
 */
@Data
public class SingleEvent implements Serializable {

  private Long userid;  //关联的用户id
  private Long eventid;  //事件id
  private String eventname;  //事件名称
  private String starttime;  //开始时间（从零点开始到事件开始时间的分钟数）
  private String endtime;    //结束时间（从零点开始到事件结束时间的分钟数）
  private String address;     //地址
  private Long level;      //级别（2：不紧迫也不重要；3：紧迫但不重要；4：重要又不紧迫；5：重要又紧迫）
  private Long duration;       //持续时间
  private Long flag;       //标识：是否为空白事件，0为空白事件，1为其他事件
  private String person;  //人物
  private String remarks;  //备注
  private String repeaTtime;  //重复次数

  private Long isOverdue;   //是否过期，即已经完成，1代表已经完成，0代表未完成
  private String remindTime;  //提醒时间

  private Long year;       //年
  private Long month;      //月
  private Long day;        //日
  private Long type;    //事件分类（0：学习；1：工作；2：商务；3：休闲；4：家庭；5：节日；6：假期；7：其他）

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
}
