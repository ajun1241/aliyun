package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.util.Date;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/8/7 17:10
 */
@Data
public class CommentReplyVo {
    private String appType;
    private String commentReplyId;
    private String commentId;
    private String userId;
    private String replyContent;
    private String date;
    private String diaryId;
    private String status;
}
