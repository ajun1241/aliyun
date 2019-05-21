package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-21
 * Time: 14:00
 */
@Data
public class UserSettings {

    /**
     * 主键
     */
    private long id;
    /**
     * 用户ID
     */
    private long userId;
    /**
     * 是否接收新消息
     */
    private long receiveNewMessage;
    /**
     * 通知消息显示详情
     */
    private long newMessageDetails;
    /**
     * 新消息系统提示
     */
    private long newMessageSystemNotify;
    /**
     * 聊天界面中的新消息通知
     */
    private long newMessageForChat;
    /**
     * 是否开启勿扰模式
     */
    private long dnd;
    /**
     * 勿扰模式开始时间
     */
    private long dndStartTime;
    /**
     * 勿扰模式结束时间
     */
    private long dndEndTime;
    /**
     * 重要又紧急
     */
    private long importantAndUrgent;
    /**
     * 重要但不紧急
     */
    private long important;
    /**
     * 不重要但紧急
     */
    private long urgent;
    /**
     * 不重要也不紧急
     */
    private long notImportantAndUrgent;
    /**
     * 自定义
     */
    private long optional;
    /**
     * 是否开启好友邀请
     */
    private long friendInvite;
    /**
     * 是否开启好友支持
     */
    private long friendSupport;
    /**
     * 通过手机号码邀请
     */
    private long phoneNumInvite;
    /**
     * 通过微信号邀请
     */
    private long wechatNumInvite;
    /**
     * 通过QQ号邀请
     */
    private long qqNumInvite;
    /**
     * 通过群聊邀请
     */
    private long groupInvite;
    /**
     * 通过我的ID邀请
     */
    private long myIdInvite;
    /**
     * 只在WIFI下更新
     */
    private long onlyWiFi;
    /**
     * 简体中文
     */
    private long simplifiedChinese;
    /**
     * 繁体中文(香港)
     */
    private long chineseTraditionalForHongKong;
    /**
     * 繁体中文(台湾)
     */
    private long chineseTraditionalForTaiWan;
    /**
     * 英语
     */
    private long forEnglish;
    /**
     * 印度尼西亚语
     */
    private long indonesia;
    /**
     * 日语
     */
    private long japanese;
    /**
     * 法语
     */
    private long french;
    /**
     * 字体大小
     */
    private long font;

}
