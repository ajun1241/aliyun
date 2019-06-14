package com.modcreater.tmutils.messageutil;

import io.rong.messages.BaseMessage;
import io.rong.util.GsonUtil;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/22 11:06
 */

public class UpdateInviteMessage extends BaseMessage {
    private String content = "";
    private String date = "";
    private String extraData = "";
    private String extra = "";
    /**
     *   消息id
     */
    private String msgId = "";
    private static final transient String TYPE = "ZX:UpdInviteMsg";

    public UpdateInviteMessage(String content, String date, String extraData, String extra, String msgId) {
        this.content = content;
        this.date = date;
        this.extraData = extraData;
        this.extra = extra;
        this.msgId = msgId;
    }

    @Override
    public String getType() {
        return "ZX:UpdInviteMsg";
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
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
    public String toString() {
        return GsonUtil.toJson(this, UpdateInviteMessage.class);
    }
}
