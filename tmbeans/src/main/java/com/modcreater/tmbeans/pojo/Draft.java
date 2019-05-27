package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: AJun
 */
@Data
public class Draft implements Serializable {

  private Long id;
  private Long eventId;
  private Long userId;
  private String eventName;
  private String startTime;
  private String endTime;
  private String address;
  private Long level;
  private Long flag;
  private String person;
  private String remarks;
  private String repeatTime;
  private Long isOverdue;
  private String remindTime;
  private Long day;
  private Long month;
  private Long year;
  private Long type;
  private Long isLoop;

}
