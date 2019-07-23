package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * @Author:
 */
@Data
public class SynchronHistory {

  private Long id;
  private Long createrId;
  private Long senderId;
  private Long eventId;
  private Long createDate;
  private Integer status;
  private Long receiverId;
  private Integer isSucceed;

}
