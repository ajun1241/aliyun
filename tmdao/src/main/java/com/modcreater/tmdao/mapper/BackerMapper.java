package com.modcreater.tmdao.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/27 16:17
 */
@Mapper
public interface BackerMapper {
    /**
     * 添加事件支持者
     * @param userId
     * @param backerId
     * @param eventId
     * @return
     */
    int addBackers(String userId,String[] backerId,String eventId);
}
