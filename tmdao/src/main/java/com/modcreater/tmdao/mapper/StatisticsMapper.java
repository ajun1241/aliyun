package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.StatisticsTable;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/22 13:23
 */
@Mapper
public interface StatisticsMapper {
    /**
     * 创建一张统计表
     * @param statisticsTable
     * @return
     */
    int createStatistics(List<StatisticsTable> statisticsTable);

    /**
     * 更改统计表
     * @param statisticsTable
     * @return
     */
    int updateStatistics(StatisticsTable statisticsTable);

    /**
     * 查询统计总条数
     * @param statisticsTable
     * @return
     */
    int queryStatisticsCount(StatisticsTable statisticsTable);

    /**
     * 查询最终统计结果
     * @param creatorId
     * @param eventId
     * @return
     */
    Map<String,String> queryFeedbackStatistics(String creatorId,String eventId);

}
