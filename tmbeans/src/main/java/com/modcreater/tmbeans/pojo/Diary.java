package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: AJun
 * Description:
 *    日记实体类
 */
@Data
public class Diary {

  private Long id;
  private Long userId;
  private String content;
  private Date createDate;
  private Long moodType;
  private Long status;
  private String cover;
  private String weather;

}
