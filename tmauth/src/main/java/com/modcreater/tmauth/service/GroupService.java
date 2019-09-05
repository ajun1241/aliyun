package com.modcreater.tmauth.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.GroupInfoVo;
import com.modcreater.tmbeans.vo.GroupRelationVo;
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

}
