package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/23 17:54
 */
@Data
public class QueryFeedbackStatisticsVo implements Serializable {
    private String agree;
    private String refuse;
    private String noReply;
}
