package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: @AJun
 */
@Data
public class Eventtemporary implements Serializable {

  private long id;
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
  private String repeatTime;
  private long isOverdue;
  private String remindTime;
  private long day;
  private long month;
  private long year;
  private long type;
  private long isLoop;

}
