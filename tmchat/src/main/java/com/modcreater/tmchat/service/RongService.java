package com.modcreater.tmchat.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.rongVo.UserVo;

/**
 * 融云业务接口
 */
public interface RongService {
    /**
     * 注册获得token
     * @return
     */
    Dto rongRegister(UserVo userVo);
}
