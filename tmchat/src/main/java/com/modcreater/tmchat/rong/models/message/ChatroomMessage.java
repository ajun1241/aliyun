package com.modcreater.tmchat.rong.models.message;

import io.rong.messages.BaseMessage;
import io.rong.models.message.MessageModel;

/**
 * 聊天室消息体
 * @author RongCloud
 */
public class ChatroomMessage extends MessageModel {

    public ChatroomMessage() {

    }

    public ChatroomMessage(String senderUserId, String[] targetId, String objectName, BaseMessage content) {
        super(senderUserId, targetId, objectName, content, null, null);
    }

   @Override
    public io.rong.models.message.ChatroomMessage setSenderId(String senderId) {
        super.setSenderId(senderId);
        return this;
    }
    /**
     * 获取接受聊天室Id
     *
     * @return String
     */
    @Override
    public String[] getTargetId() {
        return super.getTargetId();
    }
    /**
     * 设置接受聊天室Id
     *
     * @return String
     */
    @Override
    public io.rong.models.message.ChatroomMessage setTargetId(String[] targetId) {
        super.setTargetId(targetId);
        return this;
    }
    @Override
    public io.rong.models.message.ChatroomMessage setObjectName(String objectName) {
        super.setObjectName(objectName);
        return this;
    }

    @Override
    public io.rong.models.message.ChatroomMessage setContent(BaseMessage content) {
        super.setContent(content);
        return this;
    }
}
