package com.modcreater.tmutils.messageutil;

import io.rong.messages.BaseMessage;
import io.rong.util.GsonUtil;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/9/18 9:26
 */
public class RefreshMsg extends BaseMessage {
    /**
     * 刷新类型（1：加入邀请事件）
     */
    private String genre;

    private static final transient String TYPE = "ZX:RefreshMsg";

    public RefreshMsg(String genre) {
        this.genre = genre;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public String getType() {
        return "ZX:RefreshMsg";
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this, RefreshMsg.class);
    }

}
