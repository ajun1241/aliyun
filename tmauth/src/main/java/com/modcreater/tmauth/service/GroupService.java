package com.modcreater.tmauth.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.*;
import com.modcreater.tmbeans.vo.group.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;

/**
 * Description:
 *  团队
 * @Author: AJun
 * @Date: 2019/9/5 16:06
 */
public interface GroupService {

    /**
     * 创建团队
     * @param groupInfoVo
     * @param token
     * @return
     */
    Dto createGroup(GroupInfoVo groupInfoVo,String token);

    /**
     * 查询与我有关的团队列表
     * @param receivedId
     * @param token
     * @return
     */
    Dto queryGroupList(ReceivedId receivedId,String token);

    /**
     * 修改团队信息
     * @param updateGroupInfo
     * @param token
     * @return
     */
    Dto updateGroup(UpdateGroupInfo updateGroupInfo, String token);

    /**
     * 解散团队
     * @param receivedGroupId
     * @param token
     * @return
     */
    Dto deleteGroup(ReceivedGroupId receivedGroupId,String token);

    /**
     * 移除团队成员
     * @param groupRelationVo
     * @param token
     * @return
     */
    Dto deleteGroupMember(GroupRelationVo groupRelationVo,String token);

    /**
     * 获取我创建的团队的数量
     * @param receivedId
     * @param token
     * @return
     */
    Dto isNeedValueAdded(ReceivedId receivedId, String token);

    /**
     * 发送团队名片
     * @param groupCardVo
     * @param token
     * @return
     */
    Dto sendGroupCard(GroupCardVo groupCardVo, String token);

    /**
     * 申请加入团队
     * @param groupApplyVo
     * @param token
     * @return
     */
    Dto applyJoinGroup(GroupApplyVo groupApplyVo, String token);

    /**
     * 回应团队申请
     * @param groupApplyDisposeVo
     * @param token
     * @return
     */
    Dto respondApply(GroupApplyDisposeVo groupApplyDisposeVo, String token);

    /**
     * 查询团队验证消息列表
     * @param receivedId
     * @param token
     * @return
     */
    Dto applyMsgList(ReceivedId receivedId,String token);

    /**
     * 查询团队验证消息详情
     * @param receivedId
     * @param token
     * @return
     */
//    Dto applyMsgInfo(ReceivedId receivedId,String token);

    /**
     * 查询团队详细信息
     * @param receivedGroupId
     * @param token
     * @return
     */
    Dto getMyGroupInfo(ReceivedGroupId receivedGroupId, String token);

    /**
     * 获取管理员数量
     * @param receivedGroupId
     * @param token
     * @return
     */
    Dto getManagerNum(ReceivedGroupId receivedGroupId, String token);

    /**
     * 获取管理员信息
     * @param receivedGroupId
     * @param token
     * @return
     */
    Dto getManagerInfo(ReceivedGroupId receivedGroupId, String token);

    /**
     * 移除管理员
     * @param removeManager
     * @param token
     * @return
     */
    Dto removeManager(RemoveManager removeManager, String token);

    /**
     * 添加管理员
     * @param addManager
     * @param token
     * @return
     */
    Dto addManager(AddManager addManager, String token);

    /**
     * 移除成员
     * @param removeMember
     * @param token
     * @return
     */
    Dto removeMember(RemoveMember removeMember, String token);

    /**
     * 成员退出团队
     * @param memberQuitGroup
     * @param token
     * @return
     */
    Dto memberQuitGroup(MemberQuitGroup memberQuitGroup, String token);

    /**
     * 转让团队
     * @param groupMakeOver
     * @param token
     * @return
     */
    Dto groupMakeOver(GroupMakeOver groupMakeOver, String token);

    /**
     * 获取群成员信息
     * @param receivedGroupId
     * @param token
     * @return
     */
    Dto getGroupMembers(ReceivedGroupId receivedGroupId, String token);

    /**
     * 添加团队成员
     * @param addNewMembers
     * @param token
     * @return
     */
    Dto addNewMembers(AddNewMembers addNewMembers, String token);

    /**
     * 查看成员角色
     * @param receivedGroupId
     * @param token
     * @return
     */
    Dto checkRole(ReceivedGroupId receivedGroupId, String token);

    /**
     * 获取团队历史事件
     * @param receivedGroupId
     * @param token
     * @return
     */
    Dto getGroupEventMsg(ReceivedGroupId receivedGroupId, String token);
}
