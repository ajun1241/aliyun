package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.GroupService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.GroupInfoVo;
import com.modcreater.tmbeans.vo.GroupRelationVo;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import org.springframework.stereotype.Service;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/9/6 9:07
 */
@Service
public class GroupServiceImpl implements GroupService {

    @Override
    public Dto createGroup(GroupInfoVo groupInfoVo, String token) {
        return null;
    }

    @Override
    public Dto queryGroupList(ReceivedId receivedId, String token) {
        return null;
    }

    @Override
    public Dto updateGroup(GroupInfoVo groupInfoVo, String token) {
        return null;
    }

    @Override
    public Dto deleteGroup(GroupInfoVo groupInfoVo, String token) {
        return null;
    }

    @Override
    public Dto deleteGroupMember(GroupRelationVo groupRelationVo, String token) {
        return null;
    }
}
