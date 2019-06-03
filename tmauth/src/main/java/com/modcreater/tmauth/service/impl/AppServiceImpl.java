package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.AppService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.AppVersion;
import com.modcreater.tmbeans.vo.app.ReceivedAppInfo;
import com.modcreater.tmdao.mapper.AppMapper;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        if (!appVersion.getAppVersion().equals(appInfo.getAppVersion())){
            appVersion.setUpdateTimes(appVersion.getUpdateTimes()+1);
            appMapper.updateUpdateTimes(appVersion.getUpdateTimes(),appVersion.getUploadTime());
            return DtoUtil.getSuccesWithDataDto("",appVersion.getApkUrl(),100000);
        }
        return DtoUtil.getFalseDto("",200000);
    }
}
