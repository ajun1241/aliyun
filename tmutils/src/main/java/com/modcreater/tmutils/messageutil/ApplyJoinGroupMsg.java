package com.modcreater.tmutils.messageutil;

import io.rong.messages.BaseMessage;
import io.rong.util.GsonUtil;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/9/6 14:44
 */
public class ApplyJoinGroupMsg extends BaseMessage {

    private static final transient String TYPE = "ZX:ApplyJoinGroupMsg";
    private String userId;
    private String userName;
    private String headImgUrl;
    private String userCode;
    private String verificationContent;
    private String groupMsgId;
    private String sourceInfo;

    public ApplyJoinGroupMsg(String userId, String userName, String headImgUrl, String userCode, String verificationContent, String groupMsgId, String sourceInfo) {
        this.userId = userId;
        this.userName = userName;
        this.headImgUrl = headImgUrl;
        this.userCode = userCode;
        this.verificationContent = verificationContent;
        this.groupMsgId = groupMsgId;
        this.sourceInfo = sourceInfo;
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

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getVerificationContent() {
        return verificationContent;
    }

    public void setVerificationContent(String verificationContent) {
        this.verificationContent = verificationContent;
    }

    public String getGroupMsgId() {
        return groupMsgId;
    }

    public void setGroupMsgId(String groupMsgId) {
        this.groupMsgId = groupMsgId;
    }

    public String getSourceInfo() {
        return sourceInfo;
    }

    public void setSourceInfo(String sourceInfo) {
        this.sourceInfo = sourceInfo;
    }

    @Override
    public String getType() {
        return "ZX:ApplyJoinGroupMsg";
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this, ApplyJoinGroupMsg.class);
    }
}
