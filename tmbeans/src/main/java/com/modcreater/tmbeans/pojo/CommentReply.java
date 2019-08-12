package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: AJun
 */
@Data
public class CommentReply {

  private Long id;
  private Long commentId;
  private Long userId;
  private String replyContent;
  private Date date;
  private Long diaryId;
  private Long status;

}
