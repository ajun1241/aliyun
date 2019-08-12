package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: AJun
 */
@Data
public class DiaryComment {

  private Long id;
  private Long diaryId;
  private Long userId;
  private String commentContent;
  private Date date;
  private Long status;

}
