package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.GroupService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Account;
import com.modcreater.tmbeans.pojo.GroupInfo;
import com.modcreater.tmbeans.pojo.GroupPermission;
import com.modcreater.tmbeans.pojo.GroupRelation;
import com.modcreater.tmbeans.show.group.ShowGroupInfo;
import com.modcreater.tmbeans.show.group.ShowMyGroup;
import com.modcreater.tmbeans.values.FinalValues;
import com.modcreater.tmbeans.vo.GroupInfoVo;
import com.modcreater.tmbeans.vo.GroupMsgVo;
import com.modcreater.tmbeans.vo.GroupRelationVo;
import com.modcreater.tmbeans.vo.group.ReceivedGroupId;
import com.modcreater.tmbeans.vo.group.UpdateGroupInfo;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmdao.mapper.GroupMapper;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmutils.RongCloudMethodUtil;
import com.modcreater.tmutils.messageutil.ApplyJoinGroupMsg;
import com.modcreater.tmutils.messageutil.GroupCardMsg;
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


    @Override
    public Dto createGroup(GroupInfoVo groupInfoVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(groupInfoVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        try {
            if (!StringUtils.hasText(groupInfoVo.getGroupPicture())){
                groupInfoVo.setGroupPicture(groupMapper.getGroupDefultHeadImgUrl(groupInfoVo.getGroupNature()));
            }
            groupMapper.createGroup(groupInfoVo);
            int i = groupMapper.addCreator(groupInfoVo.getUserId(),groupInfoVo.getId());
            if (i == 1){
                for (String memberId : groupInfoVo.getMembers()){
                    groupMapper.createMember(memberId,groupInfoVo.getId());
                }
                return DtoUtil.getSuccessDto("创建成功",100000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getSuccessDto("创建失败",80001);
        }
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return DtoUtil.getSuccessDto("创建失败",80001);
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
        //0 : 不需要; 1 : 需要
        int status = 1;
        GroupPermission groupPermission = groupMapper.getGroupUpperLimit(receivedId.getUserId());
        if (groupMapper.getMyCreatedGroupNum(receivedId.getUserId()) < groupPermission.getGroupUpperLimit()){
            status = 0;
        }
        return DtoUtil.getSuccesWithDataDto("操作成功",status,100000);
    }

    /**
     * 发送团队名片
     * @param groupMsgVo
     * @param token
     * @return
     */
    @Override
    public Dto sendGroupCard(GroupMsgVo groupMsgVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(groupMsgVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        //查询团队信息
        GroupInfo groupInfo=groupMapper.queryGroupInfo(groupMsgVo.getGroupId());
        try {
            RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
            GroupCardMsg groupCardMsg =new GroupCardMsg(groupInfo.getId().toString(),groupInfo.getGroupName(),groupInfo.getGroupPicture(),groupInfo.getGroupUnit());
            ResponseResult result=rongCloudMethodUtil.sendPrivateMsg(groupMsgVo.getUserId(),groupMsgVo.getTargetId(),1, groupCardMsg);
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
     * @param groupMsgVo
     * @param token
     * @return
     */
    @Override
    public Dto applyJoinGroup(GroupMsgVo groupMsgVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(groupMsgVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        //查询用户详情
        Account account=accountMapper.queryAccount(groupMsgVo.getUserId());
        if (!ObjectUtils.isEmpty(account)){
            try {
                RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
                ApplyJoinGroupMsg applyJoinGroupMsg =new ApplyJoinGroupMsg(account.getId().toString(),account.getUserName(),account.getHeadImgUrl(),account.getUserCode(),groupMsgVo.getValidationContent());
                //查询管理员和群主
                List<GroupRelation> groupRelationList=groupMapper.queryGroupRelation(groupMsgVo.getGroupId());
                List<String> userIds=new ArrayList<>();
                for (GroupRelation groupRelation:groupRelationList) {
                    if (groupRelation.getMemberLevel().equals(1L) || groupRelation.getMemberLevel().equals(2L)){
                        userIds.add(groupRelation.getMemberId().toString());
                    }
                }
                ResponseResult result=rongCloudMethodUtil.sendPrivateMsg(groupMsgVo.getUserId(),userIds.toArray(new String[userIds.size()]),1, applyJoinGroupMsg);
                if (result.getCode()!=200){
                    return DtoUtil.getFalseDto("发送失败",17002);
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
     * @param groupMsgVo
     * @param token
     * @return
     */
    @Override
    public Dto respondApply(GroupMsgVo groupMsgVo, String token) {
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
}
