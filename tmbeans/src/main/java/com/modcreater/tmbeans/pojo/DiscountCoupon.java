package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: AJun
 */
@Data
public class DiscountCoupon {

  private Long id;
  private String couponName;
  private Long couponType;
  private Double couponMoney;
  private Long starTime;
  private Long endTime;
  private Date createDate;
  private Long couponStatus;
  private String describe;

}
