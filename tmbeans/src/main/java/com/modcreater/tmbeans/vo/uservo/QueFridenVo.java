package com.modcreater.tmbeans.vo.uservo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/16 13:47
 */
@Data
public class QueFridenVo implements Serializable {
    private String userId;
    private String userCode;
    private String appType;
}
