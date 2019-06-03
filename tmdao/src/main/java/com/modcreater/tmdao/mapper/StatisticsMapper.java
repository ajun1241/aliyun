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
     * 回滚统计表
     * @param statisticsTable
     * @return
     */
    int rollbackStatistics(StatisticsTable statisticsTable);

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

    /**
     * 查询选择的人都有谁
     * @param choose
     * @param creatorId
     * @param eventId
     * @return
     */
    List<String> queryChooser(String choose,String creatorId,String eventId);

    /**
     * 设置统计表为已过期
     * @param creatorId
     * @param eventId
     * @return
     */
    int updStaIsOverdue(String creatorId,String eventId);

    /**
     * 查询一个统计表是否已过期
     * @param creatorId
     * @param eventId
     * @return
     */
    String selectStaIsOverdue(String creatorId,String eventId);
}
