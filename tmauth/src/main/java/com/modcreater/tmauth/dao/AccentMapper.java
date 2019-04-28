package com.modcreater.tmauth.dao;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Account;
import com.modcreater.tmbeans.vo.AccountVo;
import com.modcreater.tmbeans.vo.LoginVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccentMapper {

    /**
     * 登录
     * @return
     */
    int doLogin(LoginVo loginVo);

    /**
     * 注册
     * @param account 用户信息
     * @return
     */
    int register(Account account);

    /**
     * 判断用户名
     * @param userCode
     * @return
     */
    int checkCode(@Param("userCode") String userCode);

//    List<Account> queryAll();
}
