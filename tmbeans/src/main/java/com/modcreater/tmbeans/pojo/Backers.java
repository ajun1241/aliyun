package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: AJun
 */
@Data
public class Backers {

  private Long id;
  private Long userId;
  private Long backerId;
  private Date createDate;
  private Long status;

}
