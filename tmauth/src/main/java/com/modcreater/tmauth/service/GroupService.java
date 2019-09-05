package com.modcreater.tmauth.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.GroupInfoVo;

/**
 * Description:
 *  团队
 * @Author: AJun
 * @Date: 2019/9/5 16:06
 */
public interface GroupService {

    /**
     * 创建团队
     * @param groupInfoVo
     * @param token
     * @return
     */
    Dto createGroup(GroupInfoVo groupInfoVo,String token);

}
