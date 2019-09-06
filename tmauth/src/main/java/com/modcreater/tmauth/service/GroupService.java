package com.modcreater.tmauth.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.GroupInfoVo;
import com.modcreater.tmbeans.vo.GroupMsgVo;
import com.modcreater.tmbeans.vo.GroupRelationVo;
import com.modcreater.tmbeans.vo.group.ReceivedGroupId;
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
     * 团队设置
     * @param groupInfoVo
     * @param token
     * @return
     */
    Dto updateGroup(GroupInfoVo groupInfoVo,String token);

    /**
     * 解散团队
     * @param groupInfoVo
     * @param token
     * @return
     */
    Dto deleteGroup(GroupInfoVo groupInfoVo,String token);

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
     * @param groupMsgVo
     * @param token
     * @return
     */
    Dto sendGroupCard(GroupMsgVo groupMsgVo, String token);

    /**
     * 申请加入团队
     * @param groupMsgVo
     * @param token
     * @return
     */
    Dto applyJoinGroup(GroupMsgVo groupMsgVo,String token);

    /**
     * 回应团队申请
     * @param groupMsgVo
     * @param token
     * @return
     */
    Dto respondApply(GroupMsgVo groupMsgVo,String token);

    /**
     * 查询团队验证消息列表
     * @param receivedId
     * @param token
     * @return
     */
    Dto applyMsgList(ReceivedId receivedId,String token);

    /**
     * 查询团队详细信息
     * @param receivedGroupId
     * @param token
     * @return
     */
    Dto getMyGroupInfo(ReceivedGroupId receivedGroupId, String token);
}
