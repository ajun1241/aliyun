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

    private static final String SYSTEM_ID="100000";
    private static final String VALIDATION_ID ="100001";
    private static final String VALIDATION_FEEDBACK_ID="100002";


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
                groupInfoVo.setGroupPicture(groupMapper.getGroupDefaultHeadImgUrl(groupInfoVo.getGroupNature()));
            }
            groupMapper.createGroup(groupInfoVo);
            int i = groupMapper.addCreator(groupInfoVo.getUserId(), groupInfoVo.getId());
            String msgInfo = "您已成为团队\"" + groupInfoVo.getGroupName() + "\"的成员";
            if (i == 1) {
                for (String memberId : groupInfoVo.getMembers()) {
                    groupMapper.createMember(memberId, groupInfoVo.getId());
                    RongCloudMethodUtil rong = new RongCloudMethodUtil();
                    ResponseResult responseResult = rong.sendPrivateMsg(SYSTEM_ID, new String[]{memberId}, 0, new TxtMessage(msgInfo, null));
                    if (responseResult.getCode() != 200) {
                        logger.warn("添加团队成员时融云消息异常" + responseResult.toString());
                    }
                }
                List<String> members = new ArrayList<>(Arrays.asList(groupInfoVo.getMembers()));
                members.add(groupInfoVo.getUserId());
                Result result = groupCloudUtil.createGroup(members,groupInfoVo.getId().toString(),groupInfoVo.getGroupName());
                if (result.getCode() != 200){
                    logger.warn("注册团队时异常" + result.toString());
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return DtoUtil.getFalseDto("创建团队时发生错误",80006);
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
                    String urlValue = groupMapper.getGroupDefaultHeadImgUrl(updateGroupInfo.getValue());
                    groupMapper.updateGroupInfo(updateGroupInfo.getGroupId(),"groupPicture",urlValue);
                }
            }
        }

        int i = groupMapper.updateGroupInfo(updateGroupInfo.getGroupId(),updateGroupInfo.getUpdateType(),updateGroupInfo.getValue());
        if (i == 1){
            if ("groupName".equals(updateGroupInfo.getUpdateType()) && groupInfo.getGroupName().equals(updateGroupInfo.getValue())){
                try {
                    Result result = groupCloudUtil.refreshGroup(groupInfo.getId().toString(),updateGroupInfo.getValue());
                    if (result.getCode() != 200){
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        logger.warn("刷新团队信息时融云消息异常" + result.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return DtoUtil.getSuccessDto("修改失败",80002);
    }

    @Override
    public Dto deleteGroup(ReceivedGroupId receivedGroupId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedGroupId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        int level = groupMapper.getMemberLevel(receivedGroupId.getGroupId(),receivedGroupId.getUserId());
        if (level != 2){
            return DtoUtil.getFalseDto("违规操作!",80004);
        }
        GroupInfo groupInfo = groupMapper.queryGroupInfo(receivedGroupId.getGroupId());
        int deleteNum = groupMapper.removeAllMember(receivedGroupId.getGroupId());
        int delete = groupMapper.removeGroup(receivedGroupId.getGroupId());
        if (deleteNum >= 3 && delete == 1){
            List<String> membersId = groupMapper.getMembersId(receivedGroupId.getGroupId());
            membersId.remove(0);
            RongCloudMethodUtil rong = new RongCloudMethodUtil();
            String msgInfo = "团队\"" + groupInfo.getGroupName() + "\"已被群主解散";
            for (String memberId : membersId){
                try {
                    ResponseResult result = rong.sendPrivateMsg(SYSTEM_ID,new String[]{memberId},0,new TxtMessage(msgInfo,null));
                    if (result.getCode() != 200){
                        logger.warn("发送解散融云消息异常" + result.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Result result1 = groupCloudUtil.dissolutionGroup(membersId,receivedGroupId.getGroupId());
                if (result1.getCode() != 200){
                    logger.warn("解散团队异常" + result1.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return DtoUtil.getSuccessDto("团队解散成功",100000);
        }
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return DtoUtil.getFalseDto("解散团队失败",80008);
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
        Account source=accountMapper.queryAccount(groupApplyVo.getSourceId());
        //查询团队详情
        GroupInfo groupInfo=groupMapper.queryGroupInfo(groupApplyVo.getGroupId());
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
                String validationSource="由用户"+source.getUserName()+"介绍";
                //验证内容保存
                GroupValidation groupValidation=new GroupValidation();
                groupValidation.setUserId(Long.parseLong(groupApplyVo.getUserId()));
                groupValidation.setValidationContent(groupApplyVo.getValidationContent());
                groupValidation.setValidationSource(validationSource);
                groupMapper.saveValidationContent(groupValidation);
                for (String userId:userIds){
                    //消息保存
                    GroupSystemMsg groupSystemMsg=new GroupSystemMsg();
                    groupSystemMsg.setSenderId(Long.parseLong(groupApplyVo.getUserId()));
                    groupSystemMsg.setReceiverId(Long.parseLong(userId));
                    groupSystemMsg.setMsgContent("用户"+account.getUserName()+"申请加入团队"+groupInfo.getGroupName());
                    groupSystemMsg.setGroupValidationId(groupValidation.getId());
                    groupMapper.saveGroupMsg(groupSystemMsg);
                    //发送申请消息
                    RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
                    ApplyJoinGroupMsg applyJoinGroupMsg =new ApplyJoinGroupMsg(account.getId().toString(),account.getUserName(),account.getHeadImgUrl(),
                            account.getUserCode(), groupApplyVo.getValidationContent(),groupSystemMsg.getId().toString(),validationSource);
                    ResponseResult result=rongCloudMethodUtil.sendPrivateMsg(VALIDATION_ID,new String[]{userId},0, applyJoinGroupMsg);
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
     * @param groupApplyDisposeVo
     * @param token
     * @return
     */
    @Override
    public Dto respondApply(GroupApplyDisposeVo groupApplyDisposeVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(groupApplyDisposeVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        try {
            GroupInfo groupInfo=groupMapper.queryGroupInfo(groupApplyDisposeVo.getGroupId());
            if (ObjectUtils.isEmpty(groupInfo)){
                return DtoUtil.getFalseDto("要加入的团队不存在，可能已解散",26046);
            }
            //查询申请处理状态
            GroupSystemMsg groupSystemMsg=groupMapper.getGroupMsgById(groupApplyDisposeVo.getGroupMsgId());
            GroupValidation groupValidation=groupMapper.getGroupValidation(groupSystemMsg.getGroupValidationId());
            if (!StringUtils.isEmpty(groupApplyDisposeVo.getChoose())){
                if (!ObjectUtils.isEmpty(groupValidation) && groupValidation.getProcessState() == 0){
                    Account account=accountMapper.queryAccount(groupApplyDisposeVo.getUserId());
                    if ("1".equals(groupApplyDisposeVo.getChoose())){
                        //同意
                        //查询该成员是否已加入
                        GroupRelation groupRelation=groupMapper.queryGroupMember(groupApplyDisposeVo.getGroupId(), groupApplyDisposeVo.getMemberId());
                        if (!ObjectUtils.isEmpty(groupRelation)){
                            return DtoUtil.getFalseDto("该用户已加入此团队",26045);
                        }
                        //数据库添加群关系
                        groupMapper.createMember(groupApplyDisposeVo.getMemberId(),Long.valueOf(groupApplyDisposeVo.getGroupId()));
                        //同步融云群关系
                        List<String> list=new ArrayList<>();
                        list.add(groupApplyDisposeVo.getMemberId());
                        Result result=groupCloudUtil.joinGroup(list, groupApplyDisposeVo.getGroupId(),groupInfo.getGroupName());
                        if (result.getCode()!=200){
                            logger.error("融云同步群关系失败，错误信息："+result);
                        }
                        //修改验证处理状态
                        groupMapper.updGroupValidation(groupSystemMsg.getGroupValidationId(),"1",System.currentTimeMillis()/1000,groupApplyDisposeVo.getUserId());
                        //发送反馈信息
                        rongCloudMethodUtil.sendPrivateMsg(VALIDATION_FEEDBACK_ID,new String[]{groupApplyDisposeVo.getMemberId()},
                                0,new TxtMessage(account.getUserName()+"已同意您的申请",""));
                        return DtoUtil.getSuccessDto("操作成功",100000);
                    }else {
                        //拒绝
                        //修改验证处理状态
                        groupMapper.updGroupValidation(groupSystemMsg.getGroupValidationId(),"2",System.currentTimeMillis()/1000,groupApplyDisposeVo.getUserId());
                        //发送反馈信息
                        rongCloudMethodUtil.sendPrivateMsg(VALIDATION_FEEDBACK_ID,new String[]{groupApplyDisposeVo.getMemberId()},
                                0,new TxtMessage(account.getUserName()+"已拒绝您的申请",""));
                        return DtoUtil.getSuccessDto("操作成功",100000);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return DtoUtil.getFalseDto("操作失败",26047);
    }

    /**
     * 查询团队验证消息列表
     * @param receivedId
     * @param token
     * @return
     */
    @Override
    public Dto applyMsgList(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        List<GroupSystemMsg> groupSystemMsgs=groupMapper.queryApplyMsgList(receivedId.getUserId());
        List<GroupSystemMsg> groupSystemMsgs1=new ArrayList<>();
        List<GroupSystemMsg> groupSystemMsgs2=new ArrayList<>();
        Map<String,Object> map=new HashMap<>(2);
        for (GroupSystemMsg groupSystemMsg:groupSystemMsgs) {
            if (groupSystemMsg.getReadStatus()==0){
                groupSystemMsgs1.add(groupSystemMsg);
            }else {
                groupSystemMsgs2.add(groupSystemMsg);
            }
        }
        map.put("readMsg",groupSystemMsgs1);
        map.put("unreadMsg",groupSystemMsgs2);
        return DtoUtil.getSuccesWithDataDto("查询成功",map,100000);
    }

    @Override
    public Dto getMyGroupInfo(ReceivedGroupId receivedGroupId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedGroupId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        ShowGroupInfo showGroupInfo = groupMapper.getMyGroupInfo(receivedGroupId.getGroupId());
        List<Map<String,Object>> membersInfo = new ArrayList<>();
        if (!ObjectUtils.isEmpty(showGroupInfo)){
            List<String> memberIds = groupMapper.getMembersId(receivedGroupId.getGroupId());
            for (String memberId : memberIds){
                Map<String,Object> map = new HashMap<>();
                Account account = accountMapper.queryAccount(memberId);
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
            ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(SYSTEM_ID,new String[]{removeManager.getManagerId()},0,new TxtMessage(msgInfo,null));
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
            ResponseResult result = rongCloudMethodUtil.sendPrivateMsg(SYSTEM_ID,new String[]{addManager.getMemberId()},0,new TxtMessage(msgInfo,null));
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
        GroupInfo groupInfo = groupMapper.queryGroupInfo(removeMember.getGroupId());
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
                    ResponseResult result = rong.sendPrivateMsg(SYSTEM_ID,new String[]{memberId},0,new TxtMessage(msgInfo,null));
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
        try {
            Result result = groupCloudUtil.quitGroup(new ArrayList<>(Arrays.asList(removeMember.getMemberId())),removeMember.getGroupId(),groupInfo.getGroupName());
            if (result.getCode()!=200){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                logger.warn("更新移除团队成员时融云消息异常" + result.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            ResponseResult result = rong.sendPrivateMsg(SYSTEM_ID,new String[]{memberQuitGroup.getUserId()},0,new TxtMessage(msgInfo,null));
            if (result.getCode() != 200){
                logger.warn("移除团队成员时融云消息异常" + result.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            List<String> rongMemberId = new ArrayList<>();
            rongMemberId.add(memberQuitGroup.getUserId());
            Result result = groupCloudUtil.quitGroup(rongMemberId,memberQuitGroup.getGroupId(),groupInfo.getGroupName());
            if (result.getCode()!=200){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                logger.warn("更新成员退出团队融云消息异常" + result.toString());
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
        if (groupMapper.changeCreator(groupMakeOver.getGroupId(),groupMakeOver.getMemberId()) != 1){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getFalseDto("转让失败",80007);
        }
        try {
            String msgInfo1 = "\"" + groupInfo.getGroupName() + "\"的创建者已将团队转让给你";
            ResponseResult result1 = rong.sendPrivateMsg(SYSTEM_ID, new String[]{groupMakeOver.getMemberId()}, 0, new TxtMessage(msgInfo1, null));
            if (result1.getCode() != 200){
                logger.warn("成为团队创建者时融云消息异常" + result1.toString());
            }
            String msgInfo2 = "您已成功将团队\"" + groupInfo.getGroupName() + "\"转让给" + account.getUserName();
            ResponseResult result2 = rong.sendPrivateMsg(SYSTEM_ID, new String[]{groupMakeOver.getUserId()}, 0, new TxtMessage(msgInfo2, null));
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
