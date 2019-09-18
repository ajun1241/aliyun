package com.modcreater.tmauth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmauth.service.GroupService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.dto.EventPersons;
import com.modcreater.tmbeans.pojo.*;
import com.modcreater.tmbeans.show.ShowSingleEvent;
import com.modcreater.tmbeans.show.group.ShowGroupEventMsg;
import com.modcreater.tmbeans.show.group.ShowGroupInfo;
import com.modcreater.tmbeans.show.group.ShowMyGroup;
import com.modcreater.tmbeans.values.FinalValues;
import com.modcreater.tmbeans.vo.*;
import com.modcreater.tmbeans.vo.group.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmdao.mapper.EventMapper;
import com.modcreater.tmdao.mapper.GroupMapper;
import com.modcreater.tmbeans.vo.uservo.UserIdVo;
import com.modcreater.tmdao.mapper.*;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmutils.GroupCloudUtil;
import com.modcreater.tmutils.RongCloudMethodUtil;
import com.modcreater.tmutils.SingleEventUtil;
import com.modcreater.tmutils.messageutil.ApplyJoinGroupMsg;
import com.modcreater.tmutils.messageutil.GroupCardMsg;
import com.modcreater.tmutils.messageutil.InviteMessage;
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

    @Resource
    private EventMapper eventMapper;

    @Resource
    private BacklogMapper backlogMapper;

    @Resource
    private EventViceMapper eventViceMapper;

    @Resource
    private MsgStatusMapper msgStatusMapper;

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
        if (groupInfoVo.getMembers().length > Integer.valueOf(groupInfoVo.getGroupScale())){
            return DtoUtil.getFalseDto("当前选中人数超出上限,请重新选择",80014);
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
        if (ObjectUtils.isEmpty(groupInfo)){
            return DtoUtil.getFalseDto("该团队不存在,可能已被解散",17004);
        }
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
        if (ObjectUtils.isEmpty(groupInfo)){
            return DtoUtil.getFalseDto("该团队不存在,可能已被解散",17004);
        }
        if (!ObjectUtils.isEmpty(account)){
            try {
                //查询管理员和群主
                List<GroupRelation> groupRelationList=groupMapper.queryGroupRelation(groupApplyVo.getGroupId());
                List<String> userIds=new ArrayList<>();
                for (GroupRelation groupRelation:groupRelationList) {
                    //查询该成员是否已加入
                    if (groupRelation.getMemberId().toString().equals(groupApplyVo.getUserId())){
                        return DtoUtil.getFalseDto("该用户已加入此团队",26045);
                    }
                    if (groupRelation.getMemberLevel().equals(1L) || groupRelation.getMemberLevel().equals(2L)){
                        userIds.add(groupRelation.getMemberId().toString());
                    }
                }
                //验证内容保存
                GroupValidation groupValidation=new GroupValidation();
                groupValidation.setUserId(Long.parseLong(groupApplyVo.getUserId()));
                groupValidation.setGroupId(Long.parseLong(groupApplyVo.getGroupId()));
                groupValidation.setValidationContent(groupApplyVo.getValidationContent());
                groupValidation.setValidationSource(source.getUserName());
                groupMapper.saveValidationContent(groupValidation);
                for (String userId:userIds){
                    //消息保存
                    GroupSystemMsg groupSystemMsg=new GroupSystemMsg();
                    groupSystemMsg.setSenderId(Long.parseLong(groupApplyVo.getUserId()));
                    groupSystemMsg.setReceiverId(Long.parseLong(userId));
                    groupSystemMsg.setMsgContent("用户"+account.getUserName()+"申请加入团队"+groupInfo.getGroupName());
                    groupSystemMsg.setGroupValidationId(groupValidation.getId());
                    groupSystemMsg.setMsgType(1L);
                    groupMapper.saveGroupMsg(groupSystemMsg);
                    //发送申请消息
                    RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
                    ResponseResult result=rongCloudMethodUtil.sendPrivateMsg(VALIDATION_ID,new String[]{userId},0,
                            new TxtMessage("用户"+account.getUserName()+"申请加入团队"+groupInfo.getGroupName(),""));
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
            if(groupMapper.getMemberLevel(groupApplyDisposeVo.getGroupId(),groupApplyDisposeVo.getUserId())==0){
                return DtoUtil.getFalseDto("您没有权限",26076);
            }
            if (!StringUtils.isEmpty(groupApplyDisposeVo.getChoose())){
                if (!ObjectUtils.isEmpty(groupValidation) && groupValidation.getProcessState() == 0){
                    Account account=accountMapper.queryAccount(groupApplyDisposeVo.getUserId());
                    String content="";
                    Long flag=-1L;
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
                        content=account.getUserName()+"已同意您的申请";
                        flag=1L;
                    }else {
                        //拒绝
                        //修改验证处理状态
                        groupMapper.updGroupValidation(groupSystemMsg.getGroupValidationId(),"2",System.currentTimeMillis()/1000,groupApplyDisposeVo.getUserId());
                        content=account.getUserName()+"已拒绝您的申请";
                        flag=2L;
                    }
                    //发送反馈信息
                    ResponseResult responseResult=rongCloudMethodUtil.sendPrivateMsg(VALIDATION_FEEDBACK_ID,new String[]{groupApplyDisposeVo.getMemberId()},
                            0,new TxtMessage(content,""));
                    if (responseResult.getCode()==200){
                        //保存反馈信息
                        GroupFeedbackValidation groupFeedbackValidation=new GroupFeedbackValidation();
                        groupFeedbackValidation.setProcessId(Long.valueOf(groupApplyDisposeVo.getUserId()));
                        groupFeedbackValidation.setReceiverId(Long.valueOf(groupApplyDisposeVo.getMemberId()));
                        groupFeedbackValidation.setMsgContent(content);
                        groupFeedbackValidation.setProcessState(flag);
                        groupFeedbackValidation.setGroupId(groupInfo.getId());
                        groupMapper.saveGroupFeedbackMsg(groupFeedbackValidation);
                    }
                    return DtoUtil.getSuccessDto("操作成功",100000);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return DtoUtil.getFalseDto("操作失败,该用户可能已加入此团队",26047);
    }

    /**
     * 查询团队已处理验证消息列表
     * @param userIdVo
     * @param token
     * @return
     */
    @Override
    public Dto applyReadMsgList(UserIdVo userIdVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(userIdVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        int pageSize=Integer.parseInt(userIdVo.getPageSize());
        int pageIndex=(Integer.parseInt(userIdVo.getPageNumber())-1)*pageSize;
        List<GroupSystemMsg> groupSystemMsgs=groupMapper.queryApplyReadMsgList(userIdVo.getUserId(),pageIndex,pageSize);
        Account account=accountMapper.queryAccount(VALIDATION_ID);
        List<Map<String,Object>> mapList=new ArrayList<>();
        for (GroupSystemMsg groupSystemMsg:groupSystemMsgs) {
            Map<String,Object> map=new HashMap<>();
            map.put("msgId",groupSystemMsg.getId());
            map.put("headImgUrl","https://mdxc2019-1258779334.cos.ap-chengdu.myqcloud.com/icon/tm_verify_feedback_gray.png");
            map.put("userName",account.getUserName());
            map.put("msgContent",groupSystemMsg.getMsgContent());
            map.put("sendDate",groupSystemMsg.getSendDate().substring(11,16));
            mapList.add(map);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",mapList,100000);
    }

    /**
     * 查询团队未处理验证消息列表
     * @param userIdVo
     * @param token
     * @return
     */
    @Override
    public Dto applyUnreadMsgList(UserIdVo userIdVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(userIdVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        int pageSize=Integer.parseInt(userIdVo.getPageSize());
        int pageIndex=(Integer.parseInt(userIdVo.getPageNumber())-1)*pageSize;
        List<GroupSystemMsg> groupSystemMsgs=groupMapper.queryApplyUnreadMsgList(userIdVo.getUserId(),pageIndex,pageSize);
        List<Map<String,Object>> mapList=new ArrayList<>();
        Account account=accountMapper.queryAccount(VALIDATION_ID);
        for (GroupSystemMsg groupSystemMsg:groupSystemMsgs) {
            Map<String,Object> map=new HashMap<>();
            map.put("msgId",groupSystemMsg.getId());
            map.put("headImgUrl",account.getHeadImgUrl());
            map.put("userName",account.getUserName());
            map.put("msgContent",groupSystemMsg.getMsgContent());
            map.put("sendDate",groupSystemMsg.getSendDate().substring(11,16));
            mapList.add(map);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",mapList,100000);
    }

    /**
     * 查询团队未读验证反馈列表
     * @param userIdVo
     * @param token
     * @return
     */
    @Override
    public Dto applyUFMsgList(UserIdVo userIdVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(userIdVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        int pageSize=Integer.parseInt(userIdVo.getPageSize());
        int pageIndex=(Integer.parseInt(userIdVo.getPageNumber())-1)*pageSize;
        List<GroupFeedbackValidation> groupSystemMsgs=groupMapper.queryApplyUnreadFMsgList(userIdVo.getUserId(),pageIndex,pageSize);
        List<Map<String,Object>> mapList=new ArrayList<>();
        Account account=accountMapper.queryAccount(VALIDATION_FEEDBACK_ID);
        for (GroupFeedbackValidation groupFeedbackValidation:groupSystemMsgs) {
            Map<String,Object> map=new HashMap<>();
            map.put("msgId",groupFeedbackValidation.getId());
            map.put("headImgUrl",account.getHeadImgUrl());
            map.put("userName",account.getUserName());
            map.put("msgContent",groupFeedbackValidation.getMsgContent());
            map.put("sendDate",groupFeedbackValidation.getSendDate().substring(11,16));
            mapList.add(map);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",mapList,100000);
    }

    /**
     * 查询团队已读验证反馈列表
     * @param userIdVo
     * @param token
     * @return
     */
    @Override
    public Dto applyRFMsgList(UserIdVo userIdVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(userIdVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        int pageSize=Integer.parseInt(userIdVo.getPageSize());
        int pageIndex=(Integer.parseInt(userIdVo.getPageNumber())-1)*pageSize;
        List<GroupFeedbackValidation> groupSystemMsgs=groupMapper.queryApplyReadFMsgList(userIdVo.getUserId(),pageIndex,pageSize);
        List<Map<String,Object>> mapList=new ArrayList<>();
        Account account=accountMapper.queryAccount(VALIDATION_FEEDBACK_ID);
        for (GroupFeedbackValidation groupFeedbackValidation:groupSystemMsgs) {
            Map<String,Object> map=new HashMap<>();
            map.put("msgId",groupFeedbackValidation.getId());
            map.put("headImgUrl","https://mdxc2019-1258779334.cos.ap-chengdu.myqcloud.com/icon/tm_verify_message_gray.png");
            map.put("userName",account.getUserName());
            map.put("msgContent",groupFeedbackValidation.getMsgContent());
            map.put("sendDate",groupFeedbackValidation.getSendDate().substring(11,16));
            mapList.add(map);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",mapList,100000);
    }

    /**
     * 查询团队验证消息详情
     * @param applyMsgInfoVo
     * @param token
     * @return
     */
    @Override
    public Dto applyMsgInfo(ApplyMsgInfoVo applyMsgInfoVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(applyMsgInfoVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        //查询消息详情
        GroupSystemMsg groupSystemMsg=groupMapper.getGroupMsgById(applyMsgInfoVo.getGroupMsgId());
        //判断消息类型
        if (groupSystemMsg.getMsgType()==2){
            //事件邀请申请消息
            SingleEvent singleEvent = eventMapper.queryEventBySingleEventId(groupSystemMsg.getGroupValidationId());
            if (ObjectUtils.isEmpty(singleEvent)) {
                return DtoUtil.getFalseDto("事件已过期或者不存在", 200000);
            }
            Map<String, Object> maps = new HashMap<>();
            ArrayList<Map> list = new ArrayList<>();
            EventPersons eventPersons = JSONObject.parseObject(singleEvent.getPerson(), EventPersons.class);
            if (!StringUtils.isEmpty(eventPersons.getFriendsId())) {
                String[] friendsId = eventPersons.getFriendsId().split(",");
                for (String friendId : friendsId) {
                    Map map = new HashMap();
                    Account account = accountMapper.queryAccount(friendId);
                    map.put("friendId", account.getId().toString());
                    map.put("userCode", account.getUserCode());
                    map.put("userName", account.getUserName());
                    map.put("headImgUrl", account.getHeadImgUrl());
                    map.put("gender", account.getGender().toString());
                    list.add(map);
                }
            }
            maps.put("friendsId", list);
            maps.put("others", eventPersons.getOthers());
            singleEvent.setPerson(JSON.toJSONString(maps));
            System.out.println("显示一个事件详情时输出的" + singleEvent.getPerson());
            SingleEventAndBacklog singleEventAndBacklog=JSONObject.parseObject(JSON.toJSONString(singleEvent),SingleEventAndBacklog.class);
            //查询该事件的清单
            List<BacklogList> backlogLists=backlogMapper.queryBacklogList(singleEvent.getId());
            //查询清单权限
            if (backlogLists.size()>0){
                singleEventAndBacklog.setIsSync(backlogLists.get(0).getIsSync().toString());
            }
            singleEventAndBacklog.setBacklogList(backlogLists);
            //修改消息未读状态
            groupMapper.updGroupMsgById(applyMsgInfoVo.getGroupMsgId());
            return DtoUtil.getSuccesWithDataDto("查询成功", SingleEventUtil.getShowSingleEvent(singleEventAndBacklog), 100000);
        }
        GroupValidation groupValidation=groupMapper.getGroupValidation(groupSystemMsg.getGroupValidationId());
        Account account=accountMapper.queryAccount(groupValidation.getUserId().toString());
        Map<String,Object> map=new HashMap<>(8);
        map.put("userId",account.getId().toString());
        map.put("groupId",groupValidation.getGroupId().toString());
        map.put("userName",account.getUserName());
        map.put("city",account.getUserAddress());
        map.put("headImgUrl",account.getHeadImgUrl());
        map.put("userCode",account.getUserCode());
        map.put("processState",groupValidation.getProcessState());
        map.put("validationContent",groupValidation.getValidationContent());
        map.put("validationSource",groupValidation.getValidationSource());
        //修改消息未读状态
        groupMapper.updGroupMsgById(applyMsgInfoVo.getGroupMsgId());
        return DtoUtil.getSuccesWithDataDto("查询成功",map,100000);
    }

    /**
     * 查询团队验证反馈详情
     * @param applyMsgInfoVo
     * @param token
     * @return
     */
    @Override
    public Dto applyRFMsgInfo(ApplyMsgInfoVo applyMsgInfoVo, String token) {
        System.out.println(token);
        if (!token.equals(stringRedisTemplate.opsForValue().get(applyMsgInfoVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        //查询消息详情
        GroupFeedbackValidation groupSystemMsg=groupMapper.queryGroupFeedback(applyMsgInfoVo.getGroupMsgId());
        Map<String,Object> map=new HashMap<>(4);
        GroupInfo groupInfo=groupMapper.queryGroupInfo(groupSystemMsg.getGroupId().toString());
        map.put("groupName",ObjectUtils.isEmpty(groupInfo)?"该群已被解散":"“"+groupMapper.getMyGroupInfo(groupSystemMsg.getGroupId().toString()).getGroupName()+"”");
        map.put("status",groupSystemMsg.getProcessState().toString());
        map.put("content",groupSystemMsg.getMsgContent());
        map.put("time",groupSystemMsg.getSendDate());
        map.put("title",groupSystemMsg.getProcessState()==1?"欢迎您加入":"拒绝您加入");
        //修改消息未读状态
        groupMapper.updGroupFeedback(applyMsgInfoVo.getGroupMsgId());
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
            List<String> memberIds = groupMapper.getMembersIdExceptSelf(receivedGroupId.getGroupId(),receivedGroupId.getUserId());
            for (String memberId : memberIds){
                membersInfo.add(setAccount(memberId));
            }
            showGroupInfo.setMembersInfo(membersInfo);
            showGroupInfo.setMembersNum((long) memberIds.size());
            showGroupInfo.setRole(groupMapper.getMemberLevel(receivedGroupId.getGroupId(),receivedGroupId.getUserId()));
            return DtoUtil.getSuccesWithDataDto("查询成功",showGroupInfo,100000);
        }
        return DtoUtil.getSuccessDto("查询失败",200000);
    }

    @Override
    public Dto getMyGroupMembers(SearchMembersConditions searchMembersConditions, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(searchMembersConditions.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        List<Map<String, Object>> membersInfo = new ArrayList<>();
        List<String> memberIds;
        if (!StringUtils.hasText(searchMembersConditions.getCondition())) {
            memberIds = groupMapper.getMembersIdExceptSelf(searchMembersConditions.getGroupId(),searchMembersConditions.getUserId());
        } else {
            memberIds = groupMapper.searchMembersByCondition(searchMembersConditions.getGroupId(), searchMembersConditions.getCondition());
        }
        for (String memberId : memberIds) {
            membersInfo.add(setAccount(memberId));
        }
        return DtoUtil.getSuccesWithDataDto("查询成功", membersInfo, 100000);
    }


    /**
     * 只分组管理员和普通成员
     * @param searchMembersConditions
     * @param token
     * @return
     */
    @Override
    public Dto groupingMembers(SearchMembersConditions searchMembersConditions, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(searchMembersConditions.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        Map<String,Object> result = new HashMap<>();
        List<Map<String, Object>> managersInfo = new ArrayList<>();
        List<Map<String, Object>> membersInfo = new ArrayList<>();
        List<String> managersId;
        List<String> membersId;
        if (!StringUtils.hasText(searchMembersConditions.getCondition())) {
            membersId = groupMapper.groupingMembers(searchMembersConditions.getGroupId(),0,searchMembersConditions.getUserId());
            managersId = groupMapper.groupingMembers(searchMembersConditions.getGroupId(),1,searchMembersConditions.getUserId());
        } else {
            membersId = groupMapper.groupingMembersByCondition(searchMembersConditions.getCondition(),searchMembersConditions.getGroupId(),0,searchMembersConditions.getUserId());
            managersId = groupMapper.groupingMembersByCondition(searchMembersConditions.getCondition(),searchMembersConditions.getGroupId(),1,searchMembersConditions.getUserId());
        }
        for (String memberId : managersId) {
            managersInfo.add(setAccount(memberId));
        }
        for (String memberId : membersId) {
            membersInfo.add(setAccount(memberId));
        }
        result.put("managersInfo",managersInfo);
        result.put("membersInfo",membersInfo);
        return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
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
    public synchronized Dto removeManager(RemoveManager removeManager, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(removeManager.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (groupMapper.getMemberLevel(removeManager.getGroupId(),removeManager.getUserId()) != 2){
            return DtoUtil.getFalseDto("您没有操作权限",80004);
        }
        if (groupMapper.isMemberInGroup(removeManager.getManagerId(),removeManager.getGroupId()) != 1){
            return DtoUtil.getFalseDto("移除失败,成员\""+ accountMapper.queryAccount(removeManager.getManagerId()).getUserName() + "\"不在团队中",80011);
        }
        if (groupMapper.updateMemberLevel(removeManager.getGroupId(),removeManager.getManagerId(),0) != 1){
            return DtoUtil.getFalseDto("移除失败",80003);
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
    public synchronized Dto addManager(AddManager addManager, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(addManager.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (groupMapper.getMemberLevel(addManager.getGroupId(),addManager.getUserId()) != 2){
            return DtoUtil.getFalseDto("您没有操作权限",80004);
        }
        if (groupMapper.isMemberInGroup(addManager.getMemberId(),addManager.getGroupId()) != 1){
            return DtoUtil.getFalseDto("添加失败,成员\""+ accountMapper.queryAccount(addManager.getMemberId()).getUserName() + "\"不在团队中",80011);
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
        String[] membersId = removeMember.getMembersId();
        if (ObjectUtils.isEmpty(membersId)){
            return DtoUtil.getFalseDto("请选择要移除的成员",80010);
        }
        for (String memberId : membersId){
            if (groupMapper.isMemberInGroup(memberId,removeMember.getGroupId()) != 1){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return DtoUtil.getFalseDto("移除失败,成员\""+ accountMapper.queryAccount(memberId).getUserName() + "\"不在团队中",80011);
            }
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
            Result result = groupCloudUtil.quitGroup(new ArrayList<>(Arrays.asList(removeMember.getMembersId())),removeMember.getGroupId(),groupInfo.getGroupName());
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
    public synchronized Dto memberQuitGroup(MemberQuitGroup memberQuitGroup, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(memberQuitGroup.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (groupMapper.isMemberInGroup(memberQuitGroup.getUserId(),memberQuitGroup.getGroupId()) != 1){
            return DtoUtil.getFalseDto("退出失败,您已不在团队中",80011);
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
    public synchronized Dto groupMakeOver(GroupMakeOver groupMakeOver, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(groupMakeOver.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (groupMapper.getMemberLevel(groupMakeOver.getGroupId(),groupMakeOver.getUserId()) != 2){
            return DtoUtil.getFalseDto("违规操作!",80004);
        }
        if (groupMapper.isMemberInGroup(groupMakeOver.getMemberId(),groupMakeOver.getGroupId()) != 1){
            return DtoUtil.getFalseDto("转让失败,成员\""+ accountMapper.queryAccount(groupMakeOver.getMemberId()).getUserName() + "\"不在团队中",80011);
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

    @Override
    public synchronized Dto addNewMembers(AddNewMembers addNewMembers, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(addNewMembers.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (addNewMembers.getMembersId().length == 0){
            return DtoUtil.getFalseDto("请选择要添加的成员",80008);
        }
        String[] membersId = addNewMembers.getMembersId();
        for (String memberId : membersId){
            if (groupMapper.getGroupRelation(addNewMembers.getGroupId(),memberId) >= 1){
                Account account = accountMapper.queryAccount(memberId);
                return DtoUtil.getFalseDto("成员\""+ account.getUserName() +"\"已存在",80009);
            }
        }
        for (String memberId : membersId) {
            if (groupMapper.createMember(memberId, Long.valueOf(addNewMembers.getGroupId())) == 0){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return DtoUtil.getSuccessDto("添加成员失败",80004);
            }
        }
        GroupInfo groupInfo = groupMapper.queryGroupInfo(addNewMembers.getGroupId());
        Account account = accountMapper.queryAccount(addNewMembers.getUserId());
        try {
            Result result = groupCloudUtil.joinGroup(new ArrayList<>(Arrays.asList(addNewMembers.getMembersId())),addNewMembers.getGroupId(),groupInfo.getGroupName());
            if (result.getCode() != 200) {
                logger.warn("注册进入团队时融云消息异常" + result.toString());
            }
            String msgInfo = "您已被\"" + account.getUserName() + "\"邀请进入团队\"" + groupInfo.getGroupName() + "\"";
            ResponseResult result1 = rongCloudMethodUtil.sendPrivateMsg(SYSTEM_ID,addNewMembers.getMembersId(),0,new TxtMessage(msgInfo,null));
            if (result1.getCode() != 200) {
                logger.warn("发送加入团队成功融云消息异常" + result1.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DtoUtil.getSuccessDto("添加成员成功",100000);
    }

    @Override
    public Dto checkRole(ReceivedGroupId receivedGroupId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedGroupId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",groupMapper.getMemberLevel(receivedGroupId.getGroupId(),receivedGroupId.getUserId()),100000);
    }

    @Override
    public Dto getGroupEventMsg(GetGroupEventMsg getGroupEventMsg, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(getGroupEventMsg.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (!StringUtils.hasText(getGroupEventMsg.getEventType())){
            getGroupEventMsg.setEventType(null);
        }
        if (!StringUtils.hasText(getGroupEventMsg.getEventLevel())){
            getGroupEventMsg.setEventLevel(null);
        }
        if (!StringUtils.hasText(getGroupEventMsg.getYear())){
            getGroupEventMsg.setYear(null);
        }
        if (!StringUtils.hasText(getGroupEventMsg.getMonth())){
            getGroupEventMsg.setMonth(null);
        }
        if (!StringUtils.hasText(getGroupEventMsg.getDay())){
            getGroupEventMsg.setDay(null);
        }
        if (getGroupEventMsg.getPageNum() != null && getGroupEventMsg.getPageNum() != 0){
            getGroupEventMsg.setPageNum((getGroupEventMsg.getPageNum() - 1) * getGroupEventMsg.getPageSize());
        }else {
            getGroupEventMsg.setPageNum(0L);
        }
        if (getGroupEventMsg.getPageSize() == null || getGroupEventMsg.getPageSize() == 0){
            getGroupEventMsg.setPageSize(5L);
        }
        List<GroupEventMsg> ids = groupMapper.getGroupEventMsg(getGroupEventMsg);
        List<ShowGroupEventMsg> groupEventMsgs = new ArrayList<>();
        long bPageNum = getGroupEventMsg.getPageNum();
        long lPageNum = bPageNum + getGroupEventMsg.getPageSize() - 1;
        if (StringUtils.hasText(getGroupEventMsg.getPerson())){
            int index = 0;
            for (GroupEventMsg groupEventMsg : ids){
                //该person为传入的条件
                String[] persons;
                //该person为查询结果
                String[] personsInResult;
                try {
                    persons = getGroupEventMsg.getPerson().split(",");
                    EventPersons eventPersons2 = JSONObject.parseObject(groupEventMsg.getPerson(), EventPersons.class);
                    personsInResult = eventPersons2.getFriendsId().split(",");
                } catch (NullPointerException e) {
                    continue;
                }
                int i = 0;
                //如果条件人数多于查询结果人数,则不符合查询结果
                if (persons.length > personsInResult.length) {
                    continue;
                }
                //此处将i作为person字段条件与结果的匹配成功次数
                for (String sOut : persons) {
                    for (String sInside : personsInResult) {
                        if (sOut.equals(sInside)) {
                            i += 1;
                        }
                    }
                }
                //如果成功次数和传入条件人数相等则本次查询成功
                if (i == persons.length && index >= bPageNum && index <= lPageNum) {
                    groupEventMsgs.add(groupMapper.getGroupEventMsgBody(groupEventMsg.getId().toString()));
                }
                index += 1;
            }
        }else {
            int index = 0;
            for (GroupEventMsg groupEventMsg : ids){
                if (index >= bPageNum && index <= lPageNum) {
                    groupEventMsgs.add(groupMapper.getGroupEventMsgBody(groupEventMsg.getId().toString()));
                }
                index += 1;
            }
        }
        GroupInfo groupInfo = groupMapper.queryGroupInfo(getGroupEventMsg.getGroupId());
        for (ShowGroupEventMsg showGroupEventMsg : groupEventMsgs){
            String roleName = FinalValues.GROUPROLESNAME[groupMapper.getMemberLevel(getGroupEventMsg.getGroupId(),showGroupEventMsg.getUserId())];
            String userName = accountMapper.queryAccount(showGroupEventMsg.getUserId()).getUserName();
            showGroupEventMsg.setMsgBody(roleName + "\"" + userName + "\"" + showGroupEventMsg.getMsgBody()+ "\""  + showGroupEventMsg.getEventName()+ "\"" );
            showGroupEventMsg.setGroupPicture(groupInfo.getGroupPicture());
            showGroupEventMsg.setGroupName(groupInfo.getGroupName());
        }
        return DtoUtil.getSuccesWithDataDto("消息列表获取成功",groupEventMsgs,100000);
    }

    @Override
    public Dto getGroupEventMsgInfo(ReceivedGroupEventMsgId receivedGroupEventMsgId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedGroupEventMsgId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        GroupEventMsg groupEventMsg = groupMapper.getGroupEventMsgInfo(receivedGroupEventMsgId.getGroupEventMsgId());
        List<BacklogList> bac = new ArrayList<>();
        for (Object s : JSONObject.parseObject(groupEventMsg.getBacklogList(),ArrayList.class)){
            BacklogList backlogList = new BacklogList();
            backlogList.setBacklogName(s.toString());
            backlogList.setId(0L);
            backlogList.setSingleEventId(0L);
            backlogList.setBacklogStatus(0L);
            backlogList.setFinishTime(0L);
            backlogList.setIsTest(0L);
            backlogList.setIsSync(0L);
            backlogList.setCreateTime("");
            bac.add(backlogList);
        }
        SingleEvent singleEvent = JSONObject.parseObject(JSON.toJSONString(groupEventMsg),SingleEvent.class);
        EventPersons eventPersons = JSONObject.parseObject(singleEvent.getPerson(), EventPersons.class);
        Map<String, Object> maps = new HashMap<>();
        ArrayList<Map> list = new ArrayList<>();
        if (!StringUtils.isEmpty(eventPersons.getFriendsId())) {
            String[] friendsId = eventPersons.getFriendsId().split(",");
            for (String friendId : friendsId) {
                list.add(setAccount(friendId));
            }
        }
        maps.put("friendsId", list);
        maps.put("others", eventPersons.getOthers());
        singleEvent.setPerson(JSON.toJSONString(maps));
        ShowSingleEvent showSingleEvent = SingleEventUtil.getShowSingleEvent1(singleEvent);
        showSingleEvent.setEventid(0L);
        showSingleEvent.setFlag(1L);
        showSingleEvent.setIsOverdue(1L);
        showSingleEvent.setBacklogList(bac);
        showSingleEvent.setRemarks("");
        return DtoUtil.getSuccesWithDataDto("查询成功",showSingleEvent,100000);
    }

    /**
     * 回应团队事件邀请申请
     * @param feedbackGroupEventVo
     * @param token
     * @return
     */
    @Override
    public Dto feedbackGroupEvent(FeedbackGroupEventVo feedbackGroupEventVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(feedbackGroupEventVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        try {
            GroupSystemMsg groupSystemMsg=groupMapper.getGroupMsgById(feedbackGroupEventVo.getMsgId());
            GroupSendEventMsg groupSendEventMsg=groupMapper.getGroupSendEventMsg(groupSystemMsg.getGroupValidationId());
            //查询事件
            SingleEvent singleEvent=eventMapper.queryEventBySingleEventId(groupSystemMsg.getGroupValidationId());
            SingleEventAndBacklog singleEventAndBacklog=JSONObject.parseObject(JSON.toJSONString(singleEvent),SingleEventAndBacklog.class);
            //查询清单
            List<BacklogList> backlogLists=backlogMapper.queryBacklogList(singleEvent.getId());
            singleEventAndBacklog.setBacklogList(backlogLists);
            String content="";
            Long flag=-1L;
            //同意
            if ("1".equals(feedbackGroupEventVo.getChoose())){
                //查询团队详情
                GroupInfo groupInfo=groupMapper.queryGroupInfo(groupSendEventMsg.getGroupId().toString());
                if (ObjectUtils.isEmpty(groupInfo)){
                    return DtoUtil.getFalseDto("该团队不存在,可能已被解散",17004);
                }
                //保存事件消息
                GroupEventMsg groupEventMsg=new GroupEventMsg();
                groupEventMsg.setUserId(singleEvent.getUserid());
                groupEventMsg.setGroupId(groupSendEventMsg.getGroupId());
                groupEventMsg.setMsgBody("发起");
                groupEventMsg.setEventName(singleEvent.getEventname());
                groupEventMsg.setAddress(singleEvent.getAddress());
                groupEventMsg.setStartTime(Long.valueOf(singleEvent.getStarttime()));
                groupEventMsg.setEndTime(Long.valueOf(singleEvent.getEndtime()));
                groupEventMsg.setType(singleEvent.getType());
                groupEventMsg.setLevel(singleEvent.getLevel());
                groupEventMsg.setRepeatTime(singleEvent.getRepeaTtime());
                groupEventMsg.setRemindTime(singleEvent.getRemindTime());
                groupEventMsg.setPerson(singleEvent.getPerson());
                groupEventMsg.setRemark(singleEvent.getRemarks());
                groupEventMsg.setYear(singleEvent.getYear());
                groupEventMsg.setMonth(singleEvent.getMonth());
                groupEventMsg.setDay(singleEvent.getDay());
                List<String> backlogs=new ArrayList<>();
                for (BacklogList backlogList:backlogLists) {
                    backlogs.add(backlogList.getBacklogName());
                }
                groupEventMsg.setBacklogList(JSON.toJSONString(backlogs));
                groupMapper.saveGroupEventMsg(groupEventMsg);
                //消息保存
                MsgStatus msgStatus = new MsgStatus();
                msgStatus.setType(1L);
                msgStatus.setUserId(singleEvent.getUserid());
                msgStatus.setReceiverId(groupEventMsg.getGroupId());
                msgStatusMapper.addNewMsg(msgStatus);
                //保存群聊事件消息
                List<GroupRelation> groupRelationList=groupMapper.queryGroupRelation(groupSendEventMsg.getGroupId().toString());
                List<Map<String,String>> mapList=new ArrayList<>();
                for (GroupRelation relation:groupRelationList) {
                    Map<String,String> map=new HashMap(2);
                    map.put("msgStatusId",msgStatus.getId().toString());
                    map.put("userId",relation.getMemberId().toString());
                    if (relation.getMemberId().equals(singleEvent.getUserid())){
                        map.put("status","1");
                    }else {
                        map.put("status","2");
                    }
                    mapList.add(map);
                }
                groupMapper.saveGroupInviteMsg(mapList);
                //发送邀请消息至群聊
                String date = singleEvent.getYear() + "/" + singleEvent.getMonth() + "/" + singleEvent.getDay();
                InviteMessage inviteMessage = new InviteMessage(singleEvent.getEventname(), date, JSON.toJSONString(SingleEventUtil.getShowSingleEvent(singleEventAndBacklog)), "2",msgStatus.getId().toString());
                logger.info(JSON.toJSONString(SingleEventUtil.getShowSingleEvent(singleEventAndBacklog)));
                ResponseResult result = rongCloudMethodUtil.sendGroupMsg(singleEvent.getUserid().toString(), new String[]{groupSendEventMsg.getGroupId().toString()}, inviteMessage,1);
                if (result.getCode() != 200) {
                    logger.info("群聊发送邀请事件时融云消息异常" + result.toString());
                    return DtoUtil.getFalseDto("消息发送失败", 21040);
                }
                //修改申请处理状态
                groupMapper.updGroupSendEventMsg(1L,System.currentTimeMillis()/1000,feedbackGroupEventVo.getUserId(),groupSendEventMsg.getId());
                content="管理员"+accountMapper.queryAccount(feedbackGroupEventVo.getUserId()).getUserName()+"同意了您发送+“"+singleEvent.getEventname()+"”事件的申请";
                flag=1L;
            }else {//拒绝
                //修改申请处理状态
                groupMapper.updGroupSendEventMsg(2L,System.currentTimeMillis()/1000,feedbackGroupEventVo.getUserId(),groupSendEventMsg.getId());
                content="管理员拒绝了您发送+“"+singleEvent.getEventname()+"”事件的申请";
                flag=2L;
            }
            //发送反馈信息
            ResponseResult responseResult=rongCloudMethodUtil.sendPrivateMsg(VALIDATION_FEEDBACK_ID,new String[]{groupSendEventMsg.getSenderId().toString()},
                    0,new TxtMessage(content,""));
            if (responseResult.getCode()==200){
                //保存反馈信息
                GroupFeedbackValidation groupFeedbackValidation=new GroupFeedbackValidation();
                groupFeedbackValidation.setProcessId(Long.valueOf(feedbackGroupEventVo.getUserId()));
                groupFeedbackValidation.setReceiverId(groupSendEventMsg.getSenderId());
                groupFeedbackValidation.setMsgContent(content);
                groupFeedbackValidation.setProcessState(flag);
                groupFeedbackValidation.setGroupId(groupSendEventMsg.getGroupId());
                groupMapper.saveGroupFeedbackMsg(groupFeedbackValidation);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return DtoUtil.getFalseDto("操作失败",20155);
        }
        return DtoUtil.getSuccessDto("操作成功",100000);
    }

    @Override
    public synchronized Dto breakGroup(ReceivedGroupId receivedGroupId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedGroupId.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        if (groupMapper.getMemberLevel(receivedGroupId.getGroupId(),receivedGroupId.getUserId()) != 2){
            return DtoUtil.getFalseDto("您没有操作权限",80004);
        }
        List<String> membersId = groupMapper.getMembersIdExceptSelf(receivedGroupId.getGroupId(),receivedGroupId.getUserId());
        GroupInfo groupInfo = groupMapper.queryGroupInfo(receivedGroupId.getGroupId());
        int b1 = groupMapper.deleteGroup(receivedGroupId.getGroupId());
        int b2 = groupMapper.deleteAllMembers(receivedGroupId.getGroupId());
        if (b1 == 1 && b2 >= 3){
            List<String> allMembersId = groupMapper.getMembersIdExceptSelf(receivedGroupId.getGroupId(),receivedGroupId.getUserId());
            allMembersId.add(receivedGroupId.getUserId());
            try {
                Result result = groupCloudUtil.dissolutionGroup(allMembersId,receivedGroupId.getGroupId());
                if (result.getCode() != 200){
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    logger.warn("解散团队出错");
                    return DtoUtil.getFalseDto("解散团队失败,请检查网络环境",80012);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String msgInfo = "团队\"" + groupInfo.getGroupName() + "\"已被创建者解散";
            try {
                ResponseResult result1 = rongCloudMethodUtil.sendPrivateMsg(SYSTEM_ID, (String[]) membersId.toArray(),0,new TxtMessage(msgInfo,null));
                if (result1.getCode() != 200){
                    logger.warn("解散团队消息发送失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return DtoUtil.getSuccessDto("解散成功",100000);
        }
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return DtoUtil.getFalseDto("解散失败,请勿重复操作",80013);
    }


    /**
     * 发送邀请事件至团队
     * @param sendInviteEventVo
     * @param token
     * @return
     */
    @Override
    public Dto sendInviteEvent(SendInviteEventVo sendInviteEventVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(sendInviteEventVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        //查询团队详情
        GroupInfo groupInfo=groupMapper.queryGroupInfo(sendInviteEventVo.getGroupId());
        if (ObjectUtils.isEmpty(groupInfo)){
            return DtoUtil.getFalseDto("该团队不存在,可能已被解散",17004);
        }
        try {
            int userLevel=groupMapper.getMemberLevel(sendInviteEventVo.getGroupId(),sendInviteEventVo.getUserId());
            SingleEventAndBacklog singleEvent=JSONObject.parseObject(sendInviteEventVo.getSingleEvent(),SingleEventAndBacklog.class);
            //保存事件至发送者时间轴
            singleEvent.setUserid(Long.parseLong(sendInviteEventVo.getUserId()));
            //这里开始判断是否是一个重复事件,如果状态值为真,则该事件为重复事件
            singleEvent.setIsLoop(SingleEventUtil.isLoopEvent(singleEvent.getRepeaTtime()) ? 1 : 0);
            if (singleEvent.getIsLoop() == 1) {
                List<SingleEvent> loopEventList = eventMapper.queryClashLoopEventList(singleEvent);
                if (!SingleEventUtil.loopEventTime(loopEventList, singleEvent)) {
                    return DtoUtil.getFalseDto("时间段冲突,无法添加", 21012);
                }
            } else if (!SingleEventUtil.eventTime(eventMapper.queryClashEventList(singleEvent), Long.valueOf(singleEvent.getStarttime()), Long.valueOf(singleEvent.getEndtime()))) {
                return DtoUtil.getFalseDto("时间段冲突,无法添加", 21012);
            }
            //事件保存在自己的时间轴里
            eventMapper.uploadingEvents(singleEvent);
            //添加事件清单
            if (!ObjectUtils.isEmpty(singleEvent.getBacklogList()) && singleEvent.getBacklogList().size()>0){
                List<BacklogList> backlogLists=new ArrayList<>();
                for (BacklogList backlogList:singleEvent.getBacklogList()) {
                    backlogList.setSingleEventId(singleEvent.getId());
                    //判断同步权限
                    if (!StringUtils.isEmpty(singleEvent.getIsSync()) && "1".equals(singleEvent.getIsSync())){
                        backlogList.setIsSync(1L);
                    }
                    backlogLists.add(backlogList);
                }
                backlogMapper.insertBacklog(backlogLists);
            }
            //在事件副表插入创建者
            SingleEventVice singleEventVice = new SingleEventVice();
            singleEventVice.setCreateBy(Long.parseLong(sendInviteEventVo.getUserId()));
            singleEventVice.setUserId(singleEvent.getUserid());
            singleEventVice.setEventId(singleEvent.getEventid());
            eventViceMapper.createEventVice(singleEventVice);
            //判断发送者权限
            if (userLevel==2 || userLevel==1){
                //管理员或群主
                //保存事件消息
                GroupEventMsg groupEventMsg=new GroupEventMsg();
                groupEventMsg.setUserId(Long.parseLong(sendInviteEventVo.getUserId()));
                groupEventMsg.setGroupId(Long.parseLong(sendInviteEventVo.getGroupId()));
                groupEventMsg.setMsgBody("发起");
                groupEventMsg.setEventName(singleEvent.getEventname());
                groupEventMsg.setAddress(singleEvent.getAddress());
                groupEventMsg.setStartTime(Long.valueOf(singleEvent.getStarttime()));
                groupEventMsg.setEndTime(Long.valueOf(singleEvent.getEndtime()));
                groupEventMsg.setType(singleEvent.getType());
                groupEventMsg.setLevel(singleEvent.getLevel());
                groupEventMsg.setRepeatTime(singleEvent.getRepeaTtime());
                groupEventMsg.setRemindTime(singleEvent.getRemindTime());
                groupEventMsg.setPerson(singleEvent.getPerson());
                groupEventMsg.setRemark(singleEvent.getRemarks());
                groupEventMsg.setYear(singleEvent.getYear());
                groupEventMsg.setMonth(singleEvent.getMonth());
                groupEventMsg.setDay(singleEvent.getDay());
                List<String> backlogs=new ArrayList<>();
                for (BacklogList backlogList:singleEvent.getBacklogList()) {
                    backlogs.add(backlogList.getBacklogName());
                }
                groupEventMsg.setBacklogList(JSON.toJSONString(backlogs));
                groupMapper.saveGroupEventMsg(groupEventMsg);
                //消息保存
                MsgStatus msgStatus = new MsgStatus();
                msgStatus.setType(1L);
                msgStatus.setUserId(Long.parseLong(sendInviteEventVo.getUserId()));
                msgStatus.setReceiverId(Long.parseLong(sendInviteEventVo.getGroupId()));
                msgStatusMapper.addNewMsg(msgStatus);
                //保存群聊事件消息
                List<GroupRelation> groupRelationList=groupMapper.queryGroupRelation(sendInviteEventVo.getGroupId());
                List<Map<String,String>> mapList=new ArrayList<>();
                for (GroupRelation relation:groupRelationList) {
                    Map<String,String> map=new HashMap(2);
                    map.put("msgStatusId",msgStatus.getId().toString());
                    map.put("userId",relation.getMemberId().toString());
                    if (relation.getMemberId().equals(singleEvent.getUserid())){
                        map.put("status","1");
                    }else {
                        map.put("status","2");
                    }
                    mapList.add(map);
                }
                groupMapper.saveGroupInviteMsg(mapList);
                //发送邀请消息至群聊
                String date = singleEvent.getYear() + "/" + singleEvent.getMonth() + "/" + singleEvent.getDay();
                InviteMessage inviteMessage = new InviteMessage(singleEvent.getEventname(), date, JSON.toJSONString(SingleEventUtil.getShowSingleEvent(singleEvent)), "2",msgStatus.getId().toString());
                logger.info(JSON.toJSONString(SingleEventUtil.getShowSingleEvent(singleEvent)));
                ResponseResult result = rongCloudMethodUtil.sendGroupMsg(sendInviteEventVo.getUserId(), new String[]{sendInviteEventVo.getGroupId()}, inviteMessage,1);
                if (result.getCode() != 200) {
                    logger.info("群聊发送邀请事件时融云消息异常" + result.toString());
                    return DtoUtil.getFalseDto("消息发送失败", 21040);
                }
                return DtoUtil.getSuccessDto("发送成功",100000);
            }else {
                //普通成员
                Account account=accountMapper.queryAccount(sendInviteEventVo.getUserId());
                //查询管理员和群主
                List<GroupRelation> groupRelationList=groupMapper.queryGroupRelation(sendInviteEventVo.getGroupId());
                List<String> userIds=new ArrayList<>();
                for (GroupRelation groupRelation:groupRelationList) {
                    if (groupRelation.getMemberLevel().equals(1L) || groupRelation.getMemberLevel().equals(2L)){
                        userIds.add(groupRelation.getMemberId().toString());
                    }
                }
                //验证内容保存
                GroupSendEventMsg groupSendEventMsg=new GroupSendEventMsg();
                groupSendEventMsg.setEventId(singleEvent.getId());
                groupSendEventMsg.setGroupId(Long.valueOf(sendInviteEventVo.getGroupId()));
                groupSendEventMsg.setSenderId(Long.valueOf(sendInviteEventVo.getUserId()));
                groupMapper.saveSendEventMsg(groupSendEventMsg);
                //发送申请消息给管理员
                for (String userId:userIds) {
                    String content="用户"+account.getUserName()+"申请发送事件“"+singleEvent.getEventname()+"”至团队";
                    //消息保存
                    GroupSystemMsg groupSystemMsg=new GroupSystemMsg();
                    groupSystemMsg.setSenderId(Long.parseLong(sendInviteEventVo.getUserId()));
                    groupSystemMsg.setReceiverId(Long.parseLong(userId));
                    groupSystemMsg.setMsgContent(content);
                    groupSystemMsg.setGroupValidationId(groupSendEventMsg.getId());
                    groupSystemMsg.setMsgType(2L);
                    groupMapper.saveGroupMsg(groupSystemMsg);
                    //发送申请消息
                    RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
                    ResponseResult result=rongCloudMethodUtil.sendPrivateMsg(VALIDATION_ID,new String[]{userId},0,
                            new TxtMessage(content,""));
                    if (result.getCode()!=200){
                        return DtoUtil.getFalseDto("发送失败",17002);
                    }
                }
                return DtoUtil.getSuccessDto("请求已发送,请等待管理员审核",100000);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return DtoUtil.getFalseDto("发送失败了",17002);
    }

    /**
     * 判断用户是管理员还是团长
     * @param groupId
     * @param userId
     * @return
     */
    private boolean isHavePermission(String groupId, String userId){
        int level = groupMapper.getMemberLevel(groupId,userId);
        return level == 2 || level == 1;
    }

    private Map<String,Object> setAccount(String memberId){
        Map<String, Object> map = new HashMap<>();
        Account account = accountMapper.queryAccount(memberId);
        map.put("userSign", account.getUserSign());
        map.put("headImgUrl", account.getHeadImgUrl());
        map.put("userName", account.getUserName());
        map.put("friendId", account.getId());
        map.put("gender", account.getGender());
        map.put("userCode", account.getUserCode());
        return map;
    }
}
