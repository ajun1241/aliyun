package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.AppService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.AppVersion;
import com.modcreater.tmbeans.pojo.UserNotice;
import com.modcreater.tmbeans.vo.app.ReceivedAppInfo;
import com.modcreater.tmbeans.vo.app.ReceivedNotice;
import com.modcreater.tmdao.mapper.AppMapper;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-03
 * Time: 9:01
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AppServiceImpl implements AppService {

    @Resource
    private AppMapper appMapper;

    @Override
    public Dto updateApp(ReceivedAppInfo appInfo, HttpServletRequest request) {
        AppVersion appVersion = appMapper.getAppVersion(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        Map<String, String> result = new HashMap<>();
        result.put("type", "0");
        result.put("apkUrl", null);
        if (!appVersion.getAppVersion().equals(appInfo.getAppVersion())) {
            appVersion.setUpdateTimes(appVersion.getUpdateTimes() + 1);
            appMapper.updateUpdateTimes(appVersion.getUpdateTimes(), appVersion.getUploadTime());
            result.put("type", "1");
            result.put("apkUrl", appVersion.getApkUrl());
            return DtoUtil.getSuccesWithDataDto("需要更新", result, 100000);
        }
        return DtoUtil.getSuccesWithDataDto("已是最新版本", result, 100000);
    }

    @Override
    public Dto getAuthenticationActivityNotice(ReceivedNotice receivedNotice, String token) {
        if (ObjectUtils.isEmpty(receivedNotice)) {
            return DtoUtil.getSuccesWithDataDto("公告获取失败", null,200000);
        }
        if (!StringUtils.hasText(receivedNotice.getNoticeName()) || !StringUtils.hasText(receivedNotice.getNoticeTypeId())) {
            return DtoUtil.getSuccesWithDataDto("公告获取失败",null, 200000);
        }
        UserNotice userNotice = appMapper.getUserNotice(receivedNotice.getUserId());
        if (ObjectUtils.isEmpty(userNotice)) {
            if (appMapper.addUserNotice(receivedNotice.getUserId(),receivedNotice.getNoticeName()) == 0) {
                return DtoUtil.getSuccesWithDataDto("操作失败", null,200000);
            }
        }
        userNotice = appMapper.getUserNotice(receivedNotice.getUserId());
        if (userNotice.getTodayNotifications() == 1) {
            return DtoUtil.getSuccesWithDataDto("已经通知过了",null, 200000);
        }
        String content = appMapper.getNoticeContent(receivedNotice.getNoticeTypeId(), receivedNotice.getNoticeName(),receivedNotice.getDate());
        if (StringUtils.hasText(content)) {
            UserNotice user = new UserNotice();
            user.setUserId(receivedNotice.getUserId());
            user.setNoticeName(receivedNotice.getNoticeName());
            user.setTodayNotifications(1L);
            appMapper.updateUserNotice(user);
            return DtoUtil.getSuccesWithDataDto("获取成功", content, 100000);
        }
        return DtoUtil.getSuccesWithDataDto("未获取到公告",null,200000);
    }
}
