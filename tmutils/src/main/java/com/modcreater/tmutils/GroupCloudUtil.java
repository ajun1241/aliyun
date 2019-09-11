package com.modcreater.tmutils;

import io.rong.RongCloud;
import io.rong.messages.VoiceMessage;
import io.rong.methods.group.Group;
import io.rong.methods.message.chatroom.Chatroom;
import io.rong.methods.message.discussion.Discussion;
import io.rong.methods.message.history.History;
import io.rong.methods.message.system.MsgSystem;
import io.rong.models.Result;
import io.rong.models.group.GroupMember;
import io.rong.models.group.GroupModel;
import io.rong.models.group.UserGroup;
import io.rong.models.response.GroupUserQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description:
 *  融云群操作工具类
 * @Author: AJun
 * @Date: 2019/9/6 16:34
 */
public class GroupCloudUtil {

    // 申请的融云key
    public final static String appKey = "0vnjpoad0314z";
    // 申请的的云secret
    public final static String appSecret = "0uoZVUDt8lROGb";

    private Logger logger= LoggerFactory.getLogger(RongCloudMethodUtil.class);


    RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
    io.rong.methods.message._private.Private Private = rongCloud.message.msgPrivate;
    MsgSystem system = rongCloud.message.system;
    Group Group = rongCloud.group;
    Chatroom chatroom = rongCloud.message.chatroom;
    Discussion discussion = rongCloud.message.discussion;
    History history = rongCloud.message.history;

    /**
     * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/group/group.html#create
     *
     * 创建群组方法
     *
     */
    public Result createGroup(List<String> groupMembers,String groupId,String groupName) throws Exception {
        List<GroupMember> groupMemberList=new ArrayList<>();
        for (String memberId:groupMembers) {
            GroupMember groupMember=new GroupMember();
            groupMember.setId(memberId);
            groupMemberList.add(groupMember);
        }
        GroupMember[] members=groupMemberList.toArray(new GroupMember[groupMemberList.size()]);
        GroupModel group = new GroupModel()
                .setId(groupId)
                .setMembers(members)
                .setName(groupName);
        Result groupCreateResult = Group.create(group);
        return groupCreateResult;
    }

    /**
     * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/group/group.html#join
     *
     * 邀请用户加入群组
     *
     */
    public Result inviteGroup(List<String> groupMembers,String groupId,String groupName) throws Exception {
        List<GroupMember> groupMemberList=new ArrayList<>();
        for (String memberId:groupMembers) {
            GroupMember groupMember=new GroupMember();
            groupMember.setId(memberId);
            groupMemberList.add(groupMember);
        }
        GroupMember[] members=groupMemberList.toArray(new GroupMember[groupMemberList.size()]);
        GroupModel group = new GroupModel()
                .setId(groupId)
                .setMembers(members)
                .setName(groupName);
        Result groupInviteResult = rongCloud.group.invite(group);
        return groupInviteResult;
    }

    /**
     * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/group/group.html#join
     *
     * 用户加入指定群组
     *
     */
    public Result joinGroup(List<String> groupMembers,String groupId,String groupName) throws Exception {
        List<GroupMember> groupMemberList=new ArrayList<>();
        for (String memberId:groupMembers) {
            GroupMember groupMember=new GroupMember();
            groupMember.setId(memberId);
            groupMemberList.add(groupMember);
        }
        GroupMember[] members=groupMemberList.toArray(new GroupMember[groupMemberList.size()]);
        GroupModel group = new GroupModel()
                .setId(groupId)
                .setMembers(members)
                .setName(groupName);
        Result groupJoinResult = Group.join(group);
        return groupJoinResult;
    }

    /**
     * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/group/group.html#quit
     *
     * 退出群组
     *
     */
    public Result quitGroup(List<String> groupMembers,String groupId,String groupName) throws Exception {
        List<GroupMember> groupMemberList=new ArrayList<>();
        for (String memberId:groupMembers) {
            GroupMember groupMember=new GroupMember();
            groupMember.setId(memberId);
            groupMemberList.add(groupMember);
        }
        GroupMember[] members=groupMemberList.toArray(new GroupMember[groupMemberList.size()]);
        GroupModel group = new GroupModel()
                .setId(groupId)
                .setMembers(members)
                .setName(groupName);
        Result groupQuitResult = Group.quit(group);
        return groupQuitResult;
    }


    /**
     *
     * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/group/group.html#dismiss
     *
     * 解散群组
     *
     */
    public Result dissolutionGroup(List<String> groupMembers,String groupId) throws Exception {
        List<GroupMember> groupMemberList=new ArrayList<>();
        for (String memberId:groupMembers) {
            GroupMember groupMember=new GroupMember();
            groupMember.setId(memberId);
            groupMemberList.add(groupMember);
        }
        GroupMember[] members=groupMemberList.toArray(new GroupMember[groupMemberList.size()]);

        GroupModel group = new GroupModel()
                .setId(groupId)
                .setMembers(members);
        Result groupDismissResult = Group.dismiss(group);
        return groupDismissResult;
    }

    /**
     * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/group/group.html#sync
     *
     * 	同步用户所属群组方法
     */
    public Result syncGroup(List<GroupModel> groupModels,String userId) throws Exception {

        UserGroup user = new UserGroup()
                .setId(userId)
                .setGroups(groupModels.toArray(new GroupModel[groupModels.size()]));

        Result syncResult =Group.sync(user);
        logger.info(syncResult.toString());
        return syncResult;
    }

    /**
     *
     * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/group/group.html#refresh
     *  刷新群组信息方法
     */
    public Result refreshGroup(String groupId,String groupName) throws Exception {
        GroupModel group = new GroupModel()
                    .setId(groupId)
                    .setName(groupName);
        Result refreshResult =Group.update(group);
        return refreshResult;
    }


    public static void main(String[] args) throws Exception {
        /**
         * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/group/group.html#getMembers
         *
         * 查询群成员方法
         *
         */
        GroupCloudUtil groupCloudUtil=new GroupCloudUtil();
        groupCloudUtil.dissolutionGroup(new ArrayList<>(Arrays.asList(new String[]{"100133", "1002", "100126","100124","100118"})),"1002");

        GroupModel group = new GroupModel().setId("1002");
        GroupUserQueryResult getMemberesult = groupCloudUtil.Group.get(group);
        System.out.println("group getMember:  " + getMemberesult.toString());
    }
}
