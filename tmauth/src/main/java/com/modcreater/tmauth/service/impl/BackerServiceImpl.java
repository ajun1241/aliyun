package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.BackerService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Account;
import com.modcreater.tmbeans.pojo.Backers;
import com.modcreater.tmbeans.show.backer.ShowFriendList;
import com.modcreater.tmbeans.vo.backer.ReceivedChangeBackerInfo;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmdao.mapper.BackerMapper;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        Backers backer = backerMapper.getMyBacker(receivedId.getUserId());
        if (!ObjectUtils.isEmpty(backer)) {
            for (ShowFriendList showFriendList : friendList) {
                if (backer.getBackerId().equals(showFriendList.getFriendId())) {
                    showFriendList.setStatus("0");
                }
            }
        }
        return DtoUtil.getSuccesWithDataDto("查询成功", friendList, 100000);
    }

    @Override
    public Dto changeBacker(ReceivedChangeBackerInfo receivedChangeBackerInfo, String token) {
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
        List<Long> friends = accountMapper.queryAllFriendList(receivedChangeBackerInfo.getUserId());
        boolean status = false;
        if (friends.size() != 0){
            for (Long friendId : friends){
                if (friendId.toString().equals(receivedChangeBackerInfo.getFriendId())){
                    status = true;
                    break;
                }
            }
        }
        if (!status){
            return DtoUtil.getFalseDto("只能将好友设置为您的支持者",22003);
        }
        Backers backer = backerMapper.getMyBacker(receivedChangeBackerInfo.getUserId());
        if (ObjectUtils.isEmpty(backer)) {
            if (receivedChangeBackerInfo.getStatus().equals("0")) {
                backerMapper.addBackers(receivedChangeBackerInfo.getUserId(), receivedChangeBackerInfo.getFriendId());
                return DtoUtil.getSuccessDto("修改成功", 100000);
            }
        } else {
            if (receivedChangeBackerInfo.getFriendId().equals(backer.getBackerId())) {
                if (receivedChangeBackerInfo.getStatus().equals("1")) {
                    if (backerMapper.deleteBacker(receivedChangeBackerInfo.getUserId(), receivedChangeBackerInfo.getFriendId()) > 0) {
                        return DtoUtil.getSuccessDto("修改成功", 100000);
                    }
                }
            } else {
                if (receivedChangeBackerInfo.getStatus().equals("0")) {
                    if (backerMapper.updateBacke(receivedChangeBackerInfo.getUserId(), receivedChangeBackerInfo.getFriendId()) > 0) {
                        return DtoUtil.getSuccessDto("修改成功", 100000);
                    }
                }
            }
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
        Backers backer = backerMapper.getMyBacker(receivedId.getUserId());
        if (!ObjectUtils.isEmpty(backer)) {
            Account account = accountMapper.queryAccount(backer.getBackerId());
            if (!ObjectUtils.isEmpty(account)) {
                result.put("userId", account.getId().toString());
                result.put("userName", account.getUserName());
                result.put("headImgUrl", account.getHeadImgUrl());
                result.put("gender", account.getGender().toString());
                result.put("userSign", account.getUserSign());
            }
        }
        if (result.size() == 0) {
            return DtoUtil.getSuccessDto("查询成功", 200000);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
    }
}
