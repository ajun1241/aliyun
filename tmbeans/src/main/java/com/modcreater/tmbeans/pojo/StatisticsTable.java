package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: AJun
 * 事件邀请反馈统计表
 */
@Data
public class StatisticsTable implements Serializable {

  private Long  id;
  private Long  creatorId;
  private Long  eventId;
  private Long userId;
  /**
   *选择（0；同意; 1：不同意; 2：未回应）
   */
  private Long  choose;
  private Long  modify;
  private Date createDate;
  private String rejectContent;
  private Long isOverdue;
}
