package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Account;
import com.modcreater.tmbeans.vo.AccountVo;
import com.modcreater.tmbeans.vo.LoginVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccountMapper {

    /**
     * 登录
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
    int updateTimestampUnderAccount(String id ,String time);

    /**
     * 查询时间戳
     * @param id
     * @return
     */
    String queryTime(String id);
}
