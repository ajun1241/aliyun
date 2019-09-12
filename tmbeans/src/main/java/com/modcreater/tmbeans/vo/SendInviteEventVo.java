package com.modcreater.tmbeans.vo;

import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.pojo.SingleEventAndBacklog;
import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/9/11 17:15
 */
@Data
public class SendInviteEventVo implements Serializable {
    private String userId;
    private String appType;
    private String groupId;//目标Id
    private String singleEvent;//带清单的事件内容
}
