package com.modcreater.tmbeans.vo.eventvo;

import lombok.Data;

import java.util.Date;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/8/28 10:16
 */
@Data
public class BacklogListVo {
    private String userId;  //用户Id
    private String appType;
    private String id; //主键
    private String singleEventId; //事件id
    private String backlogName; // 清单名称
    private String backlogStatus; //完成状态（0：待完成；1：已完成）
    private String finishTime;  //完成时间
    private String isTest;  //测试（0：否；1：是）
    private String createTime;  //创建时间
    private String eventId;
}
