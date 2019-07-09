package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.DeviceTokenService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.DeviceTokenVo;
import com.modcreater.tmdao.mapper.DeviceTokenMapper;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/7/9 13:45
 */
@Service
public class DeviceTokenServiceImpl implements DeviceTokenService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private DeviceTokenMapper deviceTokenMapper;

    /**
     * 生成/置换DeviceToken 和 appType
     * @param deviceTokenVo
     * @param token
     * @return
     */
    @Override
    public Dto replaceDeviceToken(DeviceTokenVo deviceTokenVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(deviceTokenVo.getUserId()))) {
            return DtoUtil.getFalseDto("请重新登录", 21013);
        }
        //判断是新增还是更换
        if (ObjectUtils.isEmpty(deviceTokenMapper.queryDeviceToken(deviceTokenVo.getUserId()))){
            //新增
            deviceTokenMapper.insertDeviceToken(deviceTokenVo.getUserId(),deviceTokenVo.getDeviceToken(),deviceTokenVo.getAppType());
        }else {
            //修改
            deviceTokenMapper.updDeviceToken(deviceTokenVo.getUserId(),deviceTokenVo.getDeviceToken(),deviceTokenVo.getAppType());
        }
        return null;
    }
}
