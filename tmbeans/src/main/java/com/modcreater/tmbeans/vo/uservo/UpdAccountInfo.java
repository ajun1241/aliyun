package com.modcreater.tmbeans.vo.uservo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 * 用户信息修改
 * @Author: AJun
 * @Date: 2019/6/4 15:52
 */
@Data
public class UpdAccountInfo implements Serializable {
    private String userId;
    private String userName;
    private String gender;
    private String userSign;
    private String appType;
}
