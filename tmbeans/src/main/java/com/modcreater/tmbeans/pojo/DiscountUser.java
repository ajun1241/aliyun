package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: AJun
 */
@Data
public class DiscountUser {

  private Long id;
  private Long userId;
  private Long discountId;
  private Date createDate;
  private Long starTime;
  private Long endTime;
  private Long status;


}
