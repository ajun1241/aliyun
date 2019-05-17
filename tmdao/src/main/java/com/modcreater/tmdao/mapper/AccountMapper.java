package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Account;
import com.modcreater.tmbeans.show.userinfo.ShowUserDetails;
import com.modcreater.tmbeans.vo.AccountVo;
import com.modcreater.tmbeans.vo.LoginVo;
import com.modcreater.tmbeans.vo.uservo.BuildFriendshipVo;
import com.modcreater.tmbeans.vo.uservo.DeleteFriendshipVo;
import com.modcreater.tmbeans.vo.uservo.UpdateFriendJurisdictionVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccountMapper {

    /**
     * 登录
     * @param loginVo
     * @return
     */
    Account doLogin(LoginVo loginVo);

    /**
     * 注册
     * @param account 用户信息
     * @return
     */
    int register(Account account);

    /**
     * 根据账号查询用户信息
     * @param userCode
     * @return
     */
    Account checkCode(@Param("userCode") String userCode);

    /**
     * 查看用户详情
     * @param id
     * @return
     */
    Account queryAccount(String id);

    /**
     * 修改用户信息
     * @param account
     * @return
     */
    int updateAccount(Account account);

    /**
     * 修改用户表下的时间戳
     * @param id 用户ID
     * @param time 时间戳
     * @return
     */
    int updateTimestampUnderAccount(@Param("userId") String id ,@Param("timestamp") String time);

    /**
     * 查询时间戳
     * @param id
     * @return
     */
    String queryTime(String id);


    /**
     * 根据账号搜索好友
     * @param userCode
     * @return
     */
    Account queryFriendByUserCode(String userCode);

    /**
     * 建立好友关系
     * @param buildFriendshipVo
     * @return
     */
    int buildFriendship(BuildFriendshipVo buildFriendshipVo);

    /**
     * 查询好友列表
     * @param userId
     * @return
     */
    List<Account> queryFriendList(String userId);

    /**
     * 修改好友权限
     * @param jurisdictionVo
     * @return
     */
    int updateFriendJurisdiction(UpdateFriendJurisdictionVo jurisdictionVo);

    /**
     * 解除好友关系
     * @param deleteFriendshipVo
     * @return
     */
    int deleteFriendship(DeleteFriendshipVo deleteFriendshipVo);

    /**
     * 根据用户ID查询用户名称头像及头像
     * @param userId
     * @return
     */
    ShowUserDetails queryUserDetails(String userId);
}
