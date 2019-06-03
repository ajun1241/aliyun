package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/6/3 17:27
 */
@Data
public class BackendLoginVo implements Serializable {
    private String userName;
    private String passWord;
}
