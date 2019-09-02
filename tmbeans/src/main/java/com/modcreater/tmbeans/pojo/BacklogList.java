package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: AJun
 * Created with IntelliJ IDEA.
 * Description: 事件清单
 */
@Data
public class BacklogList {

    private Long id; //主键
    private Long singleEventId; //事件id
    private String backlogName; // 清单名称
    private Long backlogStatus; //完成状态（0：待完成；1：已完成）
    private Long finishTime;  //完成时间
    private Long isTest;  //测试（0：否；1：是）
    private String createTime;  //创建时间
    private Long isSync;  //是否同步

}
