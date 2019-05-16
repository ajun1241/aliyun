package com.modcreater.tmauth.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Account;
import com.modcreater.tmbeans.vo.AccountVo;
import com.modcreater.tmbeans.vo.AddPwdVo;
import com.modcreater.tmbeans.vo.LoginVo;
import com.modcreater.tmbeans.vo.QueryUserVo;
import com.modcreater.tmbeans.vo.uservo.*;

import java.util.Date;
import java.util.List;

public interface AccountService {
    /**
     * 登录
     * @param loginVo
     * @return
     */
    Dto doLogin(LoginVo loginVo);

    /**
     * 注册/登录
     * @param loginVo
     * @return
     */
    Dto registered(LoginVo loginVo);

    /**
     * 修改账号信息
     * @param accountVo
     * @param token
     * @return
     */
    Dto updateAccount(AccountVo accountVo,String token);

    /**
     * 查看用户详情
     * @param queryUserVo
     * @param token
     * @return
     */
    Dto queryAccount(QueryUserVo queryUserVo,String token);

    /**
     *添加二级密码
     * @param addPwdVo
     * @return
     */
    Dto addPassword(AddPwdVo addPwdVo);

    /**
     * 查询用户的成就
     * @param userId
     * @param token
     * @return
     */
    Dto queryUserAchievement(String userId,String token);


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
     * 建立好友关系
     * @param buildFriendshipVo
     * @param token
     * @return
     */
    Dto buildFriendship(BuildFriendshipVo buildFriendshipVo,String token);

    /**
     * 查询好友列表
     * @param queryFriendListVo
     * @param token
     * @return
     */
    Dto queryFriendList(QueryFriendListVo queryFriendListVo, String token);

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
    Dto deleteFriendship(DeleteFriendshipVo deleteFriendshipVo, String token);
}
