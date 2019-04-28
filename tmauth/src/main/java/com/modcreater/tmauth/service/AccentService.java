package com.modcreater.tmauth.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Account;
import com.modcreater.tmbeans.vo.AccountVo;
import com.modcreater.tmbeans.vo.LoginVo;

import java.util.List;

public interface AccentService {
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
    Dto updateAccent(AccountVo accountVo);

//    List<Account> queryAll();
}
