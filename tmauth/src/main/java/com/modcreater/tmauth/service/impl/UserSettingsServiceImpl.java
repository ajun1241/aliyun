package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.UserInfoService;
import com.modcreater.tmauth.service.UserSettingsService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmdao.mapper.UserSettingsMapper;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

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
    public Dto updateNotAllowedInvited(String userId, String friendsIds, String token) {
        return null;
    }

    @Override
    public Dto updateNotAllowedSupported(String userId, String friendsIds, String token) {
        return null;
    }

    @Override
    public Dto updateReceiveNewMessage(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "ReceiveNewMessage";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateNewMessageDetails(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "NewMessageDetails";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateNewMessageSystemNotify(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "NewMessageSystemNotify";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateNewMessageForChat(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "NewMessageForChat";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateDND(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "DND";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateDNDStartTime(String userId, int time, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "DNDStartTime";
        if (userSettingsMapper.updateUserSettings(type,userId,time) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateDNDEndTime(String userId, int time, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "DNDEndTime";
        if (userSettingsMapper.updateUserSettings(type,userId,time) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateImportantAndUrgent(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "ImportantAndUrgent";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateImportant(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "Important";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateUrgent(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "Urgent";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateNotImportantAndUrgent(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "NotImportantAndUrgent";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateOptional(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "Optional";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateFriendInvite(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "FriendInvite";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateFriendSupport(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "FriendSupport";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updatePhoneNumInvite(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "PhoneNumInvite";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateWechatNumInvite(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "WechatNumInvite";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateQQNumInvite(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "QQNumInvite";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateGroupInvite(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "GroupInvite";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateMyIdInvite(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "MyIdInvite";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateOnlyWiFi(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "OnlyWiFi";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateSimplifiedChinese(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "SimplifiedChinese";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateChineseTraditionalForHongKong(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "ChineseTraditionalForHongKong";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateChineseTraditionalForTaiWan(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "ChineseTraditionalForTaiWan";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateForEnglish(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "ForEnglish";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateIndonesia(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "Indonesia";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateJapanese(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "Japanese";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateFrench(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "French";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }

    @Override
    public Dto updateFont(String userId, int status, String token) {
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(userId);
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String type = "Font";
        if (userSettingsMapper.updateUserSettings(type,userId,status) != 0){
            return DtoUtil.getSuccessDto("修改成功",100000);
        }
        return DtoUtil.getFalseDto("修改失败",50001);
    }
}
