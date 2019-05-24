package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/24 17:19
 */
@Data
public class MsgVo implements Serializable {
    private String headImgUrl;
    private String userName;
    private String msgContent;
}
