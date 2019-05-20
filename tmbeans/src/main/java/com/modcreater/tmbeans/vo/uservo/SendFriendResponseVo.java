package com.modcreater.tmbeans.vo.uservo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 *  content 消息内容
 *  fromUserId 发送用户id
 *  toUserId 接收用户Id
 *  objectName RC:TxtMsg
 *  pushContent 消息标题
 *  pushData 空-安卓 非空：苹果
 *
 * @Author: AJun
 * @Date: 2019/5/17 17:22
 */


@Data
public class SendFriendResponseVo implements Serializable {

    private String userId;
    private String friendId;

    private String appType;
}
