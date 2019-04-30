package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.AccountService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Account;
import com.modcreater.tmbeans.vo.AccountVo;
import com.modcreater.tmbeans.vo.LoginVo;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmutils.DateUtil;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AccountServiceImpl implements AccountService {
    @Resource
    private AccountMapper accountMapper;
    @Override
    public Dto doLogin(LoginVo loginVo) {
        Map map=new HashMap();
        if (ObjectUtils.isEmpty(loginVo)){
            return DtoUtil.getFalseDto("登录信息接收失败",11001);
        }
        //判断是否新用户
        String code= accountMapper.checkCode(loginVo.getUserCode());
        if (ObjectUtils.isEmpty(code)){
            //是新用户注册
            Account account=new Account();
            account.setUserCode(loginVo.getUserCode());
            account.setUserType(loginVo.getUserType());
            account.setUserName("未果新用户");
            account.setGender(0);
            account.setBirthday(new Date());
            account.setIDcard("");
            account.setOfflineTime(null);
            account.setHeadImgUrl("");
            account.setTime(null);
            int add= accountMapper.register(account);
            if (add<=0){
                return DtoUtil.getFalseDto("注册失败！",11002);
            }
            String id= accountMapper.checkCode(loginVo.getUserCode());
            if (ObjectUtils.isEmpty(id)){
                return DtoUtil.getFalseDto("注册时没有查询到用户id",200000);
            }
            map.put("id",id);
            return DtoUtil.getSuccesWithDataDto("注册成功",map,100000);
        }
        //老用户直接登录
        String id= accountMapper.doLogin(loginVo);
        if (ObjectUtils.isEmpty(id)){
            return DtoUtil.getFalseDto("登录失败!",200000);
        }
        map.put("id",id);
        return DtoUtil.getSuccesWithDataDto("登录成功!",map,100000);
    }

    @Override
    public Dto queryAccount(String id) {
        if (ObjectUtils.isEmpty(id)){
            return DtoUtil.getFalseDto("用户id接收失败",12001);
        }
        Account account= accountMapper.queryAccount(id);
        System.out.println(account.toString());
        if (ObjectUtils.isEmpty(account)){
            return DtoUtil.getFalseDto("查询用户信息失败",200000);
        }
        AccountVo accountVo=new AccountVo();
        accountVo.setId(account.getId());
        accountVo.setUserCode(account.getUserCode());
        accountVo.setUserName(account.getUserName());
        accountVo.setGender(account.getGender());
        try {
            accountVo.setBirthday(DateUtil.dateToStamp(account.getBirthday()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        accountVo.setHeadImgUrl("");
        accountVo.setUserType(account.getUserType());
        return DtoUtil.getSuccesWithDataDto("查询用户信息成功",accountVo,100000);
    }

    @Override
    public Dto updateAccount(AccountVo accountVo) {
        if (ObjectUtils.isEmpty(accountVo)){
            return DtoUtil.getFalseDto("用户信息接收失败",13001);
        }
        Account account=new Account();
        account.setId(accountVo.getId());
        account.setUserCode(accountVo.getUserCode());
        account.setUserName(accountVo.getUserName());
        account.setGender(accountVo.getGender());
        account.setUserType(accountVo.getUserType());
        account.setBirthday(DateUtil.stampToDate(accountVo.getBirthday()));
        int result=accountMapper.updateAccount(account);
        if (result<=0){
            return DtoUtil.getFalseDto("用户信息修改失败",13002);
        }
        return DtoUtil.getSuccessDto("用户信息修改成功",100000);
    }
}
