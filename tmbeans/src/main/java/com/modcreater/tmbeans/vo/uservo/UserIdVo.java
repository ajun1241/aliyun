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
    //第几页
    private String pageNumber;
    //每页的条数
    private String pageSize;
    private String appType;
}
