package com.modcreater.tmutils.messageutil;

import io.rong.messages.BaseMessage;
import io.rong.util.GsonUtil;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/8/5 16:18
 */
public class VerifyFriendMsg extends BaseMessage {
    private String content;
    private String extra;
    private static final transient String TYPE = "ZX:VFMsg";

    public VerifyFriendMsg(String content, String extra) {
        this.content = content;
        this.extra = extra;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this, VerifyFriendMsg.class);
    }
}
