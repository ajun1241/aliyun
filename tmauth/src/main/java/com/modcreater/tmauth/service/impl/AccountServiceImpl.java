package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.AccountService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Account;
import com.modcreater.tmbeans.vo.AccountVo;
import com.modcreater.tmbeans.vo.LoginVo;
import com.modcreater.tmbeans.vo.QueryUserVo;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmutils.DateUtil;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AccountServiceImpl implements AccountService {
    @Resource
    private AccountMapper accountMapper;
    @Override
    public Dto doLogin(LoginVo loginVo) {
        if (ObjectUtils.isEmpty(loginVo)){
            return DtoUtil.getFalseDto("登录信息接收失败",11001);
        }
        AccountVo accountVo=new AccountVo();
        //判断是否新用户
        Account result= accountMapper.checkCode(loginVo.getUserCode());
        if (ObjectUtils.isEmpty(result)){
            //是新用户注册
           /* Account account=new Account();
            account.setUserCode(loginVo.getUserCode());
            account.setUserType(loginVo.getUserType());
            account.setUserName("未果新用户");
            account.setGender(0L);
            account.setBirthday(new Date());
            account.setIDcard("");
            account.setOfflineTime(null);
            account.setHeadImgUrl("");
            account.setTime(null);
            int add= accountMapper.register(account);
            if (add<=0){
                return DtoUtil.getFalseDto("注册失败！",11002);
            }
            result= accountMapper.checkCode(loginVo.getUserCode());
            if (ObjectUtils.isEmpty(result)){
                return DtoUtil.getFalseDto("注册时查找用户失败",11003);
            }
            accountVo.setId(result.getId());
            accountVo.setUserCode(result.getUserCode());
            accountVo.setUserName(result.getUserName());
            accountVo.setGender(result.getGender());
            try {
                accountVo.setBirthday(DateUtil.dateToStamp(result.getBirthday()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
//        accountVo.setHeadImgUrl("");
            accountVo.setUserType(result.getUserType());*/
            return DtoUtil.getFalseDto("用户未注册，请先注册",11002);
        }
        //老用户直接登录
        Account account= accountMapper.doLogin(loginVo);
        if (ObjectUtils.isEmpty(account)){
            return DtoUtil.getFalseDto("登录失败,用户名或密码错误",200000);
        }
//        Account account=accountMapper.queryAccount(id);
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
        return DtoUtil.getSuccesWithDataDto("登录成功!",accountVo,100000);
    }

    @Override
    public Dto registered(LoginVo loginVo) {
        if (ObjectUtils.isEmpty(loginVo)){
            return DtoUtil.getFalseDto("注册信息接收失败",14001);
        }
        Account result=accountMapper.checkCode(loginVo.getUserCode());
        if (!ObjectUtils.isEmpty(result)){
            return DtoUtil.getFalseDto("用户已存在，请直接登录",14002);
        }
        Account account=new Account();
        account.setUserCode(loginVo.getUserCode());
        account.setUserPassword(loginVo.getUserPassword());
        account.setUserType(loginVo.getUserType());
        account.setUserName("未果新用户");
        account.setGender(0L);
        account.setBirthday(new Date());
        account.setIDcard("");
        account.setOfflineTime(null);
        account.setHeadImgUrl("");
        account.setTime(null);
        int add= accountMapper.register(account);
        if (add<=0){
            return DtoUtil.getFalseDto("注册失败！",14003);
        }
        result= accountMapper.checkCode(loginVo.getUserCode());
        if (ObjectUtils.isEmpty(result)){
            return DtoUtil.getFalseDto("注册时查找用户失败",14004);
        }
        AccountVo accountVo=new AccountVo();
        accountVo.setId(result.getId());
        accountVo.setUserCode(result.getUserCode());
        accountVo.setUserName(result.getUserName());
        accountVo.setGender(result.getGender());
        try {
            accountVo.setBirthday(DateUtil.dateToStamp(result.getBirthday()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        accountVo.setHeadImgUrl("");
        accountVo.setUserType(result.getUserType());
        return DtoUtil.getSuccesWithDataDto("注册成功",accountVo,100000);
    }

    @Override
    public Dto queryAccount(QueryUserVo queryUserVo) {
        if (ObjectUtils.isEmpty(queryUserVo)){
            return DtoUtil.getFalseDto("用户数据接收失败",12001);
        }
        Account account= accountMapper.queryAccount(queryUserVo.getId());
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
        //判断日期格式
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(accountVo.getBirthday());
        if(!isNum.matches()){
            return DtoUtil.getFalseDto("用户生日格式不正确",13004);
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
