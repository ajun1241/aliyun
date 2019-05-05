package com.modcreater.tmbeans.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-05
 * Time: 9:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleEventForDatabase {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 事件id
     */
    private Long eventId;
    /**
     * 事件名称
     */
    private String eventName;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 地址
     */
    private String address;
    /**
     * 级别
     */
    private Long level;
    /**
     * 持续时间
     */
    private Long duration;
    /**
     * 标识：是否为空白事件，0为空白事件，1为其他事件
     */
    private Long flag;
    /**
     * 人物
     */
    private String person;
    /**
     * 备注
     */
    private String remarks;
    /**
     * 重复次数
     */
    private String repeatTime;
    /**
     * 是否过期，即已经完成，1代表已经完成，0代表未完成
     */
    private Long isOverdue;
    /**
     * 提醒时间
     */
    private Date remindTime;
    /**
     * 年
     */
    private Integer year;
    /**
     * 月
     */
    private Integer month;
    /**
     * 日
     */
    private Integer day;
    /**
     * 事件类型
     */
    private Integer type;

}
