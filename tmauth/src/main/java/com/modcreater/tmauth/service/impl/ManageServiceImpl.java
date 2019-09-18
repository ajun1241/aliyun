package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.ManageService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.AfterSale;
import com.modcreater.tmbeans.pojo.SuperAdministrator;
import com.modcreater.tmbeans.pojo.UserRealInfo;
import com.modcreater.tmbeans.vo.ComplaintVo;
import com.modcreater.tmbeans.vo.realname.ReceivedStudentRealInfo;
import com.modcreater.tmbeans.vo.realname.ReceivedUserRealInfo;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmdao.mapper.AfterSaleMapper;
import com.modcreater.tmdao.mapper.ComplaintMapper;
import com.modcreater.tmdao.mapper.SuperAdminMapper;
import com.modcreater.tmdao.mapper.UserRealInfoMapper;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmutils.SendMsgUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Resource
    private SuperAdminMapper superAdminMapper;

    /**
     * 上传用户真实信息
     * @param receivedUserRealInfo
     * @param token
     * @return
     */
    @Override
    public synchronized Dto uploadUserRealInfo(ReceivedUserRealInfo receivedUserRealInfo, String token) {
        receivedUserRealInfo.setCategory("1");
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
            return DtoUtil.getFalseDto("证件号码有误",50003);
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
        //通知管理员
        List<SuperAdministrator> superAdministrators=superAdminMapper.querySuperAdmins();
        List<String> emails=new ArrayList<>();
        String title="【智袖】";
        String content="有新的实名认证等待您的确认！";
        for (SuperAdministrator administrators : superAdministrators) {
            if (!StringUtils.isEmpty(administrators.getEmail())){
                emails.add(administrators.getEmail());
            }
        }
        System.out.println("接收的邮箱"+emails);
        SendMsgUtil.asynSendEmail(title,content,emails);
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
        //通知管理员
        List<SuperAdministrator> superAdministrators=superAdminMapper.querySuperAdmins();
        List<String> emails=new ArrayList<>();
        String title="【智袖】";
        String content="有用户需要更换实名信息！";
        for (SuperAdministrator administrators : superAdministrators) {
            if (!StringUtils.isEmpty(administrators.getEmail())){
                emails.add(administrators.getEmail());
            }
        }
        System.out.println("接收的邮箱"+emails);
        SendMsgUtil.asynSendEmail(title,content,emails);
        return DtoUtil.getSuccessDto("提交成功",100000);
    }

    /**
     * 学生实名认证
     * @param receivedStudentRealInfo
     * @param token
     * @return
     */
    @Override
    public Dto uploadStudentRealInfo(ReceivedStudentRealInfo receivedStudentRealInfo, String token) {
        ReceivedUserRealInfo receivedUserRealInfo=new ReceivedUserRealInfo();
        receivedUserRealInfo.setUserId(receivedStudentRealInfo.getUserId());
        receivedUserRealInfo.setAppType(receivedStudentRealInfo.getAppType());
        receivedUserRealInfo.setUserIDCardFront(receivedStudentRealInfo.getStudentIDCardFront());
        receivedUserRealInfo.setUserIDCardVerso(receivedStudentRealInfo.getStudentIDCardVerso());
        receivedUserRealInfo.setUserIDNo(receivedStudentRealInfo.getStudentIDNo());
        receivedUserRealInfo.setUserRealName(receivedStudentRealInfo.getUserRealName());
        receivedUserRealInfo.setCategory("2");
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedUserRealInfo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
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
     * 查询实名认证信息
     * @param receivedId
     * @param token
     * @return
     */
    @Override
    public Dto queryRealInfo(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        UserRealInfo userRealInfo=userRealInfoMapper.queryDetail(receivedId.getUserId());
        Map<String,Object> map=new HashMap<>();
        if (ObjectUtils.isEmpty(userRealInfo)){
            map.put("realStatus",3L);
            return DtoUtil.getSuccesWithDataDto("您尚未进行实名认证",map,100000);
        }
        String category=userRealInfo.getCategory().toString();
        String userIDNo=userRealInfo.getUserIdNo();
        String IDNo="";
        if ("1".equals(category)){
            category="身份证";
            char[] chars=userIDNo.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (i>3 && i<14){
                    chars[i]='*';
                }
                IDNo=IDNo+chars[i];
            }
        }else if ("2".equals(category)){
            IDNo=userIDNo;
            category="学生证";
        }
        String modifyDate="";
        if (!ObjectUtils.isEmpty(userRealInfo.getModifyDate())){
            modifyDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(userRealInfo.getModifyDate());
        }
        map.put("id",userRealInfo.getId());
        map.put("userId",userRealInfo.getUserId());
        map.put("userRealName",userRealInfo.getUserRealName());
        map.put("userIDNo",IDNo);
        map.put("modifyDate",modifyDate);
        map.put("category",category);
        map.put("realStatus",userRealInfo.getRealStatus());
        return DtoUtil.getSuccesWithDataDto("查询成功",map,100000);
    }
}
