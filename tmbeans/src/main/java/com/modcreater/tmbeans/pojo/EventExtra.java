package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: AJun
 */
@Data
public class EventExtra {

  private Long id;
  private Long eventId;
  private Long userId;
  private Long common;
  private Date createDate;

}
