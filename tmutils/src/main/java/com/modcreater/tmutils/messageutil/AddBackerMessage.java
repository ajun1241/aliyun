package com.modcreater.tmutils.messageutil;

import io.rong.messages.BaseMessage;
import io.rong.util.GsonUtil;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-07-03
 * Time: 14:28
 */
public class AddBackerMessage extends BaseMessage {

    private String content = "";

    private String msgId = "";

    private static final transient String TYPE = "ZX:AddBackerMsg";

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    @Override
    public String getType() {
        return "ZX:AddBackerMsg";
    }
    @Override
    public String toString() {
        return GsonUtil.toJson(this, AddBackerMessage.class);
    }

}
