package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoopEvent implements Serializable {

  private long eventId;
  private long userId;
  private String eventName;
  private String startTime;
  private String endTime;
  private String address;
  private long level;
  private long flag;
  private String person;
  private String remarks;
  private long isOverdue;
  private String remindTime;
  private long type;
  private String day;
  private String week;

}
