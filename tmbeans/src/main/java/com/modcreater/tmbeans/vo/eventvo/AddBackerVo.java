package com.modcreater.tmbeans.vo.eventvo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/27 15:53
 */
@Data
public class AddBackerVo implements Serializable {
    /**
     * 用户Id
     */
    private String userId;

    /**
     * 好友ID
     */
    private String friendIds;

    /**
     * 事件的类
     */
    private String singleEvent;

    private String apptype;
}
