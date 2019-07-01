package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.Backers;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

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
     * @return
     */
    int addBackers(String userId,String backerId);

}
