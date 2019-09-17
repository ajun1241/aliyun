package com.modcreater.tmbeans.vo.group;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-17
 * Time: 16:47
 */
@Data
public class GetGroupEventMsg {

    /**
     * 用户ID
     */
    private String userId;
    /**
     * 事件名称
     */
    private String eventName;
    /**
     * 开始日期(20190517)
     */
    private String startDate;
    /**
     * 事件类型(0：学习；1：工作；2：商务；3：休闲；4：家庭；5：节日；6：假期；7：其他)
     */
    private String eventType;
    /**
     * 事件等级(2：不紧迫也不重要；3：紧迫但不重要；4：重要又不紧迫；5：重要又紧迫)
     */
    private String eventLevel;
    /**
     * 开始时间(从当天0开始计算的分钟数)
     */
    private String startTime;
    /**
     * 结束时间(从当天0开始计算的分钟数)
     */
    private String endTime;
    /**
     * 相关人员
     */
    private String person;
    /**
     * 事件状态
     */
    private String isOverdue;
    /**
     * 要查询的类型(0:普通事件1:草稿箱)
     */
    private String searchType;
    /**
     * 当前页
     */
    private String pageNum;
    /**
     * 每页显示的条数
     */
    private String pageSize;

    private String groupId;

    private String appType;

}
