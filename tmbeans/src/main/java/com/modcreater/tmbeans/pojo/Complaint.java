package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: AJun
 */
@Data
public class Complaint {

  private Long id;
  private Long userId;
  private Date createDate;
  private String description;
  private Long disposeStatus;
  private Long disposeBy;
  private Date disposeDate;

}
