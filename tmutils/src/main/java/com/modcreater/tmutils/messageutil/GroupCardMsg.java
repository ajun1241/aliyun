package com.modcreater.tmutils.messageutil;

import io.rong.messages.BaseMessage;
import io.rong.util.GsonUtil;

/**
 * Description:
 *  团队名片自定义消息类型
 * @Author: AJun
 * @Date: 2019/9/6 11:28
 */
public class GroupCardMsg extends BaseMessage {

    private static final transient String TYPE = "ZX:GroupCardMsg";
    private String groupId;
    private String groupName;
    private String groupPicture;
    private String groupUnit;

    public GroupCardMsg(String groupId, String groupName, String groupPicture, String groupUnit) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupPicture = groupPicture;
        this.groupUnit = groupUnit;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupPicture() {
        return groupPicture;
    }

    public void setGroupPicture(String groupPicture) {
        this.groupPicture = groupPicture;
    }

    public String getGroupUnit() {
        return groupUnit;
    }

    public void setGroupUnit(String groupUnit) {
        this.groupUnit = groupUnit;
    }

    @Override
    public String getType() {
        return "ZX:GroupCardMsg";
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this, GroupCardMsg.class);
    }
}
