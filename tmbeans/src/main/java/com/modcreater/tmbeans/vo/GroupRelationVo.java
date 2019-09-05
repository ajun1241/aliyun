package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: AJun
 */
@Data
public class GroupRelationVo implements Serializable {

  private Long id;
  private Long groupId;
  private Long memberId;
  private Long memberLevel;
  private String joinDate;
  private List<String> memberIds;

  private String userId;
  private String appType;



}
