package com.modcreater.tmstore.service.impl;

import com.alibaba.fastjson.JSON;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Account;
import com.modcreater.tmbeans.pojo.StoreAttestation;
import com.modcreater.tmbeans.pojo.StoreInfo;
import com.modcreater.tmbeans.pojo.SuperAdministrator;
import com.modcreater.tmbeans.vo.store.ApproveInfoVo;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmdao.mapper.StoreMapper;
import com.modcreater.tmdao.mapper.SuperAdminMapper;
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

    @Resource
    private SuperAdminMapper superAdminMapper;

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
        storeAttestation.setExequatur(JSON.toJSONString(approveInfoVo.getExequatur()));
        storeAttestation.setStoreLogo(approveInfoVo.getStoreLogo());
        storeAttestation.setUserId(approveInfoVo.getUserId());
        int i=storeMapper.insertStoreAttestation(storeAttestation);
        if (i==0){
            return DtoUtil.getFalseDto("上传商铺认证信息失败",21022);
        }
        //通知管理员
        List<SuperAdministrator> superAdministrators=superAdminMapper.querySuperAdmins();
        List<String> emails=new ArrayList<>();
        String title="【智袖】";
        String content="有新的【商铺认证】等待您的确认！";
        for (SuperAdministrator administrators : superAdministrators) {
            if (!StringUtils.isEmpty(administrators.getEmail())){
                emails.add(administrators.getEmail());
            }
        }
        System.out.println("接收的邮箱"+emails);
        SendMsgUtil.asynSendEmail(title,content,emails);
        return DtoUtil.getSuccessDto("上传商铺认证信息成功，请耐心等待",100000);
    }

    /**
     * 查询商铺信息
     * @param receivedId
     * @param token
     * @return
     */
    @Override
    public Dto queryStoreInfo(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        Map<String,Object> resultMap=new HashMap<>(3);
        //查询用户信息
        Account account=accountMapper.queryAccount(receivedId.getUserId());
        Map<String,Object> accountMap=new HashMap<>(2);
        accountMap.put("userName",account.getUserName());
        //查询认证状态  0:未认证；1：认证中；2：已认证；3：未通过
        StoreAttestation storeAttestation=storeMapper.getDisposeStatus(receivedId.getUserId());
        if (ObjectUtils.isEmpty(storeAttestation)){
            resultMap.put("disposeStatus",0);
        }else if (storeAttestation.getDisposeStatus()==0L){
            resultMap.put("disposeStatus",1);
        }else if (storeAttestation.getDisposeStatus()==1L){
            resultMap.put("disposeStatus",2);
        }else if (storeAttestation.getDisposeStatus()==2L){
            resultMap.put("disposeStatus",3);
        }
        //查询商铺信息
        StoreInfo storeInfo=new StoreInfo();
        if (!ObjectUtils.isEmpty(storeAttestation)){
            storeInfo=storeMapper.getStoreInfoByAttestationId(storeAttestation.getId());
        }
        if (ObjectUtils.isEmpty(storeInfo)){
            storeInfo=new StoreInfo();
        }
        Map<String,Object> storeMap=new HashMap<>(3);
        storeMap.put("storeId",storeInfo.getId());
        storeMap.put("storeName",storeInfo.getStoreName());
        storeMap.put("storePicture",storeInfo.getStorePicture());
        storeMap.put("storeAddress",storeInfo.getStoreAddress());
        //查询余额
        accountMap.put("balance", storeInfo.getWallet());
        resultMap.put("account",accountMap);
        resultMap.put("storeInfo",storeMap);
        return DtoUtil.getSuccesWithDataDto("查询成功",resultMap,100000);
    }
}
