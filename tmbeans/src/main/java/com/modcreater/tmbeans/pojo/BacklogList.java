package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * @Author: AJun
 */
@Data
public class BacklogList {

  private Long id;
  private Long eventId;
  private Long userId;
  private String backlogName;
  private Long backlogStatus;
  private Long finishTime;

}
