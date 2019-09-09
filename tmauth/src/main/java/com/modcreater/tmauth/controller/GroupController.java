package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.service.GroupService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.GroupInfoVo;
import com.modcreater.tmbeans.vo.group.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-06
 * Time: 9:45
 */
@RestController
@RequestMapping(value = "/group/")
public class GroupController {

    @Resource
    private GroupService groupService;

    @PostMapping(value = "creategroup")
    @ApiOperation("创建团队")
    public Dto createGroup(@RequestBody GroupInfoVo groupInfoVo,HttpServletRequest request){
        return groupService.createGroup(groupInfoVo,request.getHeader("token"));
    }

    @PostMapping(value = "getmygroup")
    @ApiOperation("获取我的团队")
    public Dto getMyGroup(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return groupService.queryGroupList(receivedId,request.getHeader("token"));
    }

    @PostMapping(value = "isneedvalueadded")
    @ApiOperation("获取我创建的团队的数量")
    public Dto isNeedValueAdded(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return groupService.isNeedValueAdded(receivedId,request.getHeader("token"));
    }

    @PostMapping(value = "getmygroupinfo")
    @ApiOperation("获取团队详细信息")
    public Dto getMyGroupInfo(@RequestBody ReceivedGroupId receivedGroupId, HttpServletRequest request){
        return groupService.getMyGroupInfo(receivedGroupId,request.getHeader("token"));
    }

    @PostMapping(value = "updategroupinfo")
    @ApiOperation("修改团队信息")
    public Dto updateGroupInfo(@RequestBody UpdateGroupInfo updateGroupInfo, HttpServletRequest request){
        return groupService.updateGroup(updateGroupInfo,request.getHeader("token"));
    }

    @PostMapping(value = "getmanagernum")
    @ApiOperation("获取管理数量")
    public Dto getManagerNum(@RequestBody ReceivedGroupId receivedGroupId,HttpServletRequest request){
        return groupService.getManagerNum(receivedGroupId,request.getHeader("token"));
    }

    @PostMapping(value = "getmanagerinfo")
    @ApiOperation("获取管理员信息")
    public Dto getManagerInfo(@RequestBody ReceivedGroupId receivedGroupId,HttpServletRequest request){
        return groupService.getManagerInfo(receivedGroupId,request.getHeader("token"));
    }

    @PostMapping(value = "removemanager")
    @ApiOperation("移除管理员")
    public Dto removeManager(@RequestBody RemoveManager removeManager, HttpServletRequest request){
        return groupService.removeManager(removeManager,request.getHeader("token"));
    }

    @PostMapping(value = "removemember")
    @ApiOperation("移除成员")
    public Dto removeMember(@RequestBody RemoveMember removeMember, HttpServletRequest request){
        return groupService.removeMember(removeMember,request.getHeader("token"));
    }

    @PostMapping(value = "addmanager")
    @ApiOperation("添加管理员")
    public Dto addManager(@RequestBody AddManager addManager, HttpServletRequest request){
        return groupService.addManager(addManager,request.getHeader("token"));
    }

    @PostMapping(value = "memberquitgroup")
    @ApiOperation("成员退出团队")
    public Dto memberQuitGroup(@RequestBody MemberQuitGroup memberQuitGroup,HttpServletRequest request){
        return groupService.memberQuitGroup(memberQuitGroup,request.getHeader("token"));
    }

    @PostMapping(value = "groupmakeover")
    @ApiOperation("转让团队")
    public Dto groupMakeOver(@RequestBody GroupMakeOver groupMakeOver, HttpServletRequest request){
        return groupService.groupMakeOver(groupMakeOver,request.getHeader("token"));
    }

    @PostMapping(value = "getgroupmembers")
    @ApiOperation("获取群成员信息")
    public Dto getGroupMembers(@RequestBody ReceivedGroupId receivedGroupId ,HttpServletRequest request){
        return groupService.getGroupMembers(receivedGroupId,request.getHeader("token"));
    }
}
