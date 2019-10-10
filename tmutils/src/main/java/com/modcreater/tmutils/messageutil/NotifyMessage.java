package com.modcreater.tmutils.messageutil;

import io.rong.messages.BaseMessage;
import io.rong.util.GsonUtil;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/6/19 16:30
 */
public class NotifyMessage extends BaseMessage {
    private String content = "";
    private String msgType = "";
    private static final transient String TYPE = "ZX:NotifyMsg";

    public NotifyMessage(String content, String msgType) {
        this.content = content;
        this.msgType = msgType;
    }

    @Override
    public String getType() {
        return "ZX:NotifyMsg";
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this, NotifyMessage.class);
    }
}
