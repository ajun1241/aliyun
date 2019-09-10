package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.GroupService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.*;
import com.modcreater.tmbeans.show.group.ShowGroupInfo;
import com.modcreater.tmbeans.show.group.ShowMyGroup;
import com.modcreater.tmbeans.values.FinalValues;
import com.modcreater.tmbeans.vo.*;
import com.modcreater.tmbeans.vo.group.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmdao.mapper.GroupMapper;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmutils.GroupCloudUtil;
import com.modcreater.tmutils.RongCloudMethodUtil;
import com.modcreater.tmutils.messageutil.ApplyJoinGroupMsg;
import com.modcreater.tmutils.messageutil.GroupCardMsg;
import io.rong.messages.TxtMessage;
import io.rong.models.Result;
import io.rong.models.response.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/9/6 9:07
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class GroupServiceImpl implements GroupService {

    private Logger logger = LoggerFactory.getLogger(GroupServiceImpl.class);
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private GroupMapper groupMapper;

    @Resource
    private AccountMapper accountMapper;

    private GroupCloudUtil groupCloudUtil=new GroupCloudUtil();

    private RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();

    @Override
    public synchronized Dto createGroup(GroupInfoVo groupInfoVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(groupInfoVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        //0 : 不需要; 1 : 需要
        int status = 1;
        GroupPermission groupPermission = groupMapper.getGroupUpperLimit(groupInfoVo.getUserId());
        if (groupMapper.getMyCreatedGroupNum(groupInfoVo.getUserId()) < groupPermission.getGroupUpperLimit()) {
            status = 0;
        }
        if (status == 1){
            return DtoUtil.getSuccesWithDataDto("创建团队数量已达到上限",status,100000);
        }
        try {
            if (!StringUtils.hasText(groupInfoVo.getGroupNature())) {
                groupInfoVo.setGroupNature("7");
            }
            if (!StringUtils.hasText(groupInfoVo.getGroupPicture())) {
                groupInfoVo.setGroupPicture(groupMapper.getGroupDefultHeadImgUrl(groupInfoVo.getGroupNature()));
            }
            groupMapper.createGroup(groupInfoVo);
            int i = groupMapper.addCreator(groupInfoVo.getUserId(), groupInfoVo.getId());
            String msgInfo = "您已成为团队\"" + groupInfoVo.getGroupName() + "\"的成员";
            if (i == 1) {
                for (String memberId : groupInfoVo.getMembers()) {
                    groupMapper.createMember(memberId, groupInfoVo.getId());
                    RongCloudMethodUtil rong = new RongCloudMethodUtil();
                    ResponseResult responseResult = rong.sendPrivateMsg("100000", new String[]{memberId}, 0, new TxtMessage(msgInfo, null));
                    if (responseResult.getCode() != 200) {
                        logger.warn("添加团队成员时融云消息异常" + responseResult.toString());
                    }
                }
                return DtoUtil.getSuccesWithDataDto("创建成功", status, 100000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getSuccessDto("创建失败", 80001);
        }
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return DtoUtil.getSuccessDto("创建失败", 80001);
    }

    @Override
    public Dto queryGroupList(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        Map<String, Map<String, Object>> result = new HashMap<>(3);
        for (int i = 0; i <= 2; i++) {
            Map<String, Object> map = new HashMap<>();
            List<ShowMyGroup> showMyGroups = groupMapper.getMyGroup(receivedId.getUserId(), i);
            if (i == 2) {
                GroupPermission groupPermission = groupMapper.getGroupUpperLimit(receivedId.getUserId());
                if (ObjectUtils.isEmpty(groupPermission)) {
                    long groupUpperLimit = 5;
                    groupMapper.addGroupPermission(receivedId.getUserId(), groupUpperLimit);
                    map.put("totalNum", groupUpperLimit);
                } else {
                    map.put("totalNum", groupPermission.getGroupUpperLimit());
                }
        }
            map.put("num", showMyGroups.size());
            map.put("list", showMyGroups);
            result.put(FinalValues.GROUPROLES[i], map);
        }
        return DtoUtil.getSuccesWithDataDto("操作成功",result,100000);
    }

    @Override
    public Dto updateGroup(UpdateGroupInfo updateGroupInfo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(updateGroupInfo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        GroupInfo groupInfo = groupMapper.queryGroupInfo(updateGroupInfo.getGroupId());
        if (!isHavePermission(updateGroupInfo.getGroupId(),updateGroupInfo.getUserId())){
            return DtoUtil.getFalseDto("您没有操作权限",80004);
        }
        if ("groupScale".equals(updateGroupInfo.getUpdateType()) && groupInfo.getGroupScale() > Long.valueOf(updateGroupInfo.getValue())) {
            return DtoUtil.getSuccessDto("团队规模不能小于团队现有人数", 80002);
        } else if ("groupNature".equals(updateGroupInfo.getUpdateType()) && !groupInfo.getGroupNature().toString().equals(updateGroupInfo.getValue())) {
            System.out.println("进入groupNature");
            List<String> urls = groupMapper.getAllGroupDefultHeadImgUrls();
            for (String url : urls){
                if (url.equals(groupInfo.getGroupPicture())){
                    String urlValue = groupMapper.getGroupDefultHeadImgUrl(updateGroupInfo.getValue());
                    groupMapper.updateGroupInfo(updateGroupInfo.getGroupId(),"groupPicture",urlValue);
                }
            }
        }

        int i = groupMapper.updateGroupInfo(updateGroupInfo.getGroupId(),updateGroupInfo.getUpdateType(),updateGroupInfo.getValue());
        if (i == 1){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return DtoUtil.getSuccessDto("修改失败",80002);
    }

    @Override
    public Dto deleteGroup(GroupInfoVo groupInfoVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(groupInfoVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        return null;
    }

    @Override
    public Dto deleteGroupMember(GroupRelationVo groupRelationVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(groupRelationVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        return null;
    }

    @Override
    public Dto isNeedValueAdded(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        return null;
    }

    /**
     * 发送团队名片
     * @param groupCardVo
     * @param token
     * @return
     */
    @Override
    public Dto sendGroupCard(GroupCardVo groupCardVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(groupCardVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        //查询团队信息
        GroupInfo groupInfo=groupMapper.queryGroupInfo(groupCardVo.getGroupId());
        try {
            RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
            GroupCardMsg groupCardMsg =new GroupCardMsg(groupInfo.getId().toString(),groupInfo.getGroupName(),groupInfo.getGroupPicture(),groupInfo.getGroupUnit());
            ResponseResult result=rongCloudMethodUtil.sendPrivateMsg(groupCardVo.getUserId(), groupCardVo.getTargetId(),1, groupCardMsg);
            if (result.getCode()!=200){
                return DtoUtil.getFalseDto("发送失败",17002);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return DtoUtil.getFalseDto("发送失败",17003);
        }
        return DtoUtil.getSuccessDto("发送成功",100000);
    }

    /**
     * 申请加入团队
     * @param groupApplyVo
     * @param token
     * @return
     */
    @Override
    public Dto applyJoinGroup(GroupApplyVo groupApplyVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(groupApplyVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        //查询用户详情
        Account account=accountMapper.queryAccount(groupApplyVo.getUserId());
        if (!ObjectUtils.isEmpty(account)){
            try {
                //查询管理员和群主
                List<GroupRelation> groupRelationList=groupMapper.queryGroupRelation(groupApplyVo.getGroupId());
                List<String> userIds=new ArrayList<>();
                for (GroupRelation groupRelation:groupRelationList) {
                    if (groupRelation.getMemberLevel().equals(1L) || groupRelation.getMemberLevel().equals(2L)){
                        userIds.add(groupRelation.getMemberId().toString());
                    }
                }
                //验证内容保存
                GroupValidation groupValidation=new GroupValidation();
                groupValidation.setUserId(Long.parseLong(groupApplyVo.getUserId()));
                groupValidation.setValidationContent(groupApplyVo.getValidationContent());
                groupValidation.setValidationSource(groupApplyVo.getSourceId());
//                groupMapper.saveValidationContent(groupValidation);
                for (String userId:userIds){
                    //消息保存
                    GroupSystemMsg groupSystemMsg=new GroupSystemMsg();
                    groupSystemMsg.setSenderId(Long.parseLong(groupApplyVo.getUserId()));
                    groupSystemMsg.setReceiverId(Long.parseLong(userId));
                    /*groupSystemMsg.setMsgContent();*/
                    groupSystemMsg.setGroupValidationId(groupValidation.getId());
                    groupMapper.saveGroupMsg(groupSystemMsg);
                    //发送申请消息
                    RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
                    ApplyJoinGroupMsg applyJoinGroupMsg =new ApplyJoinGroupMsg(account.getId().toString(),account.getUserName(),account.getHeadImgUrl(),account.getUserCode(), groupApplyVo.getValidationContent(),groupSystemMsg.getId().toString());
                    ResponseResult result=rongCloudMethodUtil.sendPrivateMsg(groupApplyVo.getUserId(),new String[]{userId},1, applyJoinGroupMsg);
                    if (result.getCode()!=200){
                        return DtoUtil.getFalseDto("发送失败",17002);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
                return DtoUtil.getFalseDto("发送失败",17003);
            }
        }
        return DtoUtil.getSuccessDto("您的请求已发送，请耐心等待",100000);
    }

    /**
     * 回应团队申请
     * @param groupCardVo
     * @param token
     * @return
     */
    @Override
    public Dto respondApply(GroupApplyDisposeVo groupCardVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(groupCardVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        /*try {
            GroupInfo groupInfo=groupMapper.queryGroupInfo(groupCardVo.getGroupId());
            if (ObjectUtils.isEmpty(groupInfo)){
                return DtoUtil.getFalseDto("要加入的团队不存在，可能已解散",26046);
            }
            //查询消息状态
            GroupSystemMsg groupSystemMsg=groupMapper.getGroupMsgById(groupCardVo.getGroupMsgId());
            if (!StringUtils.isEmpty(groupCardVo.getChoose())){
                if ("1".equals(groupCardVo.getChoose())){
                    //同意
                    //查询该成员是否已加入
                    GroupRelation groupRelation=groupMapper.queryGroupMember(groupCardVo.getGroupId(), groupCardVo.getMemberId());
                    if (!ObjectUtils.isEmpty(groupRelation)){
                        return DtoUtil.getFalseDto("该用户已加入此团队",26045);
                    }
                    //数据库添加群关系
                    groupMapper.createMember(groupCardVo.getMemberId(),Long.valueOf(groupCardVo.getGroupId()));
                    //融云同步群关系
                    List<String> list=new ArrayList<>();
                    list.add(groupCardVo.getMemberId());
                    Result result=groupCloudUtil.joinGroup(list, groupCardVo.getGroupId(),groupInfo.getGroupName());
                    if (result.getCode()!=200){
                        logger.error("融云同步群关系失败，错误信息："+result);
                    }
                    //修改消息状态
//                    groupMapper.getGroupMsgById(groupCardVo.getGroupMsgId(),"1");
                    //发送反馈信息

                }else {
                    //拒绝
                    //修改消息状态

                    //发送反馈信息
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }*/
        return null;
    }

    /**
     * 查询团队验证消息列表
     * @param receivedId
     * @param token
     * @return
     */
    @Override
    public Dto applyMsgList(ReceivedId receivedId, String token) {
        return null;
    }

    @Override
    public Dto getMyGroupInfo(ReceivedGroupId receivedGroupId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedGroupId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        ShowGroupInfo showGroupInfo = groupMapper.getMyGroupInfo(receivedGroupId.getGroupId());
        List<Map<String,Object>> membersInfo = new ArrayList<>();
        if (!ObjectUtils.isEmpty(showGroupInfo)){
            List<Long> memberIds = groupMapper.getMembersInfo(receivedGroupId.getGroupId());
            for (Long memberId : memberIds){
                Map<String,Object> map = new HashMap<>();
                Account account = accountMapper.queryAccount(memberId.toString());
                map.put("memberId",account.getId());
                map.put("memberName",account.getUserName());
                map.put("memberHeadImgUrl",account.getHeadImgUrl());
                membersInfo.add(map);
            }
            showGroupInfo.setMembersInfo(membersInfo);
            showGroupInfo.setMembersNum((long) memberIds.size());
            return DtoUtil.getSuccesWithDataDto("查询成功",showGroupInfo,100000);
        }
        return DtoUtil.getSuccessDto("查询失败",200000);
    }

    @Override
    public Dto getManagerNum(ReceivedGroupId receivedGroupId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedGroupId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",groupMapper.getManagerNum(receivedGroupId.getGroupId()),100000);
    }

    @Override
    public Dto getManagerInfo(ReceivedGroupId receivedGroupId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedGroupId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        List<Map<String,Object>> managersInfo = groupMapper.getManagerInfo(receivedGroupId.getGroupId());
        Map<String,Object> creatorInfo = groupMapper.getCreatorInfo(receivedGroupId.getGroupId());
        Map<String,Object> result = new HashMap<>();
        result.put("creatorInfo",creatorInfo);
        result.put("managersInfo",managersInfo);
        return DtoUtil.getSuccesWithDataDto("查询成功",result,100000);
    }

    @Override
    public Dto removeManager(RemoveManager removeManager, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(removeManager.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (groupMapper.getMemberLevel(removeManager.getGroupId(),removeManager.getUserId()) != 2){
            return DtoUtil.getFalseDto("您没有操作权限",80004);
        }
        if (groupMapper.updateMemberLevel(removeManager.getGroupId(),removeManager.getManagerId(),0) != 1){
            return DtoUtil.getFalseDto("添加管理员操作失败",80003);
        }
        GroupInfo groupInfo = groupMapper.queryGroupInfo(removeManager.getGroupId());
        String msgInfo = "您已被取消团队\""+groupInfo.getGroupName()+"\"的管理员";
        RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
        try {
            ResponseResult result = rongCloudMethodUtil.sendPrivateMsg("100000",new String[]{removeManager.getManagerId()},0,new TxtMessage(msgInfo,null));
            if (result.getCode() != 200){
                logger.warn("取消团队管理员时融云消息异常" + result.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DtoUtil.getSuccessDto("移除管理员操作成功",100000);
    }

    @Override
    public Dto addManager(AddManager addManager, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(addManager.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (groupMapper.getMemberLevel(addManager.getGroupId(),addManager.getUserId()) != 2){
            return DtoUtil.getFalseDto("您没有操作权限",80004);
        }
        if (groupMapper.updateMemberLevel(addManager.getGroupId(),addManager.getMemberId(),1) != 1){
            return DtoUtil.getFalseDto("添加管理员操作失败",80003);
        }
        GroupInfo groupInfo = groupMapper.queryGroupInfo(addManager.getGroupId());
        String msgInfo = "您已成为团队\""+groupInfo.getGroupName()+"\"的管理员";
        RongCloudMethodUtil rongCloudMethodUtil = new RongCloudMethodUtil();
        try {
            ResponseResult result = rongCloudMethodUtil.sendPrivateMsg("100000",new String[]{addManager.getMemberId()},0,new TxtMessage(msgInfo,null));
            if (result.getCode() != 200){
                logger.warn("添加团队管理员时融云消息异常" + result.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DtoUtil.getSuccessDto("添加管理员操作成功",100000);
    }

    @Override
    public synchronized Dto removeMember(RemoveMember removeMember, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(removeMember.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!isHavePermission(removeMember.getGroupId(),removeMember.getUserId())){
            return DtoUtil.getFalseDto("您没有操作权限",80004);
        }
        int handlerLevel = groupMapper.getMemberLevel(removeMember.getGroupId(),removeMember.getUserId());
        for (String memberId : removeMember.getMemberId()){
            int memberLevel = groupMapper.getMemberLevel(removeMember.getGroupId(),memberId);
            boolean b1 = handlerLevel == 2 && (memberLevel == 1 || memberLevel == 0);
            boolean b2 = handlerLevel == 1 && memberLevel == 0;
            if (b1 || b2){
                if (groupMapper.removeMember(removeMember.getGroupId(),memberId) != 1){
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return DtoUtil.getFalseDto("移除成员失败",80005);
                }
                String msgInfo = "您已被团队" + (handlerLevel == 2 ? "创建者" : "管理员") + "移出团队";
                RongCloudMethodUtil rong = new RongCloudMethodUtil();
                try {
                    ResponseResult result = rong.sendPrivateMsg("100000",new String[]{memberId},0,new TxtMessage(msgInfo,null));
                    if (result.getCode() != 200){
                        logger.warn("移除团队成员时融云消息异常" + result.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return DtoUtil.getFalseDto("违规操作!",80004);
            }
        }
        return DtoUtil.getSuccessDto("操作成功",100000);
    }

    @Override
    public Dto memberQuitGroup(MemberQuitGroup memberQuitGroup, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(memberQuitGroup.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (groupMapper.removeMember(memberQuitGroup.getGroupId(),memberQuitGroup.getUserId()) != 1){
            return DtoUtil.getFalseDto("操作失败",80005);
        }
        GroupInfo groupInfo = groupMapper.queryGroupInfo(memberQuitGroup.getGroupId());
        String msgInfo = "您已退出团队\"" + groupInfo.getGroupName() + "\"";
        RongCloudMethodUtil rong = new RongCloudMethodUtil();
        try {
            ResponseResult result = rong.sendPrivateMsg("100000",new String[]{memberQuitGroup.getUserId()},0,new TxtMessage(msgInfo,null));
            if (result.getCode() != 200){
                logger.warn("移除团队成员时融云消息异常" + result.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DtoUtil.getSuccessDto("操作成功",100000);
    }

    @Override
    public Dto groupMakeOver(GroupMakeOver groupMakeOver, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(groupMakeOver.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (groupMapper.getMemberLevel(groupMakeOver.getGroupId(),groupMakeOver.getUserId()) != 2){
            return DtoUtil.getFalseDto("违规操作!",80004);
        }
        int beCreator = groupMapper.updateMemberLevel(groupMakeOver.getGroupId(),groupMakeOver.getMemberId(),2);
        int beManager = groupMapper.updateMemberLevel(groupMakeOver.getGroupId(),groupMakeOver.getUserId(),0);
        if (beCreator + beManager != 2){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getFalseDto("操作失败",80005);
        }
        RongCloudMethodUtil rong = new RongCloudMethodUtil();
        GroupInfo groupInfo = groupMapper.queryGroupInfo(groupMakeOver.getGroupId());
        Account account = accountMapper.queryAccount(groupMakeOver.getMemberId());
        try {
            String msgInfo1 = "\"" + groupInfo.getGroupName() + "\"的创建者已将团队转让给你";
            ResponseResult result1 = rong.sendPrivateMsg("100000", new String[]{groupMakeOver.getMemberId()}, 0, new TxtMessage(msgInfo1, null));
            if (result1.getCode() != 200){
                logger.warn("成为团队创建者时融云消息异常" + result1.toString());
            }
            String msgInfo2 = "您已成功将团队\"" + groupInfo.getGroupName() + "\"转让给" + account.getUserName();
            ResponseResult result2 = rong.sendPrivateMsg("100000", new String[]{groupMakeOver.getUserId()}, 0, new TxtMessage(msgInfo2, null));
            if (result1.getCode() != 200){
                logger.warn("转让团队时融云消息异常" + result2.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DtoUtil.getSuccessDto("操作成功",100000);
    }

    @Override
    public Dto getGroupMembers(ReceivedGroupId receivedGroupId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedGroupId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        Map<String ,Object> result = new HashMap<>();
        for (int i = 2; i >= 0; i--) {
            if (i == 2){
                List<Map<String ,Object>> account = groupMapper.queryGroupMemberInfoByLevel(receivedGroupId.getGroupId(),i);
                if (account.size() > 0){
                    result.put(FinalValues.GROUPROLES[i],account.get(0));
                }else {
                    result.put(FinalValues.GROUPROLES[i],null);
                }
            }else {
                result.put(FinalValues.GROUPROLES[i],groupMapper.queryGroupMemberInfoByLevel(receivedGroupId.getGroupId(),i));
            }
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",result,100000);
    }

    /**
     * 判断用户是否有操作团队信息的权限
     * @param groupId
     * @param userId
     * @return
     */
    private boolean isHavePermission(String groupId, String userId){
        int level = groupMapper.getMemberLevel(groupId,userId);
        return level == 2 || level == 1;
    }
}
