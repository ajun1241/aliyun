package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.dto.MyDetail;
import com.modcreater.tmbeans.pojo.Account;
import com.modcreater.tmbeans.pojo.Friendship;
import com.modcreater.tmbeans.show.userinfo.ShowUserDetails;
import com.modcreater.tmbeans.vo.LoginVo;
import com.modcreater.tmbeans.vo.usersettings.GetFriendListInSettings;
import com.modcreater.tmbeans.vo.uservo.FriendshipVo;
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
     * 查询好友关系
     * @param userId
     * @param friendId
     * @return
     */
    int queryFriendRel(String userId,String friendId);


    /**
     * 建立好友关系
     * @param userId
     * @param friendId
     * @param status
     * @return
     */
    int buildFriendship(String userId,String friendId,String status);


    /**
     * 修改好友关系状态
     * @param userId
     * @param friendId
     * @param status
     * @return
     */
    int updateFriendship(String userId,String friendId,String status);

    /**
     * 查询好友列表
     * @param userId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    List<Account> queryFriendList(@Param("userId") String userId,@Param("pageIndex") int pageIndex,@Param("pageSize") int pageSize);


    /**
     * 修改好友权限
     * @param jurisdictionVo
     * @return
     */
    int updateFriendJurisdiction(UpdateFriendJurisdictionVo jurisdictionVo);

    /**
     * 修改限制(好友的邀请或支持权限)
     * @param getFriendListInSettings
     * @return
     */
    int updateFriendJurisdictionForSingleCondition(GetFriendListInSettings getFriendListInSettings);

    /**
     * 解除好友关系
     * @param deleteFriendshipVo
     * @return
     */
    int deleteFriendship(FriendshipVo deleteFriendshipVo);

    /**
     * 增加实名认证
     * @param userId
     * @param realNameAuthentication
     * @return
     */
    int updRealName(@Param("userId") String userId,@Param("realNameAuthentication") String realNameAuthentication);

    /**
     * 注册时增加用户权限表信息
     * @param userId
     * @return
     */
    int insertUserRight(String userId);

    /**
     * 根据用户ID查询用户名称头像及头像
     * @param userId
     * @return
     */
    ShowUserDetails queryUserDetails(String userId);

    /**
     * 查询待规划事件
     * @param userId
     * @param day
     * @param year
     * @param month
     * @return
     */
    MyDetail queryPlanByDayAndMonth(String  userId, String  day, String year , String  month);

    /**
     * 查询两人的好友详细信息
     * @param userId
     * @param friendId
     * @return
     */
    Friendship queryFriendshipDetail(String userId,String friendId);
}
