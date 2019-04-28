package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.AccentService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Account;
import com.modcreater.tmauth.dao.AccentMapper;
import com.modcreater.tmbeans.vo.AccountVo;
import com.modcreater.tmbeans.vo.LoginVo;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccentServiceImpl implements AccentService {
    @Resource
    private AccentMapper accentMapper;
    @Override
    public Dto doLogin(LoginVo loginVo) {
        if (ObjectUtils.isEmpty(loginVo)){
            return DtoUtil.getFalseDto("登录信息获取失败",10000);
        }
        //判断是否新用户
        int code=accentMapper.checkCode(loginVo.getUserCode());
        if (code==0){
            //是新用户注册
            Account account=new Account();
            account.setUserCode(loginVo.getUserCode());
            account.setUserType(loginVo.getUserType());
            account.setUserName("未果新用户");
            account.setGender(0);
            account.setBirthday(new Timestamp(0));
            account.setIDcard("");
            account.setOfflineTime(new Timestamp(0));
            account.setHeadImgUrl("");
            account.setTime(new Timestamp(0));
            int add=accentMapper.register(account);
            if (add<=0){
                return DtoUtil.getFalseDto("注册失败！",10001);
            }
            return DtoUtil.getSuccessDto("注册成功");
        }
        //老用户直接登录
        int i=accentMapper.doLogin(loginVo);
        if (i>0){
            return DtoUtil.getSuccessDto("登录成功!");
        }
        return DtoUtil.getFalseDto("登录失败!",10002);
    }

    @Override
    public Dto updateAccent(AccountVo accountVo) {
        return null;
    }

}
