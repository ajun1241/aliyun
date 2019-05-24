package com.modcreater.tmbeans.vo.eventvo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: AJun
 */
@Data
public class DraftVo implements Serializable {
    private String userId;
    private String phoneNum;
    private String singleEvent;
    private String apptype;
}
