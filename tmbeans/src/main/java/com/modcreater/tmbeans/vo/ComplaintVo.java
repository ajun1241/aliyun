package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/6/4 15:21
 */
@Data
public class ComplaintVo implements Serializable {

    private String userId;
    private String description;
    private String appType;

}
