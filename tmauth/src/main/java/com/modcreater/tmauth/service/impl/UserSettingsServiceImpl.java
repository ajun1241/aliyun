package com.modcreater.tmauth.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmauth.service.UserSettingsService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.UserSettings;
import com.modcreater.tmbeans.show.usersettings.ShowFriendList;
import com.modcreater.tmbeans.vo.usersettings.GetFriendListInSettings;
import com.modcreater.tmbeans.vo.usersettings.ReceivedShowFriendList;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmdao.mapper.UserSettingsMapper;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (StringUtils.isEmpty(userId)){
            return DtoUtil.getFalseDto("请先登录",21011);
        }
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateNotAllowed(ReceivedShowFriendList receivedShowFriendList, String token) {
        if (StringUtils.isEmpty(receivedShowFriendList.getId())){
            return DtoUtil.getFalseDto("请先登录",21011);
        }
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(receivedShowFriendList.getId());
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        boolean result = false;
        if (receivedShowFriendList.getShowFriendList().contains("invite")){
            ArrayList list = JSONObject.parseObject(receivedShowFriendList.getShowFriendList(),ArrayList.class);
            for (Object o : list){
                ShowFriendList showFriendList = JSONObject.parseObject((String) o, ShowFriendList.class);
                if (showFriendList.getStatus().equals("0")){
                    showFriendList.setStatus("1");
                }else {
                    showFriendList.setStatus("0");
                }
                result = (userSettingsMapper.updateUserSettingsToFriends("invite", showFriendList.getUserId(),Integer.valueOf(showFriendList.getStatus()),receivedShowFriendList.getId())) > 0;
            }
        }else if (receivedShowFriendList.getShowFriendList().contains("sustain")){
            ArrayList list = JSONObject.parseObject(receivedShowFriendList.getShowFriendList(),ArrayList.class);
            for (Object o : list){
                ShowFriendList showFriendListForSupport = JSONObject.parseObject((String) o,ShowFriendList.class);
                if (showFriendListForSupport.getStatus().equals("0")){
                    showFriendListForSupport.setStatus("1");
                }else {
                    showFriendListForSupport.setStatus("0");
                }
                result = (userSettingsMapper.updateUserSettingsToFriends("sustain",showFriendListForSupport.getUserId(),Integer.valueOf(showFriendListForSupport.getStatus()),receivedShowFriendList.getId())) > 0;
            }
        }else if (receivedShowFriendList.getShowFriendList().contains("hide")){
            ArrayList list = JSONObject.parseObject(receivedShowFriendList.getShowFriendList(),ArrayList.class);
            for (Object o : list){
                ShowFriendList showFriendListForHide = JSONObject.parseObject((String) o,ShowFriendList.class);
                if (showFriendListForHide.getStatus().equals("0")){
                    showFriendListForHide.setStatus("1");
                }else {
                    showFriendListForHide.setStatus("0");
                }
                result = (userSettingsMapper.updateUserSettingsToFriends("hide",showFriendListForHide.getUserId(),Integer.valueOf(showFriendListForHide.getStatus()),receivedShowFriendList.getId())) > 0;
            }
        }
        if (result){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50005);
    }

    @Override
    public Dto getUserSettings(String userId, String token) {
        if (StringUtils.isEmpty(userId)){
            return DtoUtil.getFalseDto("请先登录",21011);
        }
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        UserSettings userSettings = userSettingsMapper.queryAllSettings(userId);
        if (ObjectUtils.isEmpty(userSettings)){
            return DtoUtil.getFalseDto("获取用户云端设置信息失败",50004);
        }
        return DtoUtil.getSuccesWithDataDto("获取用户设置成功",userSettings,100000);
    }

    @Override
    public Dto getFriendList(GetFriendListInSettings getFriendListInSettings, String token) {
        if (!StringUtils.hasText(getFriendListInSettings.getUserId())){
            return DtoUtil.getFalseDto("请先登录",21011);
        }
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(getFriendListInSettings.getUserId());
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        Map<String,Object> result = new HashMap<>(1);
        if ("invite".equals(getFriendListInSettings.getUpdateType())){
            List<ShowFriendList> showFriendListForInviteList = userSettingsMapper.getInviteFriendList(getFriendListInSettings.getUserId());
            if (showFriendListForInviteList.size() != 0){
                for (ShowFriendList showFriendList : showFriendListForInviteList){
                    showFriendList.setUpdateType("invite");
                }
                result.put("friendList",showFriendListForInviteList);
                return DtoUtil.getSuccesWithDataDto("加载好友列表成功",result,100000);
            }
        }else if ("sustain".equals(getFriendListInSettings.getUpdateType())){
            List<ShowFriendList> showFriendListForSupportList = userSettingsMapper.getSupportFriendList(getFriendListInSettings.getUserId());
            if (showFriendListForSupportList.size() != 0){
                for (ShowFriendList showFriendList : showFriendListForSupportList){
                    showFriendList.setUpdateType("sustain");
                }
                result.put("friendList",showFriendListForSupportList);
                return DtoUtil.getSuccesWithDataDto("加载好友列表成功",result,100000);
            }
        }else if ("hide".equals(getFriendListInSettings.getUpdateType())){
            List<ShowFriendList> showFriendListForHideList = userSettingsMapper.getHideFriendList(getFriendListInSettings.getUserId());
            if (showFriendListForHideList.size() != 0){
                for (ShowFriendList showFriendList : showFriendListForHideList){
                    showFriendList.setUpdateType("hide");
                }
                result.put("friendList",showFriendListForHideList);
                return DtoUtil.getSuccesWithDataDto("加载好友列表成功",result,100000);
            }
        }
        return DtoUtil.getFalseDto("未找到好友",200000);
    }
}
