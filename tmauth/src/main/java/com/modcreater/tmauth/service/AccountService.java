package com.modcreater.tmauth.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Account;
import com.modcreater.tmbeans.vo.AccountVo;
import com.modcreater.tmbeans.vo.LoginVo;

import java.util.List;

public interface AccountService {
    /**
     * 登录
     * @return
     */
    Dto doLogin(LoginVo loginVo);

    /**
     * 修改账号信息
     * @param accountVo
     * @return
     */
    Dto updateAccount(AccountVo accountVo);

    /**
     * 查看用户详情
     * @param id
     * @return
     */
    Dto queryAccount(String id);
}
