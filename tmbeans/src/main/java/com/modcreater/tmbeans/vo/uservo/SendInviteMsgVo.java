package com.modcreater.tmbeans.vo.uservo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/21 15:40
 */
@Data
public class SendInviteMsgVo implements Serializable {
    private String appType;
    private String userId;
    private String[] friendIds;
    private String eventId;
}
