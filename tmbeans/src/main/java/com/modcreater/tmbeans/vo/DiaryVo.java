package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/7/11 15:43
 */
@Data
public class DiaryVo implements Serializable {

    private String userId;
    private String appType;
    private String diaryId;
    private String content;
    private String diaryImage;
    private String moodType;
    private String status;
}
