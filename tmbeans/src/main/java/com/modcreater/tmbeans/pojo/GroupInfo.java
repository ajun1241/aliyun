package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * @Author: AJun
 */
@Data
public class GroupInfo {

  private Long id;
  private String groupName;
  private String groupPicture;
  private String groupUnit;
  private Long groupScale;
  private Long groupNature;
  private String groupPresentation;
  private String crateDate;
  private Long createBy;

}
