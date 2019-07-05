package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.Informationsafety;

import java.util.Map;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/7/5 14:56
 */
public interface SafetyMapper {

    /**
     * 插入用户记录
     * @param informationsafety
     * @return
     */
    int insertRecord(Map<String,Object> informationsafety);
}
