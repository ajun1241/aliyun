package com.modcreater.tmauth.controller;

import com.modcreater.tmauth.config.annotation.Safety;
import com.modcreater.tmauth.service.GroupService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.*;
import com.modcreater.tmbeans.vo.group.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmbeans.vo.uservo.UserIdVo;
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

    @PostMapping(value = "getmygroupmembers")
    @ApiOperation("获取团队成员")
    public Dto getMyGroupMembers(@RequestBody SearchMembersConditions searchMembersConditions, HttpServletRequest request){
        return groupService.getMyGroupMembers(searchMembersConditions,request.getHeader("token"));
    }

    @PostMapping(value = "groupingmembers")
    @ApiOperation("分组获取团队成员")
    public Dto groupingMembers(@RequestBody SearchMembersConditions searchMembersConditions,HttpServletRequest request){
        return groupService.groupingMembers(searchMembersConditions,request.getHeader("token"));
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

    @Safety
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

    @Safety
    @PostMapping(value = "sendgroupcard")
    @ApiOperation("发送团队名片")
    public Dto sendGroupCard(@RequestBody GroupCardVo groupCardVo , HttpServletRequest request){
        return groupService.sendGroupCard(groupCardVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "applyjoingroup")
    @ApiOperation("申请加入团队")
    public Dto applyJoinGroup(@RequestBody GroupApplyVo groupApplyVo , HttpServletRequest request){
        return groupService.applyJoinGroup(groupApplyVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "applyunreadmsglist")
    @ApiOperation("查询团队未处理验证消息列表")
    public Dto applyUnreadMsgList(@RequestBody UserIdVo receivedId , HttpServletRequest request){
        return groupService.applyUnreadMsgList(receivedId,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "applyreadmsglist")
    @ApiOperation("查询团队已处理验证消息列表")
    public Dto applyReadMsgList(@RequestBody UserIdVo receivedId ,HttpServletRequest request){
        return groupService.applyReadMsgList(receivedId,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "applyufmsglist")
    @ApiOperation("查询团队未读验证反馈列表")
    public Dto applyUFMsgList(@RequestBody UserIdVo receivedId , HttpServletRequest request){
        return groupService.applyUFMsgList(receivedId,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "applyrfmsglist")
    @ApiOperation("查询团队已读验证反馈列表")
    public Dto applyRFMsgList(@RequestBody UserIdVo receivedId ,HttpServletRequest request){
        return groupService.applyRFMsgList(receivedId,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "applymsginfo")
    @ApiOperation("查询团队验证消息详情")
    public Dto applyMsgInfo(@RequestBody ApplyMsgInfoVo applyMsgInfoVo , HttpServletRequest request){
        return groupService.applyMsgInfo(applyMsgInfoVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "applyfmsginfo")
    @ApiOperation("查询团队验证反馈详情")
    public Dto applyRFMsgInfo(@RequestBody ApplyMsgInfoVo applyMsgInfoVo , HttpServletRequest request){
        return groupService.applyRFMsgInfo(applyMsgInfoVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "respondapply")
    @ApiOperation("回应团队申请")
    public Dto respondApply(@RequestBody GroupApplyDisposeVo groupApplyDisposeVo , HttpServletRequest request){
        return groupService.respondApply(groupApplyDisposeVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "sendinviteevent")
    @ApiOperation("发送邀请事件至团队")
    public Dto sendInviteEvent(@RequestBody SendInviteEventVo sendInviteEventVo , HttpServletRequest request){
        return groupService.sendInviteEvent(sendInviteEventVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "feedbackgroupevent")
    @ApiOperation("回应团队事件邀请申请")
    public Dto feedbackGroupEvent(@RequestBody FeedbackGroupEventVo feedbackGroupEventVo , HttpServletRequest request){
        return groupService.feedbackGroupEvent(feedbackGroupEventVo,request.getHeader("token"));
    }

    @PostMapping(value = "addnewmembers")
    @ApiOperation("添加成员")
    public Dto addNewMembers(@RequestBody AddNewMembers addNewMembers ,HttpServletRequest request){
        return groupService.addNewMembers(addNewMembers,request.getHeader("token"));
    }

    @PostMapping(value = "checkrole")
    @ApiOperation("查看成员角色")
    public Dto checkRole(@RequestBody ReceivedGroupId receivedGroupId,HttpServletRequest request){
        return groupService.checkRole(receivedGroupId,request.getHeader("token"));
    }

    @PostMapping(value = "getgroupeventmsg")
    @ApiOperation("获取团队历史事件")
    public Dto getGroupEventMsg(@RequestBody ReceivedGroupId receivedGroupId,HttpServletRequest request){
        return groupService.getGroupEventMsg(receivedGroupId,request.getHeader("token"));
    }

    @PostMapping(value = "getgroupeventmsginfo")
    @ApiOperation("获取团队历史事件详情")
    public Dto getGroupEventMsgInfo(@RequestBody ReceivedGroupEventMsgId receivedGroupEventMsgId,HttpServletRequest request){
        return groupService.getGroupEventMsgInfo(receivedGroupEventMsgId,request.getHeader("token"));
    }

    @PostMapping(value = "breakgroup")
    @ApiOperation("解散团队")
    public Dto breakGroup(@RequestBody ReceivedGroupId receivedGroupId,HttpServletRequest request){
        return groupService.breakGroup(receivedGroupId,request.getHeader("token"));
    }


}
