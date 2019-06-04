package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * 定时任务
 * @Author: AJun
 */
@Data
public class TimedTask {

  private Long id;
  private Long userId;
  private Long eventId;
  private Long backerId;
  private String timer;
  private Long taskStatus;
  private String content;

}
