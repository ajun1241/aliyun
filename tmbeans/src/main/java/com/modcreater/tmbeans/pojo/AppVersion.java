package com.modcreater.tmbeans.pojo;

import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-03
 * Time: 8:52
 */
@Data
public class AppVersion {

    private String id;
    /**
     * app名称
     */
    private String appName;
    /**
     * app版本
     */
    private String appVersion;
    /**
     * app安装包下载地址
     */
    private String apkUrl;
    /**
     * 上传时间
     */
    private Date uploadTime;
    /**
     * 更新说明
     */
    private String updateInstructions;
    /**
     * 功能介绍
     */
    private String functionIntroduction;
    /**
     * 更新人数
     */
    private Long updateTimes;

}
