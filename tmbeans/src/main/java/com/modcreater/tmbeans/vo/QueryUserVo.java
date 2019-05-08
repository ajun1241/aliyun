package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class QueryUserVo implements Serializable {
    private String id;
    //登录平台类型
    private String apptype;
}
