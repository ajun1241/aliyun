package com.modcreater.tmutils.messageutil;

import io.rong.messages.BaseMessage;
import io.rong.util.GsonUtil;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/10/9 14:58
 */
public class GoodsListMsg extends BaseMessage {

    private String goodsList;
    /**
     * 消息类型：1：发送商品列表
     *          2：商家确认完毕发送支付消息
     */
    private String msgType;
    private String extra;

    public GoodsListMsg(String goodsList, String msgType, String extra) {
        this.goodsList = goodsList;
        this.msgType = msgType;
        this.extra = extra;
    }

    private static final transient String TYPE = "ZX:GoodsListMsg";

    public String getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(String goodsList) {
        this.goodsList = goodsList;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String getType() {
        return "ZX:GoodsListMsg";
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this, GoodsListMsg.class);
    }
}
