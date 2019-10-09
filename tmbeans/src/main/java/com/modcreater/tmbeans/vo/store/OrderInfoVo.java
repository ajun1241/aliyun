package com.modcreater.tmbeans.vo.store;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/9/29 9:14
 */
@Data
public class OrderInfoVo implements Serializable {
    private String appType;
    private String userId;
    private String code;
    private String codeContent;

}
