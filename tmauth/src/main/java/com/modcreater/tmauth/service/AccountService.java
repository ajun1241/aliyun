package com.modcreater.tmauth.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Friendship;
import com.modcreater.tmbeans.vo.AccountVo;
import com.modcreater.tmbeans.vo.AddPwdVo;
import com.modcreater.tmbeans.vo.LoginVo;
import com.modcreater.tmbeans.vo.QueryUserVo;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmbeans.vo.uservo.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface AccountService {

    /**
     * 注册/登录
     * @param loginVo
     * @return
     */
    Dto registered(LoginVo loginVo);

    /**
     * 修改账号信息
     * @param updAccountInfo
     * @param token
     * @return
     */
    Dto updateAccount(UpdAccountInfo updAccountInfo,String token);

    /**
     * 查看用户详情()
     * @param queryUserVo
     * @param token
     * @return
     */
    Dto queryAccount(QueryUserVo queryUserVo,String token);

    /**
     * 新查看用户详情(userId)
     * @param receivedId
     * @param token
     * @return
     */
    Dto newQueryAccount(ReceivedId receivedId, String token);
    /**
     *添加二级密码
     * @param addPwdVo
     * @return
     */
    Dto addPassword(AddPwdVo addPwdVo);


   /* *//**
     *执行操作时修改时间戳
     * @param date
     * @return
     *//*
    Dto updateTimeStamp(String date);*/

    /**
     * 根据账号搜索好友
     * @param queFridenVo
     * @param token
     * @return
     */
    Dto queryFriendByUserCode(QueFridenVo queFridenVo, String token);

    /**
     * 发送添加好友请求
     * @param sendFriendRequestVo
     * @param token
     * @return
     */
    Dto sendFriendRequest(SendFriendRequestVo sendFriendRequestVo,String token);

    /**
     * 发送接受好友请求
     * @param sendFriendResponseVo
     * @param token
     * @return
     */
    Dto sendFriendResponse(FriendshipVo sendFriendResponseVo,String token);


    /**
     * 查询好友列表
     * @param userIdVo
     * @param token
     * @return
     */
    Dto queryFriendList(UserIdVo userIdVo, String token);

    /**
     * 查看好友详情
     * @param queFridenVo
     * @param token
     * @return
     */
    Dto queryFriendDetails(FriendshipVo queFridenVo, String token);

    /**
     * 修改好友权限
     * @param jurisdictionVo
     * @param token
     * @return
     */
    Dto updateFriendJurisdiction(UpdateFriendJurisdictionVo jurisdictionVo, String token);

    /**
     * 解除好友关系
     * @param deleteFriendshipVo
     * @param token
     * @return
     */
    Dto deleteFriendship(FriendshipVo deleteFriendshipVo, String token);

    /**
     * 查询所有好友消息
     * @param receivedId
     * @param token
     * @return
     */
    Dto queryAllUnreadMsg(ReceivedId receivedId,String token);

    /**
     * 查询所有未读条数
     * @param receivedId
     * @param token
     * @return
     */
    Dto queryAllUnreadMsgCount(ReceivedId receivedId,String token);

    /**
     * 上传头像
     * @param headImgVo
     * @param token
     * @return
     */
    Dto uplHeadImg(HeadImgVo headImgVo,String token);

    /**
     * 查询会话列表好友信息（头像、昵称）
     * @param friendshipVo
     * @return
     */
    Dto querySessionListDetail(FriendshipVo friendshipVo,String token);

    /**
     * 判断是否是好友
     * @param friendshipVo
     * @param token
     * @return
     */
    Dto judgeFriendship(FriendshipVo friendshipVo,String token);

    /**
     * 发送验证好友消息
     * @param requestVo
     * @param token
     * @return
     */
    Dto sendVerifyFriendMsg(SendFriendRequestVo requestVo,String token);

    /**
     * 发送好友名片
     * @param friendCardVo
     * @param token
     * @return
     */
    Dto sendFriendCard(FriendCardVo friendCardVo,String token);

    /**
     * 添加黑名单
     * @param friendshipVo
     * @param token
     * @return
     */
    Dto addBlackList(FriendshipVo friendshipVo,String token);

    /**
     * 查看黑名单列表
     * @param userIdVo
     * @param token
     * @return
     */
    Dto queryBlackList(UserIdVo userIdVo,String token);

    /**
     * 移出黑名单
     * @param friendshipVo
     * @param token
     * @return
     */
    Dto removeBlackList(FriendshipVo friendshipVo,String token);

}
