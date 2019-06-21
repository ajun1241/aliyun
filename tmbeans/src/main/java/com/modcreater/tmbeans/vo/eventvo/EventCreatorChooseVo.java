package com.modcreater.tmbeans.vo.eventvo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/22 10:09
 */
@Data
public class EventCreatorChooseVo implements Serializable {
    private String appType;
    /**
     * 1 保留； 0 取消
     */
    private String choose;
    private String userId;

    private String extraData;

    private String msgId;
}
