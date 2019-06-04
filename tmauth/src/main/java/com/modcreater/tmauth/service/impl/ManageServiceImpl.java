package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.ManageService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.UserRealInfo;
import com.modcreater.tmbeans.vo.ComplaintVo;
import com.modcreater.tmbeans.vo.realname.ReceivedUserRealInfo;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmdao.mapper.UserRealInfoMapper;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/6/4 10:34
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ManageServiceImpl implements ManageService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserRealInfoMapper userRealInfoMapper;

    @Resource
    private AccountMapper accountMapper;

    /**
     * 上传用户真实信息
     * @param receivedUserRealInfo
     * @param token
     * @return
     */
    @Override
    public Dto uploadUserRealInfo(ReceivedUserRealInfo receivedUserRealInfo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(receivedUserRealInfo)){
            return DtoUtil.getFalseDto("上传数据未获取到",21013);
        }
        if (StringUtils.isEmpty(receivedUserRealInfo.getUserId())){
            return DtoUtil.getFalseDto("userId未获取到",21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedUserRealInfo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        //第一次上传认证
        if (ObjectUtils.isEmpty(userRealInfoMapper.queryDetail(receivedUserRealInfo.getUserId()))){
            //上传信息
            if (userRealInfoMapper.addNewRealInfo(receivedUserRealInfo)==0){
                return DtoUtil.getFalseDto("上传数据失败",50001);
            }
        }else {
            //多次更改认证
            //更改信息
            UserRealInfo userRealInfo=new UserRealInfo();
            userRealInfo.setUserId(Long.parseLong(receivedUserRealInfo.getUserId()));
            userRealInfo.setUserRealName(receivedUserRealInfo.getUserRealName());
            userRealInfo.setUserIdNo(receivedUserRealInfo.getUserIDNo());
            userRealInfo.setUserIdCardFront(receivedUserRealInfo.getUserIDCardFront());
            userRealInfo.setUserIdCardVerso(receivedUserRealInfo.getUserIDCardVerso());
            userRealInfo.setRealStatus(0L);
            userRealInfo.setModifyDate(new Date());
            if (userRealInfoMapper.updateRealInfo(userRealInfo)==0){
                return DtoUtil.getFalseDto("上传数据失败",50001);
            }
        }
        return DtoUtil.getSuccessDto("上传成功",100000);
    }

    /**
     * 投诉
     * @param complaintVo
     * @param token
     * @return
     */
    @Override
    public Dto complaint(ComplaintVo complaintVo, String token) {
        return null;
    }
}
