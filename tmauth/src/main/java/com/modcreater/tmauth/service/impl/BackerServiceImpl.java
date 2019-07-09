package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.BackerService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Account;
import com.modcreater.tmbeans.pojo.Backers;
import com.modcreater.tmbeans.pojo.Friendship;
import com.modcreater.tmbeans.pojo.MsgStatus;
import com.modcreater.tmbeans.show.backer.ShowFriendList;
import com.modcreater.tmbeans.vo.backer.ReceivedBeSupporterFeedback;
import com.modcreater.tmbeans.vo.backer.ReceivedChangeBackerInfo;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmdao.mapper.BackerMapper;
import com.modcreater.tmdao.mapper.MsgStatusMapper;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmutils.RongCloudMethodUtil;
import com.modcreater.tmutils.messageutil.AddBackerMessage;
import io.rong.messages.TxtMessage;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-07-01
 * Time: 15:23
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BackerServiceImpl implements BackerService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private BackerMapper backerMapper;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private MsgStatusMapper msgStatusMapper;

    private Logger logger = LoggerFactory.getLogger(BackerServiceImpl.class);

    @Override
    public Dto getFriendList(ReceivedId receivedId, String token) {
        if (StringUtils.isEmpty(receivedId.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        String redisToken = stringRedisTemplate.opsForValue().get(receivedId.getUserId());
        if (!token.equals(redisToken)) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        List<ShowFriendList> friendList = backerMapper.getFriendList(receivedId.getUserId());
        if (friendList.size() == 0) {
            return DtoUtil.getSuccesWithDataDto("查询成功", null, 200000);
        }
        Backers backer = backerMapper.getRealMyBacker(receivedId.getUserId());
        if (!ObjectUtils.isEmpty(backer)) {
            for (ShowFriendList showFriendList : friendList) {
                if (backer.getBackerId().equals(showFriendList.getFriendId())) {
                    showFriendList.setStatus(0);
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("friendList", friendList);
        return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
    }

    @Override
    public synchronized Dto changeBacker(ReceivedChangeBackerInfo receivedChangeBackerInfo, String token) {
        if (StringUtils.isEmpty(receivedChangeBackerInfo.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        String redisToken = stringRedisTemplate.opsForValue().get(receivedChangeBackerInfo.getUserId());
        if (!token.equals(redisToken)) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        try {
            Backers backer = backerMapper.getMyBacker(receivedChangeBackerInfo.getUserId());
            if (ObjectUtils.isEmpty(backer) && !StringUtils.hasText(receivedChangeBackerInfo.getFriendId())){
                return DtoUtil.getFalseDto("操作错误",22013);
            }
            if (!ObjectUtils.isEmpty(backer) && backer.getStatus().equals("0")) {
                if (System.currentTimeMillis()/1000 - backer.getCreateDate() < 1800){
                    return DtoUtil.getFalseDto("请等待您上一个邀请的好友回应", 22002);
                }else {
                    backerMapper.deleteBacker(receivedChangeBackerInfo.getUserId());
                }
            }
            if (StringUtils.hasText(receivedChangeBackerInfo.getFriendId())) {
                List<Long> friends = accountMapper.queryAllFriendList(receivedChangeBackerInfo.getUserId());
                boolean status = false;
                if (friends.size() != 0) {
                    for (Long friendId : friends) {
                        if (friendId.toString().equals(receivedChangeBackerInfo.getFriendId())) {
                            status = true;
                            break;
                        }
                    }
                }
                if (!status) {
                    return DtoUtil.getFalseDto("只能将好友设置为您的支持者", 22003);
                }
            }
            Account account = accountMapper.queryAccount(receivedChangeBackerInfo.getUserId());
            MsgStatus msgStatus = new MsgStatus();
            msgStatus.setUserId(Long.valueOf(receivedChangeBackerInfo.getUserId()));
            msgStatus.setType(2L);
            msgStatusMapper.addNewMsg(msgStatus);
            AddBackerMessage addBackerMessage = new AddBackerMessage();
            addBackerMessage.setContent("来来来,当我的支持者,搞起!");
            addBackerMessage.setMsgId(msgStatus.getId().toString());
            RongCloudMethodUtil rong = new RongCloudMethodUtil();
            String[] friendId = {ObjectUtils.isEmpty(backer) ? receivedChangeBackerInfo.getFriendId() : backer.getBackerId()};
            if (ObjectUtils.isEmpty(backer)) {
                if (StringUtils.hasText(receivedChangeBackerInfo.getFriendId())) {
                    Friendship friendship = accountMapper.queryFriendshipDetail(receivedChangeBackerInfo.getUserId(),receivedChangeBackerInfo.getFriendId());
                    if (friendship.getSustain().equals("0")){
                        ResponseResult result = rong.sendPrivateMsg("100000", friendId, 0, new TxtMessage(account.getUserName()+"拒绝了您的支持者邀请",""));
                        if (result.getCode() != 200) {
                            logger.info("添加支持者时融云消息异常" + result.toString());
                        }
                        msgStatusMapper.addNewEventMsg(friendId[0],1L,receivedChangeBackerInfo.getUserId(),":您的支持者邀请被拒绝了",System.currentTimeMillis()/1000);
                    }
                    ResponseResult result = rong.sendPrivateMsg(receivedChangeBackerInfo.getUserId(), friendId, 0, addBackerMessage);
                    if (result.getCode() != 200) {
                        logger.info("添加支持者时融云消息异常" + result.toString());
                    }
                    for (String s : friendId){
                        msgStatusMapper.addNewEventMsg(s,1L,receivedChangeBackerInfo.getUserId(),":来来来,当我的支持者,搞起!",System.currentTimeMillis()/1000);
                    }
                    if (backerMapper.addBackers(receivedChangeBackerInfo.getUserId(), receivedChangeBackerInfo.getFriendId(), System.currentTimeMillis() / 1000 ,msgStatus.getId()) > 0) {
                        return DtoUtil.getSuccessDto("修改成功", 100000);
                    }
                }

            } else {
                if (!StringUtils.hasText(receivedChangeBackerInfo.getFriendId())) {
                    ResponseResult result = rong.sendPrivateMsg("100000", friendId, 0, new TxtMessage(account.getUserName() + "取消了您作为ta支持者的身份", ""));
                    if (result.getCode() != 200) {
                        logger.info("发送删除支持者时融云消息异常" + result.toString());
                    }
                    for (String s : friendId){
                        msgStatusMapper.addNewEventMsg(s,1L,receivedChangeBackerInfo.getUserId(),"取消了您作为ta支持者的身份",System.currentTimeMillis()/1000);
                    }
                    if (backerMapper.deleteBacker(receivedChangeBackerInfo.getUserId()) > 0) {
                        return DtoUtil.getSuccessDto("修改成功", 100000);
                    }
                } else {
                    if (!receivedChangeBackerInfo.getFriendId().equals(backer.getBackerId())) {
                        String[] deletedId = {backer.getBackerId()};
                        ResponseResult result1 = rong.sendPrivateMsg("100000", deletedId, 0, new TxtMessage(account.getUserName() + "取消了您作为ta支持者的身份", ""));
                        if (result1.getCode() != 200) {
                            logger.info("发送删除支持者时融云消息异常" + result1.toString());
                        }
                        for (String s : deletedId){
                            msgStatusMapper.addNewEventMsg(s,1L,receivedChangeBackerInfo.getUserId(),"取消了您作为ta支持者的身份",System.currentTimeMillis()/1000);
                        }
                        Friendship friendship = accountMapper.queryFriendshipDetail(receivedChangeBackerInfo.getUserId(),receivedChangeBackerInfo.getFriendId());
                        if (friendship.getSustain().equals("0")){
                            ResponseResult result = rong.sendPrivateMsg("100000", friendId, 0, new TxtMessage(account.getUserName()+"拒绝了您的支持者邀请",""));
                            if (result.getCode() != 200) {
                                logger.info("添加支持者时融云消息异常" + result.toString());
                            }
                            msgStatusMapper.addNewEventMsg(friendId[0],1L,receivedChangeBackerInfo.getUserId(),":您的支持者邀请被拒绝了",System.currentTimeMillis()/1000);
                        }
                        String[] changedId = {receivedChangeBackerInfo.getFriendId()};
                        ResponseResult result2 = rong.sendPrivateMsg(receivedChangeBackerInfo.getUserId(), changedId, 0, addBackerMessage);
                        if (result2.getCode() != 200) {
                            logger.info("添加邀请事件时融云消息异常" + result2.toString());
                        }
                        for (String s : changedId){
                            msgStatusMapper.addNewEventMsg(s,1L,receivedChangeBackerInfo.getUserId(),":来来来,当我的支持者,搞起!",System.currentTimeMillis()/1000);
                        }
                        if (backerMapper.updateBacker(receivedChangeBackerInfo.getUserId(), receivedChangeBackerInfo.getFriendId(), System.currentTimeMillis() / 1000 ,msgStatus.getId()) > 0) {
                            return DtoUtil.getSuccessDto("修改成功", 100000);
                        }
                    } else {
                        return DtoUtil.getFalseDto("您已经将ta设置为您的支持者了", 22002);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getFalseDto("修改失败", 22002);
        }
        return DtoUtil.getFalseDto("修改失败", 22002);
    }

    @Override
    public Dto getMyBacker(ReceivedId receivedId, String token) {
        if (StringUtils.isEmpty(receivedId.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        String redisToken = stringRedisTemplate.opsForValue().get(receivedId.getUserId());
        if (!token.equals(redisToken)) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        Map<String, String> result = new HashMap<>();
        Backers backer = backerMapper.getRealMyBacker(receivedId.getUserId());
        if (!ObjectUtils.isEmpty(backer)) {
            Account account = accountMapper.queryAccount(backer.getBackerId());
            if (!ObjectUtils.isEmpty(account)) {
                result.put("userId", account.getId().toString());
                result.put("userName", account.getUserName());
                result.put("headImgUrl", account.getHeadImgUrl());
                result.put("gender", account.getGender().toString());
                if (!StringUtils.hasText(account.getUserSign())) {
                    account.setUserSign("");
                }
                result.put("userSign", account.getUserSign());
            }
        }
        if (result.size() == 0) {
            return DtoUtil.getSuccesWithDataDto("查询成功", null, 200000);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
    }

    @Override
    public Dto beSupporterFeedback(ReceivedBeSupporterFeedback receivedBeSupporterFeedback, String token) {
        if (StringUtils.isEmpty(receivedBeSupporterFeedback.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)) {
            return DtoUtil.getFalseDto("token未获取到", 21013);
        }
        String redisToken = stringRedisTemplate.opsForValue().get(receivedBeSupporterFeedback.getUserId());
        if (!token.equals(redisToken)) {
            return DtoUtil.getFalseDto("请重新登录", 21014);
        }
        Backers backer = backerMapper.getMyBacker(receivedBeSupporterFeedback.getReceiverId());
        MsgStatus msgStatus = msgStatusMapper.queryMsg(receivedBeSupporterFeedback.getMsgId());
        if (ObjectUtils.isEmpty(backer)){
            return DtoUtil.getFalseDto("操作失败 ",22013);
        }
        if (msgStatus.getStatus().equals("3") || System.currentTimeMillis()/1000 - backer.getCreateDate() >= 1800){
            return DtoUtil.getFalseDto("消息已过期", 22012);
        }
        try {
            if (!ObjectUtils.isEmpty(backer) && backer.getStatus().equals("0") && System.currentTimeMillis()/1000 - backer.getCreateDate() < 1800) {
                int i = backerMapper.updateMsgStatus(receivedBeSupporterFeedback.getMsgId(), receivedBeSupporterFeedback.getReceiverId(), receivedBeSupporterFeedback.getStatus());
                if (i <= 0) {
                    return DtoUtil.getFalseDto("消息已过期", 22012);
                }
                if (backerMapper.updateBackerStatus(receivedBeSupporterFeedback.getReceiverId(), receivedBeSupporterFeedback.getStatus().equals("0") ? 1 : 2) <= 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return DtoUtil.getFalseDto("操作失败", 22002);
                }
                RongCloudMethodUtil rong = new RongCloudMethodUtil();
                Account account = accountMapper.queryAccount(receivedBeSupporterFeedback.getUserId());
                String[] receiver = {receivedBeSupporterFeedback.getReceiverId()};
                String msg = receivedBeSupporterFeedback.getStatus().equals("0") ? "同意了您的支持者邀请" : "拒绝了您的支持者邀请";
                ResponseResult result1 = rong.sendPrivateMsg("100000",receiver, 0, new TxtMessage(account.getUserName() + msg, ""));
                if (result1.getCode() != 200){
                    logger.info("添加邀请事件时融云消息异常" + result1.toString());
                }
                msgStatusMapper.addNewEventMsg(receivedBeSupporterFeedback.getReceiverId(),1L,account.getId().toString(),msg,System.currentTimeMillis()/1000);
                return DtoUtil.getSuccessDto("", 100000);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getFalseDto("操作失败", 22002);
        }
        return DtoUtil.getFalseDto("消息已过期", 22012);
    }
}
