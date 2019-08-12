package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/7/15 16:47
 */
@Data
public class DiaryResultVo implements Serializable {
    private Long diaryId;
    private Long userId;
    private String content;
    private String cover;
    private String weather;
    private String createDate;
    private Long moodType;
    private Long status;
}
