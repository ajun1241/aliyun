package com.modcreater.tmauth.service.impl;

import com.modcreater.tmauth.service.AccountService;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.dto.MyDetail;
import com.modcreater.tmbeans.pojo.*;
import com.modcreater.tmbeans.show.userinfo.ShowUserInfo;
import com.modcreater.tmbeans.vo.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmbeans.vo.uservo.*;
import com.modcreater.tmdao.mapper.*;
import com.modcreater.tmutils.*;
import com.modcreater.tmutils.messageutil.FriendCardMessage;
import com.modcreater.tmutils.messageutil.UpdPortraitMessage;
import com.modcreater.tmutils.messageutil.VerifyFriendMsg;
import io.rong.messages.BaseMessage;
import io.rong.messages.ContactNtfMessage;
import io.rong.messages.TxtMessage;
import io.rong.models.response.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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



    private static final String SYSTEMID = "100000";
    private static final String ANDROID = "android";
    private static final String IOS = "ios";

    @Resource
    private AchievementMapper achievementMapper;
    @Resource
    private UserSettingsMapper userSettingsMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private EventMapper eventMapper;
    @Resource
    private BackerMapper backerMapper;
    @Resource
    private SystemMsgMapper systemMsgMapper;
    @Resource
    private AppTypeMapper appTypeMapper;

    RongCloudMethodUtil rongCloudMethodUtil =new RongCloudMethodUtil();

    private Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
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
        String code=loginVo.getUserCode();
        String pattern = "^1[\\d]{10}";
        if (!Pattern.matches(pattern,code)){
            return DtoUtil.getFalseDto("账号格式不正确",14010);
        }
        Account result=accountMapper.checkCode(code);
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
                map.put("dnd","1");
                map.put("userSign",result.getUserSign());
                return DtoUtil.getSuccesWithDataDto("注册成功，但是没有设置密码",map,100000);
            }
            if (StringUtils.isEmpty(result.getHeadImgUrl())){
                result.setHeadImgUrl("233");
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
            //更换客户端
            int i=updateAppType(result.getId().toString(), loginVo.getAppType(),loginVo.getDeviceToken());
            /*if (i==1){
                return DtoUtil.getFalseDto("客户端类型不能为空",14021);
            }else if (i==2){
                return DtoUtil.getFalseDto("客户端类型格式不正确",14022);
            }else if (i==4){
                return DtoUtil.getFalseDto("客户端类型格式不正确",14022);
            }*/
            map.put("id",result.getId());
            map.put("userCode",result.getUserCode());
            map.put("isFirst",result.getIsFirst());
            map.put("userName",result.getUserName());
            map.put("gender",result.getGender());
            map.put("birthday",result.getBirthday());
            map.put("headImgUrl",result.getHeadImgUrl());
            map.put("userSign",result.getUserSign());
            map.put("token",token);
            //查询用户是否开启了勿扰模式
            map.put("dnd",userSettingsMapper.getDND(result.getId().toString())!=null?userSettingsMapper.getDND(result.getId().toString()).toString():"");
            return DtoUtil.getSuccesWithDataDto("登录成功",map,100000);
        }else {
            //未注册时
            account.setUserCode(loginVo.getUserCode());
            account.setUserType(loginVo.getUserType());
            Random random=new Random();
            int i=random.nextInt(1000000);
            String name="智袖新用户_"+i;
            account.setUserName(name);
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
            map.put("dnd","1");
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
        //更换客户端
        int i=updateAppType(addPwdVo.getUserId(), addPwdVo.getAppType(),addPwdVo.getDeviceToken());
        /*if (i==1){
            return DtoUtil.getFalseDto("客户端类型不能为空",14021);
        }else if (i==2){
            return DtoUtil.getFalseDto("客户端类型格式不正确",14022);
        }else if (i==4){
            return DtoUtil.getFalseDto("客户端类型格式不正确",14022);
        }*/
        account=accountMapper.queryAccount(addPwdVo.getUserId());
        Map map=new HashMap();
        map.put("id",account.getId());
        map.put("userCode",account.getUserCode());
        map.put("isFirst",account.getIsFirst());
        map.put("userName",account.getUserName());
        map.put("gender",account.getGender());
        map.put("birthday",account.getBirthday());
        map.put("headImgUrl",account.getHeadImgUrl());
        map.put("dnd",userSettingsMapper.getDND(account.getId().toString()).toString());
        map.put("userSign",account.getUserSign());
        map.put("token",account.getToken());
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
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        Map<String,Object> map=new HashMap();
        Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String[] myDate=sdf.format(date).split("-");
        //好友表信息
        Account account=accountMapper.queryFriendByUserCode(queFridenVo.getUserCode());
        if (ObjectUtils.isEmpty(account)){
            return DtoUtil.getSuccesWithDataDto("搜索好友失败",null,200000);
        }
        //日规划,月规划
        MyDetail result=accountMapper.queryPlanByDayAndMonth(account.getId().toString(),myDate[2],myDate[0],myDate[1]);
        //已完成
        Long completedEvents = eventMapper.countCompletedEvents(account.getId());
        if (ObjectUtils.isEmpty(result)){
            return DtoUtil.getSuccesWithDataDto("好友其他信息失败",null,200000);
        }
        //判断这俩人是不是已经是好友
        int i=accountMapper.queryFriendRel(queFridenVo.getUserId(),account.getId().toString());
        int j=accountMapper.queryFriendRel(account.getId().toString(),queFridenVo.getUserId());
        if (i>0 && j>0){
            //已经是好友时
            //成就
            List<Achievement> achievement=achievementMapper.searchAllAchievement(account.getId().toString());
            //查询好友关系天数
            Friendship friendship=accountMapper.queryFriendshipDetail(queFridenVo.getUserId(),account.getId().toString());
            Date createDate=friendship.getCerateDate();
            String days=""+((System.currentTimeMillis()-createDate.getTime())/1000/3600/24 == 0 ? 1 : (System.currentTimeMillis()-createDate.getTime())/1000/3600/24);
            map.put("friendshipDays",days);
            map.put("userId",account.getId().toString());
            map.put("userCode",account.getUserCode());
            map.put("userName",account.getUserName());
            map.put("gender",account.getGender().toString());
            map.put("birthday",account.getBirthday());
            map.put("headImgUrl",account.getHeadImgUrl());
            map.put("userSign",account.getUserSign());
            map.put("dayPlan",result.getDay());
            map.put("monthPlan",result.getMonth());
            map.put("finish",completedEvents.toString());
            map.put("achievement",achievement);
            map.put("isFriend",1);
            return DtoUtil.getSuccesWithDataDto("搜索成功",map,100000);
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
        map.put("finish",completedEvents.toString());
        map.put("isFriend",0);
        return DtoUtil.getSuccesWithDataDto("搜索成功",map,100000);
    }

    /**
     * 发送添加好友请求
     * @param sendFriendRequestVo
     * @param token
     * @return
     */
    @Override
    public synchronized Dto sendFriendRequest(SendFriendRequestVo sendFriendRequestVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        System.out.println("添加请求"+sendFriendRequestVo.toString());
        if (StringUtils.isEmpty(sendFriendRequestVo.getUserId())|| StringUtils.isEmpty(sendFriendRequestVo.getFriendId())){
            return DtoUtil.getFalseDto("userId和friendId不能为空",17001);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(sendFriendRequestVo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
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
        if (!ObjectUtils.isEmpty(friendship) || !ObjectUtils.isEmpty(friendship1)){
            //判断有没有被对方拉黑
            if (friendship1.getFlag()==1L){
                return DtoUtil.getFalseDto("发送失败，对方可能对你设置了权限",10220);
            }
            //这时不能添加
            if (friendship.getStatus()==20L && friendship1.getStatus()==20L){
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
            //权限设置
            UpdateFriendJurisdictionVo updateFriendJurisdictionVo=new UpdateFriendJurisdictionVo();
            updateFriendJurisdictionVo.setUserId(sendFriendRequestVo.getUserId());
            updateFriendJurisdictionVo.setFriendId(sendFriendRequestVo.getFriendId());
            updateFriendJurisdictionVo.setHide(sendFriendRequestVo.getHide());
            updateFriendJurisdictionVo.setDiary(sendFriendRequestVo.getDiary());
            accountMapper.updateFriendJurisdiction(updateFriendJurisdictionVo);
            //发送添加信息
            ResponseResult result;
            Map<String,String> map=new HashMap<>();
            try {
                Account account=accountMapper.queryAccount(sendFriendRequestVo.getUserId());
                sendFriendRequestVo.setContent(StringUtils.isEmpty(sendFriendRequestVo.getContent())?"我是"+account.getUserName():sendFriendRequestVo.getContent());
                System.out.println(sendFriendRequestVo.getContent());
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
                    result = rongCloudMethodUtil.sendSystemMessage(sendFriendRequestVo.getUserId(), friendId, contactNtfMessage, "", "");
                    if (result.getCode() != 200) {
                        logger.info("融云消息异常" + result.toString());
                        return DtoUtil.getFalseDto("发送请求失败", 17002);
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
            //权限设置
            UpdateFriendJurisdictionVo updateFriendJurisdictionVo=new UpdateFriendJurisdictionVo();
            updateFriendJurisdictionVo.setUserId(sendFriendRequestVo.getUserId());
            updateFriendJurisdictionVo.setFriendId(sendFriendRequestVo.getFriendId());
            updateFriendJurisdictionVo.setHide(sendFriendRequestVo.getHide());
            updateFriendJurisdictionVo.setDiary(sendFriendRequestVo.getDiary());
            accountMapper.updateFriendJurisdiction(updateFriendJurisdictionVo);
            //发送添加信息
            ResponseResult result;
            Map<String,String> map=new HashMap<>();
            try {
                Account account=accountMapper.queryAccount(sendFriendRequestVo.getUserId());
                sendFriendRequestVo.setContent(StringUtils.isEmpty(sendFriendRequestVo.getContent())?"我是"+account.getUserName():sendFriendRequestVo.getContent());
                String[] friendId={sendFriendRequestVo.getFriendId()};
                //发送消息未读条数
                List<SystemMsgRecord> systemMsgRecordList=systemMsgMapper.queryAllUnreadMsg(sendFriendRequestVo.getFriendId(),"0","");
                Integer count;
                if (ObjectUtils.isEmpty(systemMsgRecordList)){
                    count=1;
                }else {
                    count=systemMsgRecordList.size()+1;
                }
                ContactNtfMessage contactNtfMessage = new ContactNtfMessage("1", count.toString(), sendFriendRequestVo.getUserId(), sendFriendRequestVo.getFriendId(), sendFriendRequestVo.getContent());

                    result = rongCloudMethodUtil.sendSystemMessage(sendFriendRequestVo.getUserId(), friendId, contactNtfMessage, "", "");
                    if (result.getCode() != 200) {
                        logger.info("融云消息异常" + result.toString());
                        return DtoUtil.getFalseDto("发送请求失败", 17002);
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
    public synchronized Dto sendFriendResponse(FriendshipVo sendFriendResponseVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        System.out.println("接受请求"+sendFriendResponseVo.toString());
        if (StringUtils.isEmpty(sendFriendResponseVo.getUserId())|| StringUtils.isEmpty(sendFriendResponseVo.getFriendId())){
            return DtoUtil.getFalseDto("userId和friendId不能为空",17001);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(sendFriendResponseVo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        //修改好友关系
        int i=accountMapper.updateFriendship(sendFriendResponseVo.getUserId(),sendFriendResponseVo.getFriendId(),"20");
        int j=accountMapper.updateFriendship(sendFriendResponseVo.getFriendId(),sendFriendResponseVo.getUserId(),"20");
        if (i<=0||j<=0){
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DtoUtil.getFalseDto("添加好友失败",16003);
        }
        //权限设置
        UpdateFriendJurisdictionVo updateFriendJurisdictionVo=new UpdateFriendJurisdictionVo();
        updateFriendJurisdictionVo.setUserId(sendFriendResponseVo.getUserId());
        updateFriendJurisdictionVo.setFriendId(sendFriendResponseVo.getFriendId());
        updateFriendJurisdictionVo.setHide(sendFriendResponseVo.getHide());
        updateFriendJurisdictionVo.setDiary(sendFriendResponseVo.getDiary());
        accountMapper.updateFriendJurisdiction(updateFriendJurisdictionVo);
        //给请求者 发一条消息说，我同意你的请求了
        Account user = accountMapper.queryAccount(sendFriendResponseVo.getUserId());
        Account friend = accountMapper.queryAccount(sendFriendResponseVo.getFriendId());
        String extra = "";
        try {
            String[] friendId={sendFriendResponseVo.getFriendId()};

                ContactNtfMessage contactNtfMessage=new ContactNtfMessage("2",extra,sendFriendResponseVo.getUserId(), sendFriendResponseVo.getFriendId(), "我是"+user.getUserName()+"，我已经同意你的好友请求了");
                ResponseResult result=rongCloudMethodUtil.sendSystemMessage(sendFriendResponseVo.getUserId(),friendId,contactNtfMessage,"","");
                if (result.getCode()!=200){
                    logger.info("融云消息异常"+result.toString());
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
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        int pageSize=Integer.parseInt(userIdVo.getPageSize());
        if (StringUtils.isEmpty(userIdVo.getPageNumber())){
            userIdVo.setPageNumber("1");
        }
        int pageIndex=(Integer.parseInt(userIdVo.getPageNumber())-1)*pageSize;
        List<Account> accountList=accountMapper.queryFriendList(userIdVo.getUserId(),pageIndex,pageSize);
        Backers backers=backerMapper.getRealMyBacker(userIdVo.getUserId());
        String backerId="";
        if (!StringUtils.isEmpty(backers)){
            backerId=backers.getBackerId();
        }
        Map<String,Object> result=new HashMap<>();
        result.put("backer",null);
        List<Map> maps=new ArrayList<>();
        for (Account account:accountList) {
            Map map=new HashMap();
            //找到支持者
            if (account.getId().toString().equals(backerId)){
                map.put("friendId",account.getId());
                map.put("userCode",account.getUserCode());
                map.put("userName",account.getUserName());
                map.put("headImgUrl",account.getHeadImgUrl());
                map.put("gender",account.getGender());
                map.put("userSign",account.getUserSign());
                result.put("backer",map);
            }else {
                map.put("friendId",account.getId());
                map.put("userCode",account.getUserCode());
                map.put("userName",account.getUserName());
                map.put("headImgUrl",account.getHeadImgUrl());
                map.put("gender",account.getGender());
                map.put("userSign",account.getUserSign());
                maps.add(map);
            }
        }
        result.put("friends",maps);
        /*if (ObjectUtils.isEmpty(accountList)){
            return DtoUtil.getSuccesWithDataDto("查询好友列表失败",null,200000);
        }*/
        return DtoUtil.getSuccesWithDataDto("查询好友列表成功",result,100000);
    }

    /**
     * 查看好友详情
     * @param queFridenVo
     * @param token
     * @return
     */
    @Override
    public Dto queryFriendDetails(FriendshipVo queFridenVo, String token) {
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(queFridenVo)){
            return DtoUtil.getFalseDto("查询好友数据未获取到",16004);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(queFridenVo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        Map<String,Object> map=new HashMap();
        Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String[] myDate=sdf.format(date).split("-");
        /*for (int i = 0; i < myDate.length; i++) {
            System.out.println("今天的日期"+myDate[i]);
        }*/
        //好友表信息
        Account account=accountMapper.queryAccount(queFridenVo.getFriendId());
        if (ObjectUtils.isEmpty(account)){
            return DtoUtil.getSuccesWithDataDto("查询好友详情失败",null,200000);
        }
        //日规划,月规划
        MyDetail result=accountMapper.queryPlanByDayAndMonth(account.getId().toString(),myDate[2],myDate[0],myDate[1]);
        //已完成
        Long completedEvents = eventMapper.countCompletedEvents(account.getId());
        //成就
        List<Achievement> achievement=achievementMapper.searchAllAchievement(account.getId().toString());
        String days="";
        //如果是小助手
        if (SYSTEMID.equals(queFridenVo.getFriendId())){
            Date createDate=account.getCreateDate();
            days=String.valueOf((System.currentTimeMillis()-createDate.getTime())/1000/3600/24 == 0 ? 1 : (System.currentTimeMillis()-createDate.getTime())/1000/3600/24);
        }else if(queFridenVo.getUserId().equals(queFridenVo.getFriendId())){
            //如果自己查看自己
            Date createDate=account.getCreateDate();
            days=String.valueOf((System.currentTimeMillis()-createDate.getTime())/1000/3600/24 == 0 ? 1 : (System.currentTimeMillis()-createDate.getTime())/1000/3600/24);
            map.put("friendshipDays",days);
            map.put("userId",account.getId().toString());
            map.put("userCode",account.getUserCode());
            map.put("userName",account.getUserName());
            map.put("gender",account.getGender().toString());
            map.put("birthday",account.getBirthday());
            map.put("headImgUrl",account.getHeadImgUrl());
            map.put("userSign",account.getUserSign());
            map.put("dayPlan",result.getDay());
            map.put("monthPlan",result.getMonth());
            map.put("finish",completedEvents.toString());
            map.put("achievement",achievement);
            return DtoUtil.getSuccesWithDataDto("查询成功",map,100000);
        }else {
            Friendship friendship=accountMapper.queryFriendshipDetail(queFridenVo.getUserId(),queFridenVo.getFriendId());
            if(ObjectUtils.isEmpty(friendship)){
                return DtoUtil.getFalseDto("你们可能已不是好友",81314);
            }
            Date createDate=friendship.getCerateDate();
            days=String.valueOf((System.currentTimeMillis()-createDate.getTime())/1000/3600/24 == 0 ? 1 : (System.currentTimeMillis()-createDate.getTime())/1000/3600/24);
        }
        //查询好友关系天数
        map.put("friendshipDays",days);
        map.put("userId",account.getId().toString());
        map.put("userCode",account.getUserCode());
        map.put("userName",account.getUserName());
        map.put("gender",account.getGender().toString());
        map.put("birthday",account.getBirthday());
        map.put("headImgUrl",account.getHeadImgUrl());
        map.put("userSign",account.getUserSign());
        map.put("dayPlan",result.getDay());
        map.put("monthPlan",result.getMonth());
        map.put("finish",completedEvents.toString());
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
            return DtoUtil.getFalseDto("请重新登录",21014);
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
            return DtoUtil.getFalseDto("请重新登录",21014);
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
        //删除支持者
        int a=-1;
        int b=-1;
        Backers backers1=backerMapper.getMyBacker(deleteFriendshipVo.getUserId());
        //如果该用户有支持者且支持者是该好友时
        if (!ObjectUtils.isEmpty(backers1) && deleteFriendshipVo.getFriendId().equals(backers1.getBackerId())){
            //删除该好友的支持者
            a=backerMapper.deleteBacker(backers1.getUserId());
            //如果该好友有支持者且支持者是该用户时
            Backers backers2=backerMapper.getMyBacker(deleteFriendshipVo.getFriendId());
            if (!ObjectUtils.isEmpty(backers2) && deleteFriendshipVo.getUserId().equals(backers2.getBackerId())){
                //删除该好友的支持者
                b=backerMapper.deleteBacker(backers2.getUserId());
            }
        }
        if (a==0 || b==0){
            return DtoUtil.getFalseDto("删除支持者失败",16017);
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
            return DtoUtil.getFalseDto("请重新登录",21014);
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
        /*if (systemMsgRecordList.size()==0){
            return DtoUtil.getSuccesWithDataDto("没有好友请求消息",null,200000);
        }*/
        List list=new ArrayList();
        for (SystemMsgRecord systemMsgRecord:systemMsgRecordList) {
            System.out.println("我要的数据" + systemMsgRecord.toString());
            MsgVo msgVo = new MsgVo();
            //查询用户信息
            Account account = accountMapper.queryAccount(systemMsgRecord.getFromId().toString());
            if (!ObjectUtils.isEmpty(account)) {
                //查询好友信息
                Friendship friendship = accountMapper.queryFriendshipDetail(systemMsgRecord.getUserId().toString(), systemMsgRecord.getFromId().toString());
                //日规划,月规划
                MyDetail result = accountMapper.queryPlanByDayAndMonth(account.getId().toString(), myDate[2], myDate[0], myDate[1]);
                //已完成
                Long completed = eventMapper.countCompletedEvents(systemMsgRecord.getFromId());
                if (ObjectUtils.isEmpty(result)) {
                    return DtoUtil.getSuccesWithDataDto("好友其他信息失败", null, 200000);
                }
                msgVo.setHeadImgUrl(account.getHeadImgUrl());
                msgVo.setUserName(account.getUserName());
                msgVo.setMsgContent(systemMsgRecord.getMsgContent());
                msgVo.setGender(account.getGender().toString());
                msgVo.setUserCode(account.getUserCode());
                if (ObjectUtils.isEmpty(friendship)) {
                    //如果一个好友都没有
                    msgVo.setStatus("0");
                } else {
                    msgVo.setStatus(friendship.getStatus().toString());
                }
                msgVo.setFriendId(systemMsgRecord.getFromId().toString());
                msgVo.setDayPlan(result.getDay());
                msgVo.setMonthPlan(result.getMonth());
                msgVo.setFinish(completed.toString());
                list.add(msgVo);
            }
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
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        Map<String,Object> map=new HashMap<>();
        List<SystemMsgRecord> systemMsgRecordList=systemMsgMapper.queryAllUnreadMsg(receivedId.getUserId(),"0","");
        if (systemMsgRecordList.size()==0){
            return DtoUtil.getSuccesWithDataDto("没有未读消息",null,200000);
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
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        Account account=accountMapper.queryAccount(headImgVo.getUserId());
        account.setHeadImgUrl(headImgVo.getHeadImgUrl());
        if (accountMapper.updateAccount(account)<=0){
            return DtoUtil.getFalseDto("上传头像失败",14006);
        }
        //给所有好友发送消息
        try {
            RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
            UpdPortraitMessage updPortraitMessage=new UpdPortraitMessage(account.getId().toString(),account.getUserName(),headImgVo.getHeadImgUrl());
            List<Long> friendsId=accountMapper.queryAllFriendList(account.getId().toString());
            List<String> list=new ArrayList<>();
            for (Long friendId:friendsId) {
                list.add(friendId.toString());
            }
            ResponseResult result=rongCloudMethodUtil.sendSystemMessage(headImgVo.getUserId(),list.toArray(new String[0]),updPortraitMessage,"","");
            if (result.getCode()!=200){
                logger.error("更换头像消息发送失败");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
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
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        Account account= accountMapper.queryAccount(queryUserVo.getId());
        if (ObjectUtils.isEmpty(account)){
            return DtoUtil.getSuccesWithDataDto("查询用户信息失败",null,200000);
        }
        account.setUserPassword(null);
        ShowUserInfo showUserInfo = new ShowUserInfo();
        showUserInfo.setDND(userSettingsMapper.getDND(queryUserVo.getId()).toString());
        try {
            FatherToChild.change(account,showUserInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DtoUtil.getSuccesWithDataDto("查询用户信息成功",showUserInfo,100000);
    }

    /**
     * 新查看用户详情(userId)
     * @param receivedId
     * @param token
     * @return
     */
    @Override
    public Dto newQueryAccount(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        Account account= accountMapper.queryAccount(receivedId.getUserId());
        if (ObjectUtils.isEmpty(account)){
            return DtoUtil.getSuccesWithDataDto("查询用户信息失败",null,200000);
        }
        account.setUserPassword(null);
        ShowUserInfo showUserInfo = new ShowUserInfo();
        showUserInfo.setDND(userSettingsMapper.getDND(receivedId.getUserId()).toString());
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
            return DtoUtil.getFalseDto("请重新登录",21014);
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

    /**
     * 查询会话列表好友信息（头像、昵称）
     * @param friendshipVo
     * @param token
     * @return
     */
    @Override
    public Dto querySessionListDetail(FriendshipVo friendshipVo,String token) {
        System.out.println(friendshipVo.toString());
        if (StringUtils.isEmpty(token)){
            return DtoUtil.getFalseDto("token未获取到",21013);
        }
        if (ObjectUtils.isEmpty(friendshipVo)){
            return DtoUtil.getFalseDto("请求数据未获取到",21013);
        }
        if (StringUtils.isEmpty(friendshipVo.getUserId())){
            return DtoUtil.getFalseDto("userId未获取到",21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(friendshipVo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        Account account=accountMapper.queryAccount(friendshipVo.getFriendId());
        if (ObjectUtils.isEmpty(account)){
            return DtoUtil.getSuccesWithDataDto("好友信息查询失败",null,200000);
        }else {
            Map<String,String> map=new HashMap<>();
            map.put("userId",account.getId().toString());
            map.put("userName",account.getUserName());
            map.put("headImgUrl",account.getHeadImgUrl());
            return DtoUtil.getSuccesWithDataDto("查询成功",map,100000);
        }
    }

    /**
     * 判断是否是好友
     * @param friendshipVo
     * @param token
     * @return
     */
    @Override
    public Dto judgeFriendship(FriendshipVo friendshipVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(friendshipVo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        //判断这俩人是不是已经是好友
        int i=accountMapper.queryFriendRel(friendshipVo.getUserId(),friendshipVo.getFriendId());
        int j=accountMapper.queryFriendRel(friendshipVo.getFriendId(),friendshipVo.getUserId());
        Map<String,String> map=new HashMap<>(1);
        map.put("friendshipStatus","0");
        if (i>0 && j>0){
            map.put("friendshipStatus","1");
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",map,100000);
    }

    /**
     * 发送验证好友消息
     * @param requestVo
     * @param token
     * @return
     */
    @Override
    public Dto sendVerifyFriendMsg(SendFriendRequestVo requestVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(requestVo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        try {
            String[] friendId={requestVo.getFriendId()};
            BaseMessage verifyFriendMsg=new VerifyFriendMsg(requestVo.getContent(),"1");
            ResponseResult result=rongCloudMethodUtil.sendSystemMessage(requestVo.getUserId(),friendId, verifyFriendMsg, "","");
            if (result.getCode()!=200){
                logger.info("融云消息异常"+result.toString());
                return DtoUtil.getFalseDto("发送请求失败",17002);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return DtoUtil.getFalseDto("发送失败",11996);
        }
        return DtoUtil.getSuccessDto("发送成功",100000);
    }

    /**
     * 发送好友名片
     * @param friendCardVo
     * @param token
     * @return
     */
    @Override
    public Dto sendFriendCard(FriendCardVo friendCardVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(friendCardVo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        try {
            Account account=accountMapper.queryAccount(friendCardVo.getTargetId());
            RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
            FriendCardMessage friendCardMessage=new FriendCardMessage(account.getHeadImgUrl(),account.getUserName(),account.getUserCode());
            ResponseResult result=rongCloudMethodUtil.sendPrivateMsg(friendCardVo.getUserId(),new String[]{friendCardVo.getFriendId()},1,friendCardMessage);
            if (result.getCode()!=200){
                return DtoUtil.getFalseDto("发送失败",17002);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return DtoUtil.getFalseDto("发送失败",17003);
        }
        return DtoUtil.getSuccessDto("发送成功",100000);
    }

    /**
     * 添加黑名单
     * @param friendshipVo
     * @param token
     * @return
     */
    @Override
    public Dto addBlackList(FriendshipVo friendshipVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(friendshipVo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        //判断两人好友关系
        Friendship friendship=accountMapper.queryFriendshipDetail(friendshipVo.getUserId(),friendshipVo.getFriendId());
        if (ObjectUtils.isEmpty(friendship)){
            accountMapper.buildFriendship(friendshipVo.getUserId(),friendshipVo.getFriendId(),"11");
            accountMapper.buildFriendship(friendshipVo.getFriendId(),friendshipVo.getUserId(),"10");
        }
        //加入融云的黑名单
        try {
            rongCloudMethodUtil.addBlackList(friendship.getUserId().toString(),friendship.getFriendId().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //修改权限
        UpdateFriendJurisdictionVo jurisdictionVo=new UpdateFriendJurisdictionVo();
        jurisdictionVo.setUserId(friendshipVo.getUserId());
        jurisdictionVo.setFriendId(friendshipVo.getFriendId());
        jurisdictionVo.setFlag("1");
        if (accountMapper.updateFriendJurisdiction(jurisdictionVo)<1){
            return DtoUtil.getFalseDto("加入黑名单失败",10291);
        }
        return DtoUtil.getSuccessDto("添加黑名单成功",100000);
    }

    /**
     * 查看黑名单列表
     * @param userIdVo
     * @param token
     * @return
     */
    @Override
    public Dto queryBlackList(UserIdVo userIdVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(userIdVo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        List<String> list=accountMapper.queryBlackList(userIdVo.getUserId());
        List<Map<String,String>> maps=new ArrayList<>();
        for (String userId:list) {
            Account account=accountMapper.queryAccount(userId);
            Map<String,String> map=new HashMap<>();
            map.put("userId",account.getId().toString());
            map.put("headImgUrl",account.getHeadImgUrl());
            map.put("userCode",account.getUserCode());
            map.put("userName",account.getUserName());
            map.put("gender",account.getGender().toString());
            maps.add(map);
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",maps,100000);
    }

    /**
     * 移出黑名单
     * @param friendshipVo
     * @param token
     * @return
     */
    @Override
    public Dto removeBlackList(FriendshipVo friendshipVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(friendshipVo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        //移出融云黑名单
        try {
            rongCloudMethodUtil.removeBlackList(friendshipVo.getUserId(),friendshipVo.getFriendId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //修改权限
        UpdateFriendJurisdictionVo jurisdictionVo=new UpdateFriendJurisdictionVo();
        jurisdictionVo.setUserId(friendshipVo.getUserId());
        jurisdictionVo.setFriendId(friendshipVo.getFriendId());
        jurisdictionVo.setFlag("0");
        if (accountMapper.updateFriendJurisdiction(jurisdictionVo)<1){
            return DtoUtil.getFalseDto("移出黑名单失败",10291);
        }
        return DtoUtil.getSuccessDto("移出黑名单成功",100000);
    }

    /**
     * 查询好友成就
     * @param userFriendVo
     * @param token
     * @return
     */
    @Override
    public Dto queryFriendAchievement(UserFriendVo userFriendVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(userFriendVo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        Map<String,Long> result = new HashMap<>(2);
        result.put("achievedNum",achievementMapper.getAchievedNum(userFriendVo.getFriendId()));
        result.put("totalNum",achievementMapper.getTotalNum(userFriendVo.getFriendId()));
        return DtoUtil.getSuccesWithDataDto("查询成功",result,100000);
    }

    /**
     * 账号密码登录
     * @param loginByCPVo
     * @return
     */
    @Override
    public Dto loginByCP(LoginByCPVo loginByCPVo) {
        if (StringUtils.isEmpty(loginByCPVo.getUserCode())){
            return DtoUtil.getFalseDto("账号不能为空",14009);
        }
        System.out.println("登录"+loginByCPVo.toString());
        String token=null;
        Map map=new HashMap();
        Account account=new Account();
        String code=loginByCPVo.getUserCode();
        String pattern = "^1[\\d]{10}";
        if (!Pattern.matches(pattern,code)){
            return DtoUtil.getFalseDto("账号格式不正确",14010);
        }
        //登录
        Account result=accountMapper.checkCode(code);
        if (ObjectUtils.isEmpty(result)){
            return DtoUtil.getFalseDto("该账号尚未注册",200000);
        }
        if (StringUtils.isEmpty(result.getUserPassword())){
            map.put("id",result.getId());
            map.put("userCode",result.getUserCode());
            map.put("isFirst",result.getIsFirst());
            map.put("userName",result.getUserName());
            map.put("gender",result.getGender());
            map.put("birthday",result.getBirthday());
            map.put("headImgUrl",result.getHeadImgUrl());
            map.put("dnd","1");
            map.put("userSign",result.getUserSign());
            return DtoUtil.getSuccesWithDataDto("该账号没有设置密码",map,100000);
        }
        int res=accountMapper.queryUserByCp(loginByCPVo.getUserCode(),MD5Util.createMD5(loginByCPVo.getUserPassword()));
        if (res<1){
            return DtoUtil.getFalseDto("账号或密码错误",14233);
        }
        //生成token
        if (StringUtils.isEmpty(result.getHeadImgUrl())){
            result.setHeadImgUrl("233");
        }
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
        //更换客户端
        int i=updateAppType(result.getId().toString(), loginByCPVo.getAppType(),loginByCPVo.getDeviceToken());
        /*if (i==1){
            return DtoUtil.getFalseDto("客户端类型不能为空",14021);
        }else if (i==2){
            return DtoUtil.getFalseDto("客户端类型格式不正确",14022);
        }else if (i==4){
            return DtoUtil.getFalseDto("客户端类型格式不正确",14022);
        }*/
        map.put("id",result.getId());
        map.put("userCode",result.getUserCode());
        map.put("isFirst",result.getIsFirst());
        map.put("userName",result.getUserName());
        map.put("gender",result.getGender());
        map.put("birthday",result.getBirthday());
        map.put("headImgUrl",result.getHeadImgUrl());
        map.put("userSign",result.getUserSign());
        map.put("token",token);
        //查询用户是否开启了勿扰模式
        map.put("dnd",userSettingsMapper.getDND(result.getId().toString())!=null?userSettingsMapper.getDND(result.getId().toString()).toString():"");
        return DtoUtil.getSuccesWithDataDto("登录成功",map,100000);
    }

    /**
     * 更换客户端
     * @param userId
     * @param apType
     * @return
     */
    private int updateAppType(String userId,String apType,String deviceToken){
        AppType appType=appTypeMapper.queryAppType(userId);
        String aType;
        try {
            if (StringUtils.isEmpty(apType)){
                return 1;
            } else if (ANDROID.equalsIgnoreCase(apType.substring(0, apType.indexOf(",")))) {
                aType = "1";
            } else if (IOS.equalsIgnoreCase(apType.substring(0, apType.indexOf(",")))){
                aType = "2";
            }else {
                return 2;
            }
        } catch (StringIndexOutOfBoundsException e) {
            logger.error(e.getMessage(),e);
            return 4;
        }
        //第一次绑定
        if (ObjectUtils.isEmpty(appType)){
            appTypeMapper.insertAppType(userId,aType,deviceToken);
        }else {
            //更换绑定
            appTypeMapper.updateAppType(aType,userId,deviceToken);
        }
        return 3;
    }

    /**
     * 重置密码
     * @param loginByCPVo
     * @return
     */
    @Override
    public Dto resetPassword(LoginByCPVo loginByCPVo) {
        if (StringUtils.isEmpty(loginByCPVo.getUserCode())||StringUtils.isEmpty(loginByCPVo.getUserPassword())){
            return DtoUtil.getFalseDto("账号或密码不符合格式",14010);
        }
        Account account=accountMapper.checkCode(loginByCPVo.getUserCode());
        Account account1=new Account();
        account1.setId(account.getId());
        account1.setUserPassword(MD5Util.createMD5(loginByCPVo.getUserPassword()));
        accountMapper.updateAccount(account1);
        return DtoUtil.getSuccessDto("密码重置成功，请返回登录",100000);
    }

    /**
     * 退出登录
     * @param receivedId
     * @param token
     * @return
     */
    @Override
    public Dto loginOut(ReceivedId receivedId, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(receivedId.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        String apType=receivedId.getAppType();
        try {
            if (StringUtils.isEmpty(apType)){
                apType="0";
            }  else if (ANDROID.equalsIgnoreCase(apType.substring(0, apType.indexOf(",")))) {
                apType = "1";
            } else if (IOS.equalsIgnoreCase(apType.substring(0, apType.indexOf(",")))){
                apType = "2";
            }else {
                apType="0";
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            apType="0";
        }
        appTypeMapper.updateAppType(apType,receivedId.getUserId(),null);
        return DtoUtil.getSuccessDto("您已退出登录",100000);
    }

    /**
     *  搜索已添加的好友
     * @param searchFriendVo
     * @param token
     * @return
     */
    @Override
    public Dto searchFriend(SearchFriendVo searchFriendVo, String token) {
        if (!token.equals(stringRedisTemplate.opsForValue().get(searchFriendVo.getUserId()))){
            return DtoUtil.getFalseDto("请重新登录",21014);
        }
        String searchCondition=searchFriendVo.getSearchCondition();
        List<Map<String,String>> mapList=new ArrayList<>();
        String userName=null;
        String userCode=null;
        String pattern = "^\\d{1,}$";
        if(!StringUtils.isEmpty(searchCondition)){
            if (Pattern.matches(pattern,searchCondition)){
                userCode=searchCondition;
            }else {
                userName=searchCondition;
            }
            List<Account> accountList=accountMapper.searchFriend(searchFriendVo.getUserId(),userCode,userName);
            for (Account account:accountList) {
                Map<String,String> map=new HashMap<>();
                map.put("friendId",account.getId().toString());
                map.put("userName",account.getUserName());
                map.put("headImgUrl",account.getHeadImgUrl());
                map.put("gender",account.getGender().toString());
                mapList.add(map);
            }
        }else {
            List<Account> accountList=accountMapper.searchFriend(searchFriendVo.getUserId(),userCode,userName);
            for (Account account:accountList) {
                Map<String,String> map=new HashMap<>();
                map.put("friendId",account.getId().toString());
                map.put("userName",account.getUserName());
                map.put("headImgUrl",account.getHeadImgUrl());
                map.put("gender",account.getGender().toString());
                mapList.add(map);
            }
        }
        return DtoUtil.getSuccesWithDataDto("查询成功",mapList,100000);
    }
}
