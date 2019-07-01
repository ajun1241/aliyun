package com.modcreater.tmbeans.vo.eventvo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/7/1 10:21
 */
@Data
public class AddInviterVo implements Serializable {
    private String userId;
    private String appType;
    private String eventId;
    private String friendsId;
}
