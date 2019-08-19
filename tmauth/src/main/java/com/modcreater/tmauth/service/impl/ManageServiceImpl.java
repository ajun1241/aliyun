package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.ManageService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.AfterSale;
import com.modcreater.tmbeans.pojo.UserRealInfo;
import com.modcreater.tmbeans.vo.ComplaintVo;
import com.modcreater.tmbeans.vo.realname.ReceivedUserRealInfo;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmdao.mapper.AfterSaleMapper;
import com.modcreater.tmdao.mapper.ComplaintMapper;
import com.modcreater.tmdao.mapper.UserRealInfoMapper;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
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
    private ComplaintMapper complaintMapper;

    @Resource
    private AfterSaleMapper afterSaleMapper;

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
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        if (receivedUserRealInfo.getUserIDNo().length()>18){
            return DtoUtil.getFalseDto("身份证号码有误",50003);
        }
        //第一次上传认证
        UserRealInfo userRealInfo1=userRealInfoMapper.queryDetail(receivedUserRealInfo.getUserId());
        if (ObjectUtils.isEmpty(userRealInfo1)){
            //上传信息
            if (userRealInfoMapper.addNewRealInfo(receivedUserRealInfo)==0){
                return DtoUtil.getFalseDto("上传数据失败",50001);
            }
        }else {
            if (userRealInfo1.getRealStatus()==0){
                return DtoUtil.getFalseDto("实名正在认证中，请耐心等待",50006);
            }
            //多次更改认证
            //更改信息
            UserRealInfo userRealInfo=new UserRealInfo();
            userRealInfo.setUserId(Long.parseLong(receivedUserRealInfo.getUserId()));
            userRealInfo.setUserRealName(receivedUserRealInfo.getUserRealName());
            userRealInfo.setUserIdNo(receivedUserRealInfo.getUserIDNo());
            userRealInfo.setUserIdCardFront(receivedUserRealInfo.getUserIDCardFront());
            userRealInfo.setUserIdCardVerso(receivedUserRealInfo.getUserIDCardVerso());
            userRealInfo.setRealStatus(0L);
            userRealInfo.setCreateDate(new Date());
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
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(complaintVo)){
            return DtoUtil.getFalseDto("上传数据未获取到",21013);
        }
        if (StringUtils.isEmpty(complaintVo.getUserId())){
            return DtoUtil.getFalseDto("userId未获取到",21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(complaintVo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        if (complaintMapper.addComplaint(complaintVo)==0){
            return DtoUtil.getFalseDto("投诉上传失败",50010);
        }
        return DtoUtil.getSuccessDto("上传成功",100000);
    }

    /**
     * 更换实名信息
     * @param receivedId
     * @param token
     * @return
     */
    @Override
    public Dto changeRealInfo(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        AfterSale afterSale=new AfterSale();
        afterSale.setUserId(Long.parseLong(receivedId.getUserId()));
        afterSale.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        afterSale.setServiceType("更换实名信息");
        if (afterSaleMapper.changeRealInfo(afterSale)==0){
            return DtoUtil.getFalseDto("服务提交失败",50020);
        }
        return DtoUtil.getSuccessDto("提交成功",100000);
    }
}
