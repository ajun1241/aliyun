package com.modcreater.tmbeans.vo.eventvo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/7/18 14:04
 */
@Data
public class DraftToEventVo implements Serializable {
    private String userId;
    private String draft;
    private String appType;
}
