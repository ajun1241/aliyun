package com.modcreater.tmutils.messageutil;

import io.rong.messages.BaseMessage;
import io.rong.util.GsonUtil;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/7/31 16:19
 */
public class FriendCardMessage extends BaseMessage {

    private static final transient String TYPE = "ZX:FriendCardMsg";
    private String headImg="";
    private String userName="";
    private String userCode="";
    public FriendCardMessage(String headImg, String userName, String userCode) {
        this.headImg = headImg;
        this.userName = userName;
        this.userCode = userCode;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    @Override
    public String getType() {
        return "ZX:FriendCardMsg";
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this, FriendCardMessage.class);
    }
}
