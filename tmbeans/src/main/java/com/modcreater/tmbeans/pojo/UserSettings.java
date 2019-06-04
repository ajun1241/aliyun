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
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 是否接收新消息
     */
    private Long receiveNewMessage;
    /**
     * 通知消息显示详情
     */
    private Long newMessageDetails;
    /**
     * 是否开启勿扰模式
     */
    private Long dnd;
    /**
     * 勿扰模式开始时间
     */
    private Long dndStartTime;
    /**
     * 勿扰模式结束时间
     */
    private Long dndEndTime;
    /**
     * 是否开启好友邀请
     */
    private Long friendInvite;
    /**
     * 是否开启好友支持
     */
    private Long friendSupport;
    /**
     * 是否开启好友事件查看
     */
    private Long friendHide;




/*
    *//**
     * 新消息系统提示
     *//*
    private Long newMessageSystemNotify;
    *//**
     * 聊天界面中的新消息通知
     *//*
    private Long newMessageForChat;
    *//**
     * 重要又紧急
     *//*
    private Long importantAndUrgent;
    *//**
     * 重要但不紧急
     *//*
    private Long important;
    *//**
     * 不重要但紧急
     *//*
    private Long urgent;
    *//**
     * 不重要也不紧急
     *//*
    private Long notImportantAndUrgent;
    *//**
     * 自定义
     *//*
    private Long optional;
    *//**
     * 通过手机号码邀请
     *//*
    private Long phoneNumInvite;
    *//**
     * 通过微信号邀请
     *//*
    private Long wechatNumInvite;
    *//**
     * 通过QQ号邀请
     *//*
    private Long qqNumInvite;
    *//**
     * 通过群聊邀请
     *//*
    private Long groupInvite;
    *//**
     * 通过我的ID邀请
     *//*
    private Long myIdInvite;
    *//**
     * 加我为好友时是否需要验证
     *//*
    private Long beFriendNeedVerification;
    *//**
     * 是否能向用户推荐通讯录朋友
     *//*
    private Long recommendContacts;
    *//**
     *
     *//*
    private Long permissionToView;
    *//**
     * 只在WIFI下更新
     *//*
    private Long onlyWiFi;
    *//**
     * 简体中文
     *//*
    private Long simplifiedChinese;
    *//**
     * 繁体中文(香港)
     *//*
    private Long chineseTraditionalForHongKong;
    *//**
     * 繁体中文(台湾)
     *//*
    private Long chineseTraditionalForTaiWan;
    *//**
     * 英语
     *//*
    private Long forEnglish;
    *//**
     * 印度尼西亚语
     *//*
    private Long indonesia;
    *//**
     * 日语
     *//*
    private Long japanese;
    *//**
     * 法语
     *//*
    private Long french;
    *//**
     * 字体大小
     *//*
    private Long font;*/

}
