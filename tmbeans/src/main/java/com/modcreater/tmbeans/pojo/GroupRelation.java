package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * @Author: AJun
 */
@Data
public class GroupRelation {

  private Long id;
  private Long groupId;
  private Long memberId;
  private Long memberLevel;
  private String joinDate;

}
