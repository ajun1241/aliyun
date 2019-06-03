package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.AppVersion;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-03
 * Time: 9:04
 */
@Mapper
public interface AppMapper {

    /**
     * 获取APP版本信息
     * @param now
     * @return
     */
    AppVersion getAppVersion(String now);

    /**
     * 更新APP更新人数
     * @param updateTimes
     * @param uploadTime
     * @return
     */
    int updateUpdateTimes(Long updateTimes,Date uploadTime);
}
