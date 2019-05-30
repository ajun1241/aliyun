package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * @Author: AJun
 */
@Data
public class SingleEventVice {

  private Long id;
  private Long eventId;
  private Long userId;
  private Long createBy;
  private Long modifyBy;

}
