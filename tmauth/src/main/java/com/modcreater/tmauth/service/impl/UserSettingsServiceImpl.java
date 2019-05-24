package com.modcreater.tmauth.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmauth.service.UserSettingsService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.UserSettings;
import com.modcreater.tmbeans.show.usersettings.ShowFriendListForInvite;
import com.modcreater.tmbeans.show.usersettings.ShowFriendListForSupport;
import com.modcreater.tmbeans.vo.usersettings.GetFriendListInSettings;
import com.modcreater.tmbeans.vo.usersettings.ReceivedShowFriendList;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmdao.mapper.UserSettingsMapper;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmutils.SingleEventUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-21
 * Time: 10:15
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserSettingsServiceImpl implements UserSettingsService {

    @Resource
    private UserSettingsMapper userSettingsMapper;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Dto updateUserSettings(int status, String userId, String type, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateNotAllowed(ReceivedShowFriendList receivedShowFriendList, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(receivedShowFriendList.getId());
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        boolean result = false;
        if (receivedShowFriendList.getShowFriendList().contains("invite")){
            ArrayList list = JSONObject.parseObject(receivedShowFriendList.getShowFriendList(),ArrayList.class);
            for (Object o : list){
                ShowFriendListForInvite showFriendListForInvite = JSONObject.parseObject((String) o,ShowFriendListForInvite.class);
                result = (userSettingsMapper.updateUserSettings("invite",showFriendListForInvite.getUserId(),Integer.valueOf(showFriendListForInvite.getStatus()))) > 0;
            }
        }else {
            ArrayList list = JSONObject.parseObject(receivedShowFriendList.getShowFriendList(),ArrayList.class);
            for (Object o : list){
                ShowFriendListForSupport showFriendListForSupport = JSONObject.parseObject((String) o,ShowFriendListForSupport.class);
                result = (userSettingsMapper.updateUserSettings("sustain",showFriendListForSupport.getUserId(),Integer.valueOf(showFriendListForSupport.getStatus()))) > 0;
            }
        }
        if (result){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50005);
    }

    @Override
    public Dto getUserSettings(String userId, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        UserSettings userSettings = userSettingsMapper.queryAllSettings(userId);
        if (ObjectUtils.isEmpty(userSettings)){
            return DtoUtil.getFalseDto("获取用户云端设置信息失败",50004);
        }
        return DtoUtil.getSuccesWithDataDto("获取用户设置成功",userSettings,100000);
    }

    @Override
    public Dto getFriendList(GetFriendListInSettings getFriendListInSettings, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(getFriendListInSettings.getUserId());
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        if ("invite".equals(getFriendListInSettings.getUpdateType())){
            List<ShowFriendListForInvite> showFriendListForInviteList = userSettingsMapper.getInviteFriendList(getFriendListInSettings.getUserId());
            if (showFriendListForInviteList.size() != 0){
                for (ShowFriendListForInvite showFriendListForInvite : showFriendListForInviteList){
                    showFriendListForInvite.setUpdateType("invite");
                }
                return DtoUtil.getSuccesWithDataDto("加载好友列表成功",showFriendListForInviteList,100000);
            }
        }else {
            List<ShowFriendListForSupport> showFriendListForSupportList = userSettingsMapper.getSupportFriendList(getFriendListInSettings.getUserId());
            if (showFriendListForSupportList.size() != 0){
                for (ShowFriendListForSupport showFriendListForSupport : showFriendListForSupportList){
                    showFriendListForSupport.setUpdateType("sustain");
                }
                return DtoUtil.getSuccesWithDataDto("加载好友列表成功",showFriendListForSupportList,100000);
            }
        }
        return DtoUtil.getFalseDto("未找到好友",100000);
    }
}
