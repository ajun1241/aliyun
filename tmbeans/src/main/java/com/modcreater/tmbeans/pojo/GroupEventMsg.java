package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * @Author: AJun
 */
@Data
public class GroupEventMsg {

  private Long id;
  private Long userId;
  private Long groupId;
  private String msgBody;
  private String eventName;
  private String address;
  private Long startTime;
  private Long endTime;
  private Long type;
  private Long level;
  private String repeatTime;
  private Long year;
  private Long month;
  private Long day;
  private String remindTime;
  private String person;
  private String remark;
  private String backlogList;
  private String createTime;

}
