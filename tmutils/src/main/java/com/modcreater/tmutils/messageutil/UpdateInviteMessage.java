package com.modcreater.tmutils.messageutil;

import io.rong.messages.BaseMessage;
import io.rong.util.GsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/22 11:06
 */

public class UpdateInviteMessage extends BaseMessage {
    /**
     * 事件Id
     */
    private String eventId = "";
    /**
     * 修改前事件名称
     */
    private String eventName = "";
    /**
     * 几处修改
     */
    private String count = "";
    /**
     * 修改前事件类型
     */
    private String eventType = "";
    /**
     *  修改详情list
     */
    private List<Map<String,String>> details=new ArrayList<>();
    /**
     *   消息id
     */
    private String msgId = "";
    /**
     * 预留的扩展内容
     */
    private String extra = "";

    private static final transient String TYPE = "ZX:UpdInviteMsg";

    public UpdateInviteMessage(String eventId, String eventName, String count, String eventType, List<Map<String, String>> details, String msgId, String extra) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.count = count;
        this.eventType = eventType;
        this.details = details;
        this.msgId = msgId;
        this.extra = extra;
    }

    @Override
    public String getType() {
        return "ZX:UpdInviteMsg";
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public List<Map<String, String>> getDetails() {
        return details;
    }

    public void setDetails(List<Map<String, String>> details) {
        this.details = details;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this, UpdateInviteMessage.class);
    }
}
