package com.modcreater.tmchat.rong.models.group;

import io.rong.models.Result;
import io.rong.models.response.GroupBanInfo;
import io.rong.util.GsonUtil;

public class GroupBanModel extends Result {
    private GroupBanInfo[] groupinfo;

    public GroupBanInfo[] getGroupinfo() {
        return groupinfo;
    }

    public void setGroupinfo(GroupBanInfo[] groupinfo) {
        this.groupinfo = groupinfo;
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this, GroupBanModel.class);
    }
}
