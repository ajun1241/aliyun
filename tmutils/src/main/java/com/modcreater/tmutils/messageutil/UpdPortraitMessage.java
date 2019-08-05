package com.modcreater.tmutils.messageutil;

import io.rong.messages.BaseMessage;
import io.rong.util.GsonUtil;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/7/31 16:19
 */
public class UpdPortraitMessage extends BaseMessage {

    private static final transient String TYPE = "ZX:updPortraitMsg";
    private String userId="";
    private String userName="";
    private String headImageUrl="";

    public UpdPortraitMessage(String userId, String userName, String headImageUrl) {
        this.userId = userId;
        this.userName = userName;
        this.headImageUrl = headImageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeadImageUrl() {
        return headImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }

    @Override
    public String getType() {
        return "ZX:updPortraitMsg";
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this, UpdPortraitMessage.class);
    }
}
