package com.modcreater.tmbeans.vo.uservo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/16 13:55
 */
@Data
public class UserIdVo implements Serializable {
    private String userId;
    private String pageNumber;
    private String pageSize;
    private String appType;
}
