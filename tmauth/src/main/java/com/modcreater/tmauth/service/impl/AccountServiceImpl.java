package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.AccountService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Account;
import com.modcreater.tmbeans.vo.AccountVo;
import com.modcreater.tmbeans.vo.AddPwdVo;
import com.modcreater.tmbeans.vo.LoginVo;
import com.modcreater.tmbeans.vo.QueryUserVo;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmutils.DateUtil;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmutils.TokenUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {
    @Resource
    private AccountMapper accountMapper;
    private static Pattern pattern = Pattern.compile("[0-9]*");
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
        accountVo.setBirthday(account.getBirthday());
        accountVo.setTime(account.getTime());
//        accountVo.setHeadImgUrl("");
        accountVo.setUserType(account.getUserType());
        return DtoUtil.getSuccesWithDataDto("登录成功!",accountVo,100000);
    }
    /**
     * 注册/登录
     * @param loginVo
     * @return
     */
    @Override
    public Dto registered(LoginVo loginVo) {
        if (ObjectUtils.isEmpty(loginVo)){
            return DtoUtil.getFalseDto("注册信息接收失败",14001);
        }
        String token=null;
        Account account=new Account();
        Account result=accountMapper.checkCode(loginVo.getUserCode());
        //如果已经注册
        if (!ObjectUtils.isEmpty(result)){
            if (StringUtils.isEmpty(result.getUserPassword())){
                result= accountMapper.checkCode(loginVo.getUserCode());
                if (ObjectUtils.isEmpty(result)){
                    return DtoUtil.getFalseDto("注册时查询用户失败",14004);
                }
                return DtoUtil.getSuccesWithDataDto("注册成功，但是没有设置密码",result,14002);
            }
            //登录
            //生成token
            TokenUtil tokenUtil=new TokenUtil();
            try {
                token=tokenUtil.createToken(result.getId().toString(),result.getUserName(),result.getHeadImgUrl());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (StringUtils.isEmpty(token)){
                return DtoUtil.getFalseDto("生成token失败",14005);
            }
            //把token保存在数据库
            account.setId(result.getId());
            account.setToken(token);
            if (accountMapper.updateAccount(account)<=0){
                return DtoUtil.getFalseDto("token保存失败",14006);
            }
            return DtoUtil.getSuccesWithDataDto("登录成功",token,100000);
        }
        //未注册时
        account.setUserCode(loginVo.getUserCode());
        account.setUserType(loginVo.getUserType());
        account.setUserName("未果新用户");
        account.setGender(0L);
        try {
            account.setBirthday(DateUtil.dateToStamp(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        return DtoUtil.getSuccesWithDataDto("注册成功，但是没有设置密码",result,14002);
    }
    /**
     *添加二级密码
     * @param addPwdVo
     * @return
     */
    @Override
    public Dto addPassword(AddPwdVo addPwdVo) {
        if (ObjectUtils.isEmpty(addPwdVo.toString())){
            return DtoUtil.getFalseDto("添加密码用户数据接收失败",15001);
        }
        if (StringUtils.isEmpty(addPwdVo.getUserPassword())){
            return DtoUtil.getFalseDto("密码不能为空",15002);
        }
        //生成token
        String token=null;
        TokenUtil tokenUtil=new TokenUtil();
        try {
            token=tokenUtil.createToken(addPwdVo.getUserId(),addPwdVo.getUserName(),addPwdVo.getHeadImgUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("生成token失败",15004);
        }
        Account account=new Account();
        account.setId(Long.parseLong(addPwdVo.getUserId()));
        account.setUserPassword(addPwdVo.getUserPassword());
        account.setToken(token);
        if (accountMapper.updateAccount(account)<=0){
            return DtoUtil.getFalseDto("添加密码失败",15003);
        }
        return DtoUtil.getSuccesWithDataDto("添加密码成功",token,100000);
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
        accountVo.setBirthday(account.getBirthday());
//        accountVo.setHeadImgUrl("");
        accountVo.setUserType(account.getUserType());
        accountVo.setTime(account.getTime());
        return DtoUtil.getSuccesWithDataDto("查询用户信息成功",accountVo,100000);
    }

    @Override
    public Dto updateAccount(AccountVo accountVo) {
        if (ObjectUtils.isEmpty(accountVo)){
            return DtoUtil.getFalseDto("用户信息接收失败",13001);
        }
        //判断日期格式

        Matcher isNum = pattern.matcher(accountVo.getBirthday());
        if(!isNum.matches()){
            return DtoUtil.getFalseDto("用户生日格式不正确",13004);
        }
        Account account=new Account();
        account.setId(accountVo.getId());
        account.setUserName(accountVo.getUserName());
        account.setGender(accountVo.getGender());
        account.setUserType(accountVo.getUserType());
        account.setBirthday(accountVo.getBirthday());
        account.setModifyDate(new Date());
        int result=accountMapper.updateAccount(account);
        if (result<=0){
            return DtoUtil.getFalseDto("用户信息修改失败",13002);
        }
        return DtoUtil.getSuccessDto("用户信息修改成功",100000);
    }
}
