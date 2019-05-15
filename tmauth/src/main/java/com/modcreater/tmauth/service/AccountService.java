package com.modcreater.tmauth.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Account;
import com.modcreater.tmbeans.vo.AccountVo;
import com.modcreater.tmbeans.vo.AddPwdVo;
import com.modcreater.tmbeans.vo.LoginVo;
import com.modcreater.tmbeans.vo.QueryUserVo;

import java.util.List;

public interface AccountService {
    /**
     * 登录
     * @param loginVo
     * @return
     */
    Dto doLogin(LoginVo loginVo);

    /**
     * 注册
     * @param loginVo
     * @return
     */
    Dto registered(LoginVo loginVo);

    /**
     * 修改账号信息
     * @param accountVo
     * @return
     */
    Dto updateAccount(AccountVo accountVo);

    /**
     * 查看用户详情
     * @param queryUserVo
     * @return
     */
    Dto queryAccount(QueryUserVo queryUserVo);

    /**
     *添加二级密码
     * @param addPwdVo
     * @return
     */
    Dto addPassword(AddPwdVo addPwdVo);

    /**
     * 查询token接口
     * @param userId
     * @return
     */
    Dto createToken(String userId);
}
