package com.modcreater.tmchat.rong.models.message;

import io.rong.models.message.MentionMessageContent;

/**
 *
 * @author rongcloud
 */
public class MentionMessage {
    public String senderId;
    /**
     * 接收群 Id，提供多个本参数可以实现向多群发送消息，最多不超过 3 个群组。（必传）
     */
    public String[] targetId;
    public String objectName;
    /**
     * 消息 内容
     */
    public MentionMessageContent content;
    public String pushContent;
    public String pushData;
    public Integer isPersisted;
    public Integer isCounted;
    public Integer isIncludeSender;
    private Integer contentAvailable;

    public MentionMessage() {
    }

    public MentionMessage(String senderId, String[] targetId, String objectName, MentionMessageContent content, String pushContent, String pushData,
                          Integer isPersisted, Integer isCounted, Integer isIncludeSender, Integer contentAvailable) {
        this.senderId = senderId;
        this.targetId = targetId;
        this.objectName = objectName;
        this.content = content;
        this.pushContent = pushContent;
        this.pushData = pushData;
        this.isPersisted = isPersisted;
        this.isCounted = isCounted;
        this.isIncludeSender = isIncludeSender;
        this.contentAvailable = contentAvailable;
    }

    public String getSenderId() {
        return this.senderId;
    }

    public io.rong.models.message.MentionMessage setSenderId(String senderId) {
        this.senderId = senderId;
        return this;
    }

    public String[] getTargetId() {
        return this.targetId;
    }

    public io.rong.models.message.MentionMessage setTargetId(String[] targetId) {
        this.targetId = targetId;
        return this;
    }

    public String getObjectName() {
        return this.objectName;
    }

    public io.rong.models.message.MentionMessage setObjectName(String objectName) {
        this.objectName = objectName;
        return this;
    }

    public MentionMessageContent getContent() {
        return this.content;
    }

    public io.rong.models.message.MentionMessage setContent(MentionMessageContent content) {
        this.content = content;
        return this;
    }

    public String getPushContent() {
        return this.pushContent;
    }

    public io.rong.models.message.MentionMessage setPushContent(String pushContent) {
        this.pushContent = pushContent;
        return this;
    }

    public String getPushData() {
        return this.pushData;
    }

    public io.rong.models.message.MentionMessage setPushData(String pushData) {
        this.pushData = pushData;
        return this;
    }

    public Integer getIsPersisted() {
        return this.isPersisted;
    }

    public io.rong.models.message.MentionMessage setIsPersisted(Integer isPersisted) {
        this.isPersisted = isPersisted;
        return this;
    }

    public Integer getIsCounted() {
        return this.isCounted;
    }

    public io.rong.models.message.MentionMessage setIsCounted(Integer isCounted) {
        this.isCounted = isCounted;
        return this;
    }

    public Integer getIsIncludeSender() {
        return this.isIncludeSender;
    }

    public io.rong.models.message.MentionMessage setIsIncludeSender(Integer isIncludeSender) {
        this.isIncludeSender = isIncludeSender;
        return this;
    }

    public Integer getContentAvailable() {
        return this.contentAvailable;
    }

    public io.rong.models.message.MentionMessage setContentAvailable(Integer contentAvailable) {
        this.contentAvailable = contentAvailable;
        return this;
    }
}
