package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * @Author: AJun
 */
@Data
public class MsgStatus {

  private Long id;
  private Long userId;
  private Long receiverId;
  // 0: 同意; 1：不同意; 2：未回应;3：已过期;
  private Long status;
  private Long type;

}
