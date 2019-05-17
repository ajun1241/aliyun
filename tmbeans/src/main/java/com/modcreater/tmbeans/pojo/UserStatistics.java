package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-16
 * Time: 19:31
 */
@Data
public class UserStatistics {
    /**
     *主键
     */
    private Long id;
    /**
     *用户ID
     */
    private Long userId;
    /**
     *登录的天数
     */
    private Long loggedDays;
    /**
     *完成的事件
     */
    private Long completed;
    /**
     *未完成的事件
     */
    private Long unfinished;
    /**
     *草稿箱内的事件
     */
    private Long drafts;
    /**
     * 最后操作(按天查询)的时间
     */
    private Long lastOperatedTime;

}
