package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.AccountService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.dto.MyDetail;
import com.modcreater.tmbeans.pojo.Account;
import com.modcreater.tmbeans.pojo.Friendship;
import com.modcreater.tmbeans.pojo.SystemMsgRecord;
import com.modcreater.tmbeans.pojo.UserStatistics;
import com.modcreater.tmbeans.show.userinfo.ShowUserInfo;
import com.modcreater.tmbeans.vo.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmbeans.vo.uservo.*;
import com.modcreater.tmdao.mapper.*;
import com.modcreater.tmutils.*;
import io.rong.messages.ContactNtfMessage;
import io.rong.messages.InfoNtfMessage;
import io.rong.messages.TxtMessage;
import io.rong.models.response.ResponseResult;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
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
    @Resource
    private UserServiceMapper userServiceMapper;
    @Resource
    private SystemMsgMapper systemMsgMapper;

    RongCloudMethodUtil rongCloudMethodUtil =new RongCloudMethodUtil();
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
        if (StringUtils.isEmpty(loginVo.getUserCode())){
            return DtoUtil.getFalseDto("账号不能为空",14009);
        }
        System.out.println("登录"+loginVo.toString());
        String token=null;
        Map map=new HashMap();
        Account account=new Account();
        Account result=accountMapper.checkCode(loginVo.getUserCode());
        //如果已经注册
        if (!ObjectUtils.isEmpty(result)){
            if (StringUtils.isEmpty(result.getUserPassword())){
                map.put("id",result.getId());
                map.put("userCode",result.getUserCode());
                map.put("isFirst",result.getIsFirst());
                map.put("userName",result.getUserName());
                map.put("gender",result.getGender());
                map.put("birthday",result.getBirthday());
                map.put("headImgUrl",result.getHeadImgUrl());

               /* map.put("IDCard",result.getIDCard());
                map.put("userType",result.getUserType());
                map.put("realName",result.getRealName());
                map.put("userAddress",result.getUserAddress());*/
                map.put("userSign",result.getUserSign());
                return DtoUtil.getSuccesWithDataDto("注册成功，但是没有设置密码",map,100000);
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
            System.out.println("这是我要的token*****************************>"+token);
            //把token保存在数据库
            account.setId(result.getId());
            account.setToken(token);
            //把token保存在redis
            stringRedisTemplate.opsForValue().set(result.getId().toString(),token);
            if (accountMapper.updateAccount(account)<=0){
                return DtoUtil.getFalseDto("生成token失败",14006);
            }
            map.put("id",result.getId());
            map.put("userCode",result.getUserCode());
            map.put("isFirst",result.getIsFirst());
            map.put("userName",result.getUserName());
            map.put("gender",result.getGender());
            map.put("birthday",result.getBirthday());
            map.put("headImgUrl",result.getHeadImgUrl());
            /*map.put("IDCard",result.getIDCard());
            map.put("userType",result.getUserType());
            map.put("realName",result.getRealName());
            map.put("userAddress",result.getUserAddress());*/
            map.put("userSign",result.getUserSign());
            map.put("token",token);
            //查询用户是否开启了勿扰模式
            map.put("dnd",userSettingsMapper.getDND(result.getId().toString()));
            return DtoUtil.getSuccesWithDataDto("登录成功",map,100000);
        }else {
            //未注册时
            account.setUserCode(loginVo.getUserCode());
            account.setUserType(loginVo.getUserType());
            account.setUserName(loginVo.getUserCode());
            account.setGender(0L);
            account.setBirthday(DateUtil.dateToStamp(new Date()));
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
            map.put("id",result.getId());
            map.put("userCode",result.getUserCode());
            map.put("isFirst",result.getIsFirst());
            map.put("userName",result.getUserName());
            map.put("gender",result.getGender());
            map.put("birthday",result.getBirthday());
            map.put("headImgUrl",result.getHeadImgUrl());
            /*map.put("IDCard",result.getIDCard());
            map.put("userType",result.getUserType());
            map.put("realName",result.getRealName());
            map.put("userAddress",result.getUserAddress());*/
            map.put("userSign",result.getUserSign());
            return DtoUtil.getSuccesWithDataDto("注册成功，但是没有设置密码",map,100000);
        }
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
        //添加密码
        if (accountMapper.updateAccount(account)<=0){
            return DtoUtil.getFalseDto("添加密码失败",15003);
        }

        account=accountMapper.queryAccount(addPwdVo.getUserId());
        Map map=new HashMap();
        map.put("id",account.getId());
        map.put("userCode",account.getUserCode());
        map.put("isFirst",account.getIsFirst());
        map.put("userName",account.getUserName());
        map.put("gender",account.getGender());
        map.put("birthday",account.getBirthday());
        map.put("headImgUrl",account.getHeadImgUrl());
        map.put("token",account.getToken());
        try {
            if (ObjectUtils.isEmpty(achievementMapper.queryUserStatistics(addPwdVo.getUserId()))){
                if (achievementMapper.addNewUserStatistics(addPwdVo.getUserId()) == 0){
                    return DtoUtil.getFalseDto("为用户添加计数表失败",15005);
                }
            }
            if (ObjectUtils.isEmpty(userSettingsMapper.isUserSettingsExists(addPwdVo.getUserId()))){
                if (userSettingsMapper.addNewUserSettings(addPwdVo.getUserId()) == 0){
                    return DtoUtil.getFalseDto("为用户添加设置失败",15006);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("请勿重复操作",15007);
        }
        return DtoUtil.getSuccesWithDataDto("添加密码成功",map,100000);
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
        Map<String,Object> map=new HashMap();
        Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String[] myDate=sdf.format(date).split("-");
        //好友表信息
        Account account=accountMapper.queryFriendByUserCode(queFridenVo.getUserCode());
        if (ObjectUtils.isEmpty(account)){
            return DtoUtil.getFalseDto("搜索好友失败",200000);
        }
        //日规划,月规划
        MyDetail result=accountMapper.queryPlanByDayAndMonth(account.getId().toString(),myDate[2],myDate[0],myDate[1]);
        //已完成
        UserStatistics userStatistics=achievementMapper.queryUserStatistics(account.getId().toString());
        if (ObjectUtils.isEmpty(userStatistics)|| ObjectUtils.isEmpty(result)){
            return DtoUtil.getFalseDto("好友其他信息失败",200000);
        }
        //判断这俩人是不是已经是好友
        int i=accountMapper.queryFriendRel(queFridenVo.getUserId(),account.getId().toString());
        int j=accountMapper.queryFriendRel(account.getId().toString(),queFridenVo.getUserId());
        if (i>0 && j>0){
            //已经是好友时
            //成就
            List<String> achievement=achievementMapper.searchAllAchievement(account.getId().toString());
            if (achievement.size()==0){
                return DtoUtil.getFalseDto("查询成就失败",200000);
            }
            map.put("userId",account.getId().toString());
            map.put("userCode",account.getUserCode());
            map.put("userName",account.getUserName());
            map.put("gender",account.getGender().toString());
            map.put("birthday",account.getBirthday());
            map.put("headImgUrl",account.getHeadImgUrl());
            map.put("userSign",account.getUserSign());
            map.put("dayPlan",result.getDay());
            map.put("monthPlan",result.getMonth());
            map.put("finish",userStatistics.getCompleted().toString());
            map.put("achievement",achievement);
            map.put("isFriend",1);
            return DtoUtil.getSuccesWithDataDto("搜索好友成功",map,100000);
        }
        //其他表信息
        //日规划
        //月规划
        //已完成
        map.put("userId",account.getId().toString());
        map.put("userCode",account.getUserCode());
        map.put("userName",account.getUserName());
        map.put("gender",account.getGender().toString());
        map.put("headImgUrl",account.getHeadImgUrl());
        map.put("userSign",account.getUserSign());
        map.put("dayPlan",result.getDay());
        map.put("monthPlan",result.getMonth());
        map.put("finish",userStatistics.getCompleted().toString());
        map.put("isFriend",0);
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
        Friendship friendship=accountMapper.queryFriendshipDetail(sendFriendRequestVo.getUserId(),sendFriendRequestVo.getFriendId());
        Friendship friendship1=accountMapper.queryFriendshipDetail(sendFriendRequestVo.getFriendId(),sendFriendRequestVo.getUserId());
        System.out.println("是不是好友   "+friendship);
        System.out.println("是不是好友  "+friendship1);
        //判断你是不是自己加自己
        if (sendFriendRequestVo.getUserId().equals(sendFriendRequestVo.getFriendId())){
            return DtoUtil.getFalseDto("不能自己加自己",17007);
        }
        //不是第一次添加
        if (!ObjectUtils.isEmpty(friendship)|| !ObjectUtils.isEmpty(friendship1)){
            //这时不能添加
            if (friendship.getStatus()==20 && friendship1.getStatus()==20){
                return DtoUtil.getFalseDto("你们已经是好友了不能添加",17006);
            }
            //修改好友状态
            int i=accountMapper.updateFriendship(sendFriendRequestVo.getUserId(),sendFriendRequestVo.getFriendId(),"10");
            int j=accountMapper.updateFriendship(sendFriendRequestVo.getFriendId(),sendFriendRequestVo.getUserId(),"11");
            if (i<=0||j<=0){
                //回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return DtoUtil.getFalseDto("添加好友失败",16003);
            }
            //发送添加信息
            ResponseResult result;
            Map<String,String> map=new HashMap<>();
            try {
                sendFriendRequestVo.setContent(StringUtils.isEmpty(sendFriendRequestVo.getContent())?"我是"+sendFriendRequestVo.getUserId():sendFriendRequestVo.getContent());
                String[] friendId={sendFriendRequestVo.getFriendId()};
                //发送消息未读条数
                List<SystemMsgRecord> systemMsgRecordList=systemMsgMapper.queryAllUnreadMsg(sendFriendRequestVo.getFriendId(),"0","");
                Integer count;
                if (ObjectUtils.isEmpty(systemMsgRecordList)){
                    count=1;
                }else {
                    count=systemMsgRecordList.size()+1;
                }
                ContactNtfMessage contactNtfMessage=new ContactNtfMessage("1",count.toString(),sendFriendRequestVo.getUserId(),sendFriendRequestVo.getFriendId(),sendFriendRequestVo.getContent());
                result=rongCloudMethodUtil.sendSystemMessage(sendFriendRequestVo.getUserId(),friendId, contactNtfMessage, "","");
                if (result.getCode()!=200){
                    return DtoUtil.getFalseDto("发送请求失败",17002);
                }
                //消息保存在服务器
                map.put("userId",sendFriendRequestVo.getFriendId());
                map.put("msgContent",contactNtfMessage.getMessage());
                map.put("msgType","newFriend");
                map.put("fromId",sendFriendRequestVo.getUserId());
                SystemMsgRecord systemMsgRecord=new SystemMsgRecord();
                systemMsgRecord.setUserId(Long.parseLong(sendFriendRequestVo.getFriendId()));
                systemMsgRecord.setFromId(Long.parseLong(sendFriendRequestVo.getUserId()));
                systemMsgRecord.setMsgType("newFriend");
                if (systemMsgMapper.queryMsgByUserIdFriendIdMsgType(systemMsgRecord)==0){
                    //第一次发送
                    systemMsgMapper.addNewMsg(map);
                }else {
                    //多次发送
                    systemMsgMapper.updateUnreadMsg(sendFriendRequestVo.getFriendId(),sendFriendRequestVo.getUserId(),"0",sendFriendRequestVo.getContent());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return DtoUtil.getFalseDto("出现错误了",233);
            }
        }else {
            //建立好友关系(10:请求  11：被请求)
            int i=accountMapper.buildFriendship(sendFriendRequestVo.getUserId(),sendFriendRequestVo.getFriendId(),"10");
            int j=accountMapper.buildFriendship(sendFriendRequestVo.getFriendId(),sendFriendRequestVo.getUserId(),"11");
            if (i<=0||j<=0){
                //回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return DtoUtil.getFalseDto("添加好友失败",16003);
            }
            //发送添加信息
            ResponseResult result;
            Map<String,String> map=new HashMap<>();
            try {
                sendFriendRequestVo.setContent(StringUtils.isEmpty(sendFriendRequestVo.getContent())?"我是"+sendFriendRequestVo.getUserId():sendFriendRequestVo.getContent());
                String[] friendId={sendFriendRequestVo.getFriendId()};
                //发送消息未读条数
                List<SystemMsgRecord> systemMsgRecordList=systemMsgMapper.queryAllUnreadMsg(sendFriendRequestVo.getFriendId(),"0","");
                Integer count;
                if (ObjectUtils.isEmpty(systemMsgRecordList)){
                    count=1;
                }else {
                    count=systemMsgRecordList.size()+1;
                }
                ContactNtfMessage contactNtfMessage=new ContactNtfMessage("1",count.toString(),sendFriendRequestVo.getUserId(),sendFriendRequestVo.getFriendId(),sendFriendRequestVo.getContent());
                result=rongCloudMethodUtil.sendSystemMessage(sendFriendRequestVo.getUserId(),friendId, contactNtfMessage, "","");
                if (result.getCode()!=200){
                    return DtoUtil.getFalseDto("发送请求失败",17002);
                }
                //消息保存在服务器
                map.put("userId",sendFriendRequestVo.getFriendId());
                map.put("msgContent",contactNtfMessage.getMessage());
                map.put("msgType","newFriend");
                map.put("fromId",sendFriendRequestVo.getUserId());
                SystemMsgRecord systemMsgRecord=new SystemMsgRecord();
                systemMsgRecord.setUserId(Long.parseLong(sendFriendRequestVo.getFriendId()));
                systemMsgRecord.setFromId(Long.parseLong(sendFriendRequestVo.getUserId()));
                systemMsgRecord.setMsgType("newFriend");
                if (systemMsgMapper.queryMsgByUserIdFriendIdMsgType(systemMsgRecord)==0){
                    //第一次发送
                    systemMsgMapper.addNewMsg(map);
                }else {
                    //多次发送
                    systemMsgMapper.updateUnreadMsg(sendFriendRequestVo.getFriendId(),sendFriendRequestVo.getUserId(),"0",sendFriendRequestVo.getContent());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return DtoUtil.getFalseDto("出现错误了",233);
            }
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
    public Dto sendFriendResponse(FriendshipVo sendFriendResponseVo, String token) {
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
        //修改好友关系
        int i=accountMapper.updateFriendship(sendFriendResponseVo.getUserId(),sendFriendResponseVo.getFriendId(),"20");
        int j=accountMapper.updateFriendship(sendFriendResponseVo.getFriendId(),sendFriendResponseVo.getUserId(),"20");
        if (i<=0||j<=0){
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getFalseDto("添加好友失败",16003);
        }
        //给请求者 发一条消息说，我同意你的请求了
        Account user = accountMapper.queryAccount(sendFriendResponseVo.getUserId());
        Account friend = accountMapper.queryAccount(sendFriendResponseVo.getFriendId());
        String extra = "";
        try {
            String[] friendId={sendFriendResponseVo.getFriendId()};
            ContactNtfMessage contactNtfMessage=new ContactNtfMessage("2",extra,sendFriendResponseVo.getUserId(), sendFriendResponseVo.getFriendId(), "我是"+user.getUserName()+"，我已经同意你的好友请求了");
            ResponseResult result=rongCloudMethodUtil.sendSystemMessage(sendFriendResponseVo.getUserId(),friendId,contactNtfMessage,"","");
            if (result.getCode()!=200){
                return DtoUtil.getFalseDto("发送请求失败",17002);
            }
            //发送文本消息
            ResponseResult result2=rongCloudMethodUtil.sendPrivateMsg(sendFriendResponseVo.getUserId(),new String[]{sendFriendResponseVo.getFriendId()},0,new TxtMessage("我通过了你的朋友验证请求，现在我们可以开始聊天了",""));
            if (result2.getCode()!=200){
                return DtoUtil.getFalseDto("发送文本result2消息失败",17004);
            }
            //移除黑名单
            rongCloudMethodUtil.removeBlackList(sendFriendResponseVo.getUserId(),sendFriendResponseVo.getFriendId());
            rongCloudMethodUtil.removeBlackList(sendFriendResponseVo.getFriendId(),sendFriendResponseVo.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("出现错误了",233);
        }
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
        if (StringUtils.isEmpty(userIdVo.getPageNumber())){
            userIdVo.setPageNumber("1");
        }
        int pageIndex=(Integer.parseInt(userIdVo.getPageNumber())-1)*pageSize;
        List<Account> accountList=accountMapper.queryFriendList(userIdVo.getUserId(),pageIndex,pageSize);
        System.out.println("查询好友列表时输出的为什么会失败==》"+accountList.toString());
        List<Map> maps=new ArrayList<>();
        for (Account account:accountList) {
            Map map=new HashMap();
            map.put("friendId",account.getId());
            map.put("userCode",account.getUserCode());
            map.put("userName",account.getUserName());
            map.put("headImgUrl",account.getHeadImgUrl());
            map.put("gender",account.getGender());
            map.put("userSign",account.getUserSign());
            maps.add(map);
        }
        if (ObjectUtils.isEmpty(accountList)){
            return DtoUtil.getFalseDto("查询好友列表失败",200000);
        }
        return DtoUtil.getSuccesWithDataDto("查询好友列表成功",maps,100000);
    }

    /**
     * 查看好友详情
     * @param queFridenVo
     * @param token
     * @return
     */
    @Override
    public Dto queryFriendDetails(FriendshipVo queFridenVo, String token) {
        System.out.println("{}{}{{{}++"+queFridenVo.toString());
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(queFridenVo)){
            return DtoUtil.getFalseDto("查询好友数据未获取到",16004);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(queFridenVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        Map<String,Object> map=new HashMap();
        Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String[] myDate=sdf.format(date).split("-");
        for (int i = 0; i < myDate.length; i++) {
            System.out.println("今天的日期"+myDate[i]);
        }
        //好友表信息
        Account account=accountMapper.queryAccount(queFridenVo.getFriendId());
        if (ObjectUtils.isEmpty(account)){
            return DtoUtil.getFalseDto("查询好友详情失败",200000);
        }
        //日规划,月规划
        MyDetail result=accountMapper.queryPlanByDayAndMonth(account.getId().toString(),myDate[2],myDate[0],myDate[1]);
        //已完成
        UserStatistics userStatistics=achievementMapper.queryUserStatistics(account.getId().toString());
        if (ObjectUtils.isEmpty(userStatistics)|| ObjectUtils.isEmpty(result)){
            return DtoUtil.getFalseDto("好友其他信息失败",200000);
        }
        //成就
        List<String> achievement=achievementMapper.searchAllAchievement(account.getId().toString());
        /*if (achievement.size()==0){
            return DtoUtil.getFalseDto("查询成就失败",200000);
        }*/
        map.put("userId",account.getId().toString());
        map.put("userCode",account.getUserCode());
        map.put("userName",account.getUserName());
        map.put("gender",account.getGender().toString());
        map.put("birthday",account.getBirthday());
        map.put("headImgUrl",account.getHeadImgUrl());
        map.put("userSign",account.getUserSign());
        map.put("dayPlan",result.getDay());
        map.put("monthPlan",result.getMonth());
        map.put("finish",userStatistics.getCompleted().toString());
        map.put("achievement",achievement);
        return DtoUtil.getSuccesWithDataDto("查询好友详情成功",map,100000);
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
    public Dto deleteFriendship(FriendshipVo deleteFriendshipVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(deleteFriendshipVo)){
            return DtoUtil.getFalseDto("删除好友数据未获取到",16007);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(deleteFriendshipVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        try {
        //判断这俩人是不是好友
        int i=accountMapper.queryFriendRel(deleteFriendshipVo.getUserId(),deleteFriendshipVo.getFriendId());
        int j=accountMapper.queryFriendRel(deleteFriendshipVo.getFriendId(),deleteFriendshipVo.getUserId());
        if (i<=0||j<=0){
            return DtoUtil.getFalseDto("你们不是好友不能执行相关操作",16009);
        }
        //双向删除
        i=accountMapper.deleteFriendship(deleteFriendshipVo);
        String temp=deleteFriendshipVo.getUserId();
        deleteFriendshipVo.setUserId(deleteFriendshipVo.getFriendId());
        deleteFriendshipVo.setFriendId(temp);
        j=accountMapper.deleteFriendship(deleteFriendshipVo);
        if (i<=0||j<=0){
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getFalseDto("删除好友失败",16008);
        }
        //把添加好友消息删除
        boolean flag=false;
        if (systemMsgMapper.deleteSystemMsg(deleteFriendshipVo.getUserId(),deleteFriendshipVo.getFriendId())>0){
            flag=true;
        }else if (systemMsgMapper.deleteSystemMsg(deleteFriendshipVo.getFriendId(),deleteFriendshipVo.getUserId())>0){
            flag=true;
        }
        if (!flag){
            return DtoUtil.getFalseDto("添加好友消息记录删除失败",16010);
        }
        //加入到融云的黑名单
        rongCloudMethodUtil.addBlackList(deleteFriendshipVo.getUserId(),deleteFriendshipVo.getFriendId());
        rongCloudMethodUtil.addBlackList(deleteFriendshipVo.getFriendId(),deleteFriendshipVo.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("拉黑操作出错",233);
        }
        return DtoUtil.getSuccessDto("删除好友成功",100000);
    }

    /**
     * 查询所有好友消息
     * @param receivedId
     * @param token
     * @return
     */
    @Override
    public Dto queryAllUnreadMsg(ReceivedId receivedId, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(receivedId)){
            return DtoUtil.getFalseDto("查询所有未读消息数据未获取到",16007);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        Map<String,Object> map=new HashMap<>();

        Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String[] myDate=sdf.format(date).split("-");
        for (int i = 0; i < myDate.length; i++) {
            System.out.println("今天的日期"+myDate[i]);
        }

        //查询所有消息
        List<SystemMsgRecord> systemMsgRecordList=systemMsgMapper.queryAllUnreadMsg(receivedId.getUserId(),"-1","newFriend");
        if (systemMsgRecordList.size()==0){
            return DtoUtil.getFalseDto("没有好友请求消息",200000);
        }
        List list=new ArrayList();
        for (SystemMsgRecord systemMsgRecord:systemMsgRecordList) {
            System.out.println("我要的数据"+systemMsgRecord.toString());
            MsgVo msgVo=new MsgVo();
            //查询用户信息
            Account account=accountMapper.queryAccount(systemMsgRecord.getFromId().toString());
            //查询好友信息
            Friendship friendship=accountMapper.queryFriendshipDetail(systemMsgRecord.getUserId().toString(),systemMsgRecord.getFromId().toString());
            //日规划,月规划
            MyDetail result=accountMapper.queryPlanByDayAndMonth(account.getId().toString(),myDate[2],myDate[0],myDate[1]);
            //已完成
            UserStatistics userStatistics=achievementMapper.queryUserStatistics(account.getId().toString());
            if (ObjectUtils.isEmpty(userStatistics)|| ObjectUtils.isEmpty(result)){
                return DtoUtil.getFalseDto("好友其他信息失败",200000);
            }
            msgVo.setHeadImgUrl(account.getHeadImgUrl());
            msgVo.setUserName(account.getUserName());
            msgVo.setMsgContent(systemMsgRecord.getMsgContent());
            msgVo.setGender(account.getGender().toString());
            if (ObjectUtils.isEmpty(friendship)){
                //如果一个好友都没有
                msgVo.setStatus("0");
            }else {
                msgVo.setStatus(friendship.getStatus().toString());
            }
            msgVo.setFriendId(systemMsgRecord.getFromId().toString());
            msgVo.setDayPlan(result.getDay());
            msgVo.setMonthPlan(result.getMonth());
            msgVo.setFinish(userStatistics.getCompleted().toString());
            list.add(msgVo);
        }
        map.put("msgList",list);
        //修改消息状态
        systemMsgMapper.updateUnreadMsg(receivedId.getUserId(),"","1",null);
        return DtoUtil.getSuccesWithDataDto("好友请求消息获取成功",map,100000);
    }


    /**
     * 查询所有未读条数
     * @param receivedId
     * @param token
     * @return
     */
    @Override
    public Dto queryAllUnreadMsgCount(ReceivedId receivedId, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(receivedId)){
            return DtoUtil.getFalseDto("查询所有未读消息数据未获取到",16007);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        Map<String,Object> map=new HashMap<>();
        List<SystemMsgRecord> systemMsgRecordList=systemMsgMapper.queryAllUnreadMsg(receivedId.getUserId(),"0","");
        if (systemMsgRecordList.size()==0){
            return DtoUtil.getFalseDto("没有未读消息",200000);
        }
        map.put("count",systemMsgRecordList.size());
        return DtoUtil.getSuccesWithDataDto("未读消息条数获取成功",map,100000);
    }

    /**
     * 上传头像
     * @param headImgVo
     * @param token
     * @return
     */
    @Override
    public Dto uplHeadImg(HeadImgVo headImgVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(headImgVo)){
            return DtoUtil.getFalseDto("上传数据未获取到",21013);
        }
        if (StringUtils.isEmpty(headImgVo.getUserId())){
            return DtoUtil.getFalseDto("userId未获取到",21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(headImgVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        try {
            //生成token
            Account account=accountMapper.queryAccount(headImgVo.getUserId());
            token= rongCloudMethodUtil.createToken(headImgVo.getUserId(),account.getUserName(),account.getHeadImgUrl());
            if (StringUtils.isEmpty(token)){
                return DtoUtil.getFalseDto("生成token失败",14005);
            }
            System.out.println("这是我要的token*****************************>"+token);
            //把token保存在数据库
            account.setId(account.getId());
            account.setToken(token);
            account.setHeadImgUrl(headImgVo.getHeadImgUrl());
            //把token保存在redis
            stringRedisTemplate.opsForValue().set(account.getId().toString(),token);
            if (accountMapper.updateAccount(account)<=0){
                return DtoUtil.getFalseDto("上传头像失败",14006);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.getFalseDto("生成token失败",14005);
        }
        return DtoUtil.getSuccessDto("头像上传成功",100000);
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
        ShowUserInfo showUserInfo = new ShowUserInfo();
        showUserInfo.setDND(userSettingsMapper.getDND(queryUserVo.getId()));
        try {
            FatherToChild.change(account,showUserInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DtoUtil.getSuccesWithDataDto("查询用户信息成功",showUserInfo,100000);
    }

    @Override
    public Dto updateAccount(UpdAccountInfo updAccountInfo,String token) {
        if (ObjectUtils.isEmpty(updAccountInfo)){
            return DtoUtil.getFalseDto("用户信息接收失败",13001);
        }
        if (StringUtils.isEmpty(updAccountInfo.getUserId())){
            return DtoUtil.getFalseDto("请先登录",21011);
        }
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        String redisToken=stringRedisTemplate.opsForValue().get(updAccountInfo.getUserId());
        if (!token.equals(redisToken)){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        Account account=new Account();
        account.setId(Long.parseLong(updAccountInfo.getUserId()));
        account.setUserName(updAccountInfo.getUserName());
        account.setUserSign(updAccountInfo.getUserSign());
        account.setGender(Long.parseLong(updAccountInfo.getGender()));
        int result=accountMapper.updateAccount(account);
        if (result<=0){
            return DtoUtil.getFalseDto("用户信息修改失败",13002);
        }
        return DtoUtil.getSuccessDto("用户信息修改成功",100000);
    }
}
