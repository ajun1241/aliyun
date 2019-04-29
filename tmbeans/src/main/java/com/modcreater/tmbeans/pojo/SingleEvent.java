package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SingleEvent implements Serializable {

  private long id;
  private long userId;
  private String eventName;
  private Date startTime;
  private Date endTime;
  private String address;
  private long level;
  private long flag;
  private String person;
  private String remarks;
  private long repeatTime;
  private long isOverdue;
  private Date remindTime;
  private long day;
  private long month;
  private long year;

}
