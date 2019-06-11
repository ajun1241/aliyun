package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/6/11 14:40
 */
@Data
public class QueryMsgStatusVo implements Serializable {
    private String userId;
    private String appType;
    //消息Id
    private String msgId;

}
