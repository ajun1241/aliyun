package com.modcreater.tmstore.service.impl;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Account;
import com.modcreater.tmbeans.pojo.StoreAttestation;
import com.modcreater.tmbeans.vo.store.ApproveInfoVo;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmdao.mapper.StoreMapper;
import com.modcreater.tmstore.service.StoreService;
import com.modcreater.tmutils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @Author: AJun
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class StoreServiceImpl implements StoreService {

    private static final String SYSTEMID = "100000";
    private static final String ANDROID = "android";
    private static final String IOS = "ios";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private StoreMapper storeMapper;
    @Resource
    private AccountMapper accountMapper;

    RongCloudMethodUtil rongCloudMethodUtil =new RongCloudMethodUtil();

    private Logger logger = LoggerFactory.getLogger(StoreServiceImpl.class);

    /**
     * 查询认证页面信息
     * @param approveInfoVo
     * @param token
     * @return
     */
    @Override
    public Dto queryAccountInfo(ApproveInfoVo approveInfoVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(approveInfoVo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        Account account=accountMapper.queryAccount(approveInfoVo.getUserId());
        Map<String,String> map=new HashMap<>(3);
        if (!ObjectUtils.isEmpty(account)){
            map.put("userId",account.getId().toString());
            map.put("userName",account.getUserName());
            map.put("userCode",account.getUserCode());
        }else {
            map.put("userId","");
            map.put("userName","");
            map.put("userCode","");
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",map,100000);
    }

    /**
     * 上传商铺认证信息
     * @param approveInfoVo
     * @param token
     * @return
     */
    @Override
    public Dto uploadApproveInfo(ApproveInfoVo approveInfoVo,String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(approveInfoVo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        StoreAttestation storeAttestation=new StoreAttestation();
        storeAttestation.setBusinessLicense(approveInfoVo.getBusinessLicense());
        storeAttestation.setUserId(approveInfoVo.getUserId());
        int i=storeMapper.insertStoreAttestation(storeAttestation);
        if (i==0){
            return DtoUtil.getFalseDto("上传商铺认证信息失败",21022);
        }
        return DtoUtil.getSuccessDto("上传商铺认证信息成功，请耐心等待",100000);
    }
}