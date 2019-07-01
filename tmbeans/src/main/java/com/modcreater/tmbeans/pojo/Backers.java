package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: AJun
 */
@Data
public class Backers {

  private String id;
  private String userId;
  private String backerId;
  private Date createDate;
  private String status;

}
