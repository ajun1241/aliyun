package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.SuperAdministrator;

import java.util.List;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/8/27 10:33
 */
public interface SuperAdminMapper {
    /**
     * 查询管理员
     * @return
     */
    List<SuperAdministrator> querySuperAdmins();
}
