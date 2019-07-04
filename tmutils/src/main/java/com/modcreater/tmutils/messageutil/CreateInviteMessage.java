package com.modcreater.tmutils.messageutil;

import io.rong.messages.BaseMessage;
import io.rong.util.GsonUtil;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/6/6 16:13
 */
public class CreateInviteMessage extends BaseMessage {

    private String content = "";
    /**
     * 事件Id
     */
    private String eventId = "";

    private String extra = "";

    private String msgId = "";

    private static final transient String TYPE = "ZX:CreateInviteMsg";

    public CreateInviteMessage(String content, String eventId, String extra, String msgId) {
        this.content = content;
        this.eventId = eventId;
        this.extra = extra;
        this.msgId = msgId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    @Override
    public String getType() {
        return "ZX:CreateInviteMsg";
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this, CreateInviteMessage.class);
    }
}
