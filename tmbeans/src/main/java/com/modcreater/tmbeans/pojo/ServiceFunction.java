package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-14
 * Time: 9:09
 */
@Data
public class ServiceFunction {
    /**
     * 好友服务(0:未开通;1:已开通)
     */
    private String friendService;
    /**
         * 查询服务(0:未开通;1:已开通)
     */
    private String searchService;
    /**
     * 年报服务(0:未开通;1:已开通)
     */
    private String annualReportingService;
    /**
     * 备份服务(0:未开通;1:已开通)
     */
    private String backupService;

}
