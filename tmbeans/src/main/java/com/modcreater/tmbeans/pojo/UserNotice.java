package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-17
 * Time: 14:50
 */
@Data
public class UserNotice {

    private String id;

    private String userId;
    /**
     * 公告名称
     */
    private String noticeName;
    /**
     * 今日通知次数
     */
    private Long todayNotifications;

}
