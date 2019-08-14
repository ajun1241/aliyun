package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: AJun
 */
@Data
public class ActivityTable {

  private Long id;
  private Long discountId;
  private String activityName;
  private Long starTime;
  private Long endTime;
  private Date createDate;
  private Long isOverdue;
  private String activityC1;
  private String activityC2;
  private String activityImg;
  private String afterMoney;

}
