package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.AccountService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Account;
import com.modcreater.tmbeans.vo.AccountVo;
import com.modcreater.tmbeans.vo.AddPwdVo;
import com.modcreater.tmbeans.vo.LoginVo;
import com.modcreater.tmbeans.vo.QueryUserVo;
import com.modcreater.tmbeans.vo.uservo.*;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmutils.DateUtil;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmutils.MD5Util;
import com.modcreater.tmutils.TokenUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
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

/**
 * @Author: AJun
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AccountServiceImpl implements AccountService {
    @Resource
    private AccountMapper accountMapper;
    private static Pattern pattern = Pattern.compile("[0-9]*");
    @Resource
    private StringRedisTemplate stringRedisTemplate;
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
        System.out.println("登录"+loginVo.toString());
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
                return DtoUtil.getSuccesWithDataDto("注册成功，但是没有设置密码",result,100000);
            }
            if (StringUtils.isEmpty(result.getHeadImgUrl())){
                result.setHeadImgUrl("2333");
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
            //把token保存在redis
            stringRedisTemplate.opsForValue().set(result.getId().toString(),token);
            if (accountMapper.updateAccount(account)<=0){
                return DtoUtil.getFalseDto("生成token失败",14006);
            }
            result.setToken(token);
            result.setUserPassword(null);
            return DtoUtil.getSuccesWithDataDto("登录成功",result,100000);
        }
        //未注册时
        account.setUserCode(loginVo.getUserCode());
        account.setUserType(loginVo.getUserType());
        account.setUserName("时系新用户");
        account.setGender(0L);
        try {
            account.setBirthday(DateUtil.dateToStamp(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        account.setIDCard("");
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
        return DtoUtil.getSuccesWithDataDto("注册成功，但是没有设置密码",result,100000);
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
        if (StringUtils.isEmpty(addPwdVo.getHeadImgUrl())){
            addPwdVo.setHeadImgUrl("2333");
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
        account.setUserPassword(MD5Util.createMD5(addPwdVo.getUserPassword()));
        account.setToken(token);
        //把token保存在redis
        stringRedisTemplate.opsForValue().set(addPwdVo.getUserId(),token);
        if (accountMapper.updateAccount(account)<=0){
            return DtoUtil.getFalseDto("添加密码失败",15003);
        }
        account=accountMapper.queryAccount(addPwdVo.getUserId());
        return DtoUtil.getSuccesWithDataDto("添加密码成功",account,100000);
    }

    /**
     * 根据账号搜索好友
     * @param queFridenVo
     * @param token
     * @return
     */
    @Override
    public Dto queryFriendByUserCode(QueFridenVo queFridenVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(token))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        if (ObjectUtils.isEmpty(queFridenVo)){
            return DtoUtil.getFalseDto("搜索好友数据获取失败",16001);
        }
        Account account=accountMapper.queryFriendByUserCode(queFridenVo.getUserCode());
        if (ObjectUtils.isEmpty(account)){
            return DtoUtil.getFalseDto("搜索好友失败",200000);
        }
        return DtoUtil.getSuccesWithDataDto("搜索好友成功",account,100000);
    }
    /**
     * 建立好友关系
     * @param buildFriendshipVo
     * @param token
     * @return
     */
    @Override
    public Dto buildFriendship(BuildFriendshipVo buildFriendshipVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(token))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        if (ObjectUtils.isEmpty(buildFriendshipVo)){
            return DtoUtil.getFalseDto("添加好友数据未获取到",16002);
        }
        //建立双向好友关系
        int i=accountMapper.buildFriendship(buildFriendshipVo);
        String temp=buildFriendshipVo.getUserId();
        buildFriendshipVo.setUserId(buildFriendshipVo.getFriendId());
        buildFriendshipVo.setFriendId(null);
        int j=accountMapper.buildFriendship(buildFriendshipVo);
        if (i<=0||j<=0){
            throw new RuntimeException("添加好友失败");
        }
        return DtoUtil.getSuccessDto("添加好友成功",100000);
    }
    /**
     * 查询好友列表
     * @param queryFriendListVo
     * @param token
     * @return
     */
    @Override
    public Dto queryFriendList(QueryFriendListVo queryFriendListVo, String token) {
        return null;
    }
    /**
     * 修改好友权限
     * @param jurisdictionVo
     * @param token
     * @return
     */
    @Override
    public Dto updateFriendJurisdiction(UpdateFriendJurisdictionVo jurisdictionVo, String token) {
        return null;
    }
    /**
     * 解除好友关系
     * @param deleteFriendshipVo
     * @param token
     * @return
     */
    @Override
    public Dto deleteFriendship(DeleteFriendshipVo deleteFriendshipVo, String token) {
        return null;
    }


    @Override
    public Dto queryAccount(QueryUserVo queryUserVo,String token) {
        if (ObjectUtils.isEmpty(queryUserVo)){
            return DtoUtil.getFalseDto("用户数据接收失败",12001);
        }
        if (StringUtils.isEmpty(queryUserVo.getId())){
            return DtoUtil.getFalseDto("请先登录",21011);
        }
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(queryUserVo.getId());
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
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
    public Dto updateAccount(AccountVo accountVo,String token) {
        if (ObjectUtils.isEmpty(accountVo)){
            return DtoUtil.getFalseDto("用户信息接收失败",13001);
        }
        if (StringUtils.isEmpty(accountVo.getId())){
            return DtoUtil.getFalseDto("请先登录",21011);
        }
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(accountVo.getId());
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
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
