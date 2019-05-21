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
import com.modcreater.tmdao.mapper.AchievementMapper;
import com.modcreater.tmdao.mapper.UserSettingsMapper;
import com.modcreater.tmutils.DateUtil;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmutils.MD5Util;
import com.modcreater.tmutils.RongCloudMethodUtil;
import io.rong.messages.InfoNtfMessage;
import io.rong.messages.TxtMessage;
import io.rong.models.response.ResponseResult;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;
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
    private AchievementMapper achievementMapper;
    @Resource
    private UserSettingsMapper userSettingsMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    RongCloudMethodUtil rongCloudMethodUtil =new RongCloudMethodUtil();

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

            try {
                token= rongCloudMethodUtil.createToken(result.getId().toString(),result.getUserName(),result.getHeadImgUrl());
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
        account.setUserName(loginVo.getUserCode());
        account.setGender(0L);
        try {
            account.setBirthday(DateUtil.dateToStamp(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        account.setIDCard("");
        account.setOfflineTime(null);
        account.setHeadImgUrl("");
        int add= accountMapper.register(account);
        if (add<=0){
            return DtoUtil.getFalseDto("注册失败！",14003);
        }
        result= accountMapper.checkCode(loginVo.getUserCode());
        //注册时添加用户权限信息
        accountMapper.insertUserRight(result.getId().toString());
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

        try {
            token= rongCloudMethodUtil.createToken(addPwdVo.getUserId(),addPwdVo.getUserName(),addPwdVo.getHeadImgUrl());
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
        account.setIsFirst(1);
        //把token保存在redis
        stringRedisTemplate.opsForValue().set(addPwdVo.getUserId(),token);
        //没有实名认证
        if (StringUtils.isEmpty(addPwdVo.getIDCard())&&StringUtils.isEmpty(addPwdVo.getRealName())&&StringUtils.isEmpty(addPwdVo.getUserAddress())){
            if (accountMapper.updateAccount(account)<=0){
                return DtoUtil.getFalseDto("添加密码失败",15003);
            }
        }else {
            //有实名认证
            account.setIDCard(addPwdVo.getIDCard());
            account.setRealName(addPwdVo.getRealName());
            account.setUserAddress(addPwdVo.getUserAddress());
            if (accountMapper.updateAccount(account)<=0){
                return DtoUtil.getFalseDto("添加密码失败",15003);
            }
            //更改实名认证状态(认证中)
            accountMapper.updRealName(addPwdVo.getUserId(),"1");
        }
        account=accountMapper.queryAccount(addPwdVo.getUserId());
        if (achievementMapper.addNewUserStatistics(addPwdVo.getUserId()) == 0){
            return DtoUtil.getFalseDto("为用户添加计数表失败",15005);
        }
        if (userSettingsMapper.addNewUserSettings(addPwdVo.getUserId()) == 0){
            return DtoUtil.getFalseDto("为用户添加设置失败",15006);
        }
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
        if (ObjectUtils.isEmpty(queFridenVo)){
            return DtoUtil.getFalseDto("搜索好友数据获取失败",16001);
        }
        System.out.println("搜索好友："+queFridenVo.toString());
        if (!token.equals(stringRedisTemplate.opsForValue().get(queFridenVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        Account account=accountMapper.queryFriendByUserCode(queFridenVo.getUserCode());
        Map<String,String> map=new HashMap();
        map.put("userId",account.getId().toString());
        map.put("userCode",account.getUserCode());
        map.put("userName",account.getUserName());
        map.put("gender",account.getGender().toString());
        map.put("birthday",account.getBirthday());
        map.put("headImgUrl",account.getHeadImgUrl());
        if (ObjectUtils.isEmpty(account)){
            return DtoUtil.getFalseDto("搜索好友失败",200000);
        }
        return DtoUtil.getSuccesWithDataDto("搜索好友成功",map,100000);
    }

    /**
     * 发送添加好友请求
     * @param sendFriendRequestVo
     * @param token
     * @return
     */
    @Override
    public Dto sendFriendRequest(SendFriendRequestVo sendFriendRequestVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        System.out.println("添加请求"+sendFriendRequestVo.toString());
        if (StringUtils.isEmpty(sendFriendRequestVo.getUserId())|| StringUtils.isEmpty(sendFriendRequestVo.getFriendId())){
            return DtoUtil.getFalseDto("userId和friendId不能为空",17001);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(sendFriendRequestVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        //判断这俩人是不是已经是好友
        if (accountMapper.queryFriendRel(sendFriendRequestVo.getUserId(),sendFriendRequestVo.getFriendId())>0){
            return DtoUtil.getFalseDto("你们已经是好友了不能添加",17006);
        }
//        sendFriendRequestVo.setContent(StringUtils.isEmpty(sendFriendRequestVo.getContent())?"我是"+sendFriendRequestVo.getUserId():sendFriendRequestVo.getContent());
        //发送添加信息
        ResponseResult result;
        try {
            result=rongCloudMethodUtil.sendSystemMessage(sendFriendRequestVo.getUserId(), sendFriendRequestVo.getFriendId(), sendFriendRequestVo.getContent(), "","","");
            if (result.getCode()!=200){
                return DtoUtil.getFalseDto("发送请求失败",17002);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("出现错误了",233);
        }
        return DtoUtil.getSuccessDto("发送成功",100000);
    }

    /**
     * 发送接受好友请求
     * @param sendFriendResponseVo
     * @param token
     * @return
     */
    @Override
    public Dto sendFriendResponse(SendFriendResponseVo sendFriendResponseVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        System.out.println("接受请求"+sendFriendResponseVo.toString());
        if (StringUtils.isEmpty(sendFriendResponseVo.getUserId())|| StringUtils.isEmpty(sendFriendResponseVo.getFriendId())){
            return DtoUtil.getFalseDto("userId和friendId不能为空",17001);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(sendFriendResponseVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        //更新双方的status
        //给请求者 发一条消息说，我同意你的请求了
        Account user = accountMapper.queryAccount(sendFriendResponseVo.getUserId());
        Account friend = accountMapper.queryAccount(sendFriendResponseVo.getFriendId());
        String extra = "{sourceUserNickname:"+ user.getUserName() + ",version:123456}";
        try {
            rongCloudMethodUtil.sendSystemMessage(sendFriendResponseVo.getUserId(),sendFriendResponseVo.getFriendId(),"我是"+user.getUserName()+"，我已经同意你的好友请求了","","",extra);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ResponseResult result1;
        ResponseResult result2;
        ResponseResult result3;
        try {
            //小灰条通知
            result1=rongCloudMethodUtil.sendPrivateMsg(sendFriendResponseVo.getUserId(),sendFriendResponseVo.getFriendId(),new InfoNtfMessage("你已添加了"+ friend.getUserName() +"，现在可以开始聊天了。",""));
            if (result1.getCode()!=200){
                return DtoUtil.getFalseDto("发送小灰条通知失败",17003);
            }
            //发送文本消息
            result2=rongCloudMethodUtil.sendPrivateMsg(sendFriendResponseVo.getUserId(),sendFriendResponseVo.getFriendId(),new TxtMessage("我通过了你的朋友验证请求，现在我们可以开始聊天了",""));
            if (result2.getCode()!=200){
                return DtoUtil.getFalseDto("发送文本result2消息失败",17004);
            }
            result3=rongCloudMethodUtil.sendPrivateMsg(sendFriendResponseVo.getFriendId(),sendFriendResponseVo.getUserId(),new TxtMessage(friend.getUserName(),""));
            if (result3.getCode()!=200){
                return DtoUtil.getFalseDto("发送文本result3消息失败",17005);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("出现错误了",233);
        }
        //建立好友关系
        int i=accountMapper.buildFriendship(sendFriendResponseVo.getUserId(),sendFriendResponseVo.getFriendId());
        int j=accountMapper.buildFriendship(sendFriendResponseVo.getFriendId(),sendFriendResponseVo.getUserId());
        if (i<=0||j<=0){
            return DtoUtil.getFalseDto("添加好友失败",16003);
        }
        //移除黑名单

        return DtoUtil.getSuccessDto("添加好友成功",100000);
    }



    /**
     * 查询好友列表
     * @param userIdVo
     * @param token
     * @return
     */
    @Override
    public Dto queryFriendList(UserIdVo userIdVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(userIdVo)){
            return DtoUtil.getFalseDto("查询好友数据未获取到",16004);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(userIdVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        int pageSize=Integer.parseInt(userIdVo.getPageSize());
        int pageIndex=Integer.parseInt(userIdVo.getPageNumber())*pageSize;
        List<Account> accountList=accountMapper.queryFriendList(userIdVo.getUserId(),String.valueOf(pageIndex),userIdVo.getPageSize());
        Map map=new HashMap();
        List<Map> maps=new ArrayList<>();
        for (Account account:accountList) {
            map.put("friendId",account.getId());
            map.put("userCode",account.getUserCode());
            map.put("userName",account.getUserName());
            map.put("headImgUrl",account.getHeadImgUrl());
            map.put("gender",account.getGender());
            maps.add(map);
        }
        if (ObjectUtils.isEmpty(accountList)){
            return DtoUtil.getFalseDto("查询好友列表失败",200000);
        }
        return DtoUtil.getSuccesWithDataDto("查询好友列表成功",maps,100000);
    }

    /**
     * 修改好友权限
     * @param jurisdictionVo
     * @param token
     * @return
     */
    @Override
    public Dto updateFriendJurisdiction(UpdateFriendJurisdictionVo jurisdictionVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(jurisdictionVo)){
            return DtoUtil.getFalseDto("修改好友数据未获取到",16005);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(jurisdictionVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        if (accountMapper.updateFriendJurisdiction(jurisdictionVo)<=0){
            return DtoUtil.getFalseDto("修改好友权限失败",16006);
        }
        return DtoUtil.getFalseDto("修改好友权限成功",100000);
    }

    /**
     * 解除好友关系
     * @param deleteFriendshipVo
     * @param token
     * @return
     */
    @Override
    public Dto deleteFriendship(DeleteFriendshipVo deleteFriendshipVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(deleteFriendshipVo)){
            return DtoUtil.getFalseDto("删除好友数据未获取到",16007);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(deleteFriendshipVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        //判断这俩人是不是好友
        if (accountMapper.queryFriendRel(deleteFriendshipVo.getUserId(),deleteFriendshipVo.getFriendId())<=0){
            return DtoUtil.getFalseDto("你们不是好友不能执行相关操作",16009);
        }
        //加入到融云的黑名单
        try {
            rongCloudMethodUtil.addBlackList(deleteFriendshipVo.getUserId(),deleteFriendshipVo.getFriendId());
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("拉黑操作出错",233);
        }
        //双向删除
        int i=accountMapper.deleteFriendship(deleteFriendshipVo);
        String temp=deleteFriendshipVo.getUserId();
        deleteFriendshipVo.setUserId(deleteFriendshipVo.getFriendId());
        deleteFriendshipVo.setFriendId(temp);
        int j=accountMapper.deleteFriendship(deleteFriendshipVo);
        if (i<=0||j<=0){
            return DtoUtil.getFalseDto("删除好友失败",16008);
        }
        return DtoUtil.getSuccessDto("删除好友成功",100000);
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
        account.setUserPassword(null);
        return DtoUtil.getSuccesWithDataDto("查询用户信息成功",account,100000);
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
        account.setUserSign(accountVo.getUserSign());
        int result=accountMapper.updateAccount(account);
        if (result<=0){
            return DtoUtil.getFalseDto("用户信息修改失败",13002);
        }
        return DtoUtil.getSuccessDto("用户信息修改成功",100000);
    }
}
