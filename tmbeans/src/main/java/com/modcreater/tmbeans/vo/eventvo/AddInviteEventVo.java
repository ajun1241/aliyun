package com.modcreater.tmbeans.vo.eventvo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/22 9:57
 */
@Data
public class AddInviteEventVo implements Serializable {
    private String appType;
    private String singleEvent;
    private String isSync;
    private String userId;
}
