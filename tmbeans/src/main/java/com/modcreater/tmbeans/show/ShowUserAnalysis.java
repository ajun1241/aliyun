package com.modcreater.tmbeans.show;

import lombok.Data;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-20
 * Time: 14:59
 */
@Data
public class ShowUserAnalysis {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * key:事件类型编号
     * value:该类型事件数量占总有效事件数量总和的百分比浮点数
     */
    private Map<String,Double> percentResult;
    /**
     * key:事件类型编号
     * value:该类型事件所用分钟数
     */
    private Map<String,Long> totalMinutesResult;
    /**
     * 有效事件总数量
     */
    private Long totalEvents;
    /**
     * 用时最长分类
     */
    private Long maxType;
    /**
     * 用时最短分类
     */
    private Long minType;
    /**
     * 所有有效事件的总分钟数
     */
    private Long sumMinutes;

}
