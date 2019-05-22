package com.modcreater.tmutils;

import io.rong.messages.BaseMessage;
import io.rong.util.GsonUtil;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/22 11:06
 */

public class InviteMessage extends BaseMessage {
    private String content = "";
    private String extra = "";
    private String extraData = "";
    private static final transient String TYPE = "InviteMsg";

    public InviteMessage(String content, String extra, String extraData) {
        this.content = content;
        this.extra = extra;
    }

    @Override
    public String getType() {
        return "InviteMsg";
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtra() {
        return this.extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this, InviteMessage.class);
    }
}
