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

  private Long diaryId;
  private Long userId;
  private String content;
  private String diaryImage;
  private String createDate;
  private Long moodType;
  private Long status;

}
