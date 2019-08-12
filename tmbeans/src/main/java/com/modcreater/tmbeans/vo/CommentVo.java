package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.util.Date;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/8/7 17:07
 */
@Data
public class CommentVo {
    private String appType;
    private String commentId;
    private String diaryId;
    private String userId;
    private String commentContent;
    private String date;
    private String status;
}
