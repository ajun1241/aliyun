package com.modcreater.tmdao.mapeer;

import com.modcreater.tmbeans.pojo.Account;
import com.modcreater.tmbeans.vo.LoginVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccentMapper {

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
     * 判断用户名
     * @param userCode
     * @return
     */
    int checkCode(@Param("userCode") String userCode);
}
