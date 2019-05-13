package com.modcreater.tmchat.rong.models.group;

import io.rong.models.group.GroupModel;

/**
 * @author RongCloud
 */
public class UserGroup {

    private String id;
    private GroupModel[] groups;

    public UserGroup() {
    }

    public UserGroup(String id, GroupModel[] groups) {
        this.id = id;
        this.groups = groups;
    }

    public String getId() {
        return this.id;
    }

    public io.rong.models.group.UserGroup setId(String id) {
        this.id = id;
        return this;
    }

    public GroupModel[] getGroups() {
        return this.groups;
    }

    public io.rong.models.group.UserGroup setGroups(GroupModel[] groups) {
        this.groups = groups;
        return this;
    }
}
