package com.modcreater.tmutils.messageutil;

import io.rong.messages.BaseMessage;
import io.rong.util.GsonUtil;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/6/6 16:13
 */
public class FeedbackInviteMessage extends BaseMessage {
    /**
     * 同意人数
     */
    private String agree = "";
    /**
     * 拒绝人数
     */
    private String refuse = "";
    /**
     * 无应答人数
     */
    private String noReply = "";
    /**
     * 总人数
     */
    private String total = "";

    private String extraData = "";

    private String extra = "";

    private String msgId = "";

    private static final transient String TYPE = "ZX:FeedbackInviteMsg";

    public FeedbackInviteMessage(String agree, String refuse, String noReply, String total, String extraData, String extra,String msgId) {
        this.agree = agree;
        this.refuse = refuse;
        this.noReply = noReply;
        this.total = total;
        this.extraData = extraData;
        this.extra = extra;
        this.msgId = msgId;
    }

    @Override
    public String getType() {
        return "ZX:FeedbackInviteMsg";
    }

    public String getAgree() {
        return agree;
    }

    public void setAgree(String agree) {
        this.agree = agree;
    }

    public String getRefuse() {
        return refuse;
    }

    public void setRefuse(String refuse) {
        this.refuse = refuse;
    }

    public String getNoReply() {
        return noReply;
    }

    public void setNoReply(String noReply) {
        this.noReply = noReply;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this, FeedbackInviteMessage.class);
    }
}
