package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.DeviceTokenService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.DeviceTokenVo;
import com.modcreater.tmdao.mapper.DeviceTokenMapper;
import com.modcreater.tmutils.DtoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
    /**
     * 生成/置换DeviceToken 和 appType
     * @param deviceTokenVo
     * @param token
     * @return
     */
    @Override
    public Dto replaceDeviceToken(DeviceTokenVo deviceTokenVo, String token) {
        try {
            if (!token.equals(stringRedisTemplate.opsForValue().get(deviceTokenVo.getUserId()))) {
                return DtoUtil.getFalseDto("请重新登录", 21014);
            }
            logger.info("生成/置换DeviceToken 和 appType:"+deviceTokenVo.toString());
            int i=0;
            //0：安卓；1：苹果
            String type="-1";
            if (deviceTokenVo.getAppType().toLowerCase().indexOf("ios") != -1){
                type="1";
            }else if (deviceTokenVo.getAppType().toLowerCase().indexOf("android") != -1){
                type="0";
            }
            //判断是新增还是更换
            if (ObjectUtils.isEmpty(deviceTokenMapper.queryDeviceToken(deviceTokenVo.getUserId()))){
                //新增
                i=deviceTokenMapper.insertDeviceToken(deviceTokenVo.getUserId(),deviceTokenVo.getDeviceToken(),type);
            }else {
                //修改
                i=deviceTokenMapper.updDeviceToken(deviceTokenVo.getUserId(),deviceTokenVo.getDeviceToken(),type);
            }
            if (i>0){
                return DtoUtil.getSuccessDto("请求成功",100000);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return DtoUtil.getFalseDto("请求失败",21001);
    }
}
