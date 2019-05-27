package com.modcreater.tmdao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-27
 * Time: 17:36
 */
@Mapper
public interface UserServiceMapper {

    /**
     * 判断用户是否开启了搜索权限
     * @param userId
     * @return
     */
    int getSearchServie(String userId);
}
