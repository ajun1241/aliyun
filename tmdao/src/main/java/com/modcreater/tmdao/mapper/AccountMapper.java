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
    String doLogin(LoginVo loginVo);

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

}
