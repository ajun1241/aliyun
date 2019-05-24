package com.modcreater.tmdao.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/24 16:06
 */
@Mapper
public interface SystemMsgMapper {
    /**
     * 添加一条消息
     * @param map
     * @return
     */
    int addNewMsg(Map<String,String> map);
}
