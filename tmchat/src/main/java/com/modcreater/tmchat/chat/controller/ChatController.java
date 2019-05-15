package com.modcreater.tmchat.chat.controller;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmutils.DtoUtil;
import io.rong.RongCloud;
import io.rong.messages.TxtMessage;
import io.rong.methods.message._private.Private;
import io.rong.methods.message.chatroom.Chatroom;
import io.rong.methods.message.discussion.Discussion;
import io.rong.methods.message.group.Group;
import io.rong.methods.message.history.History;
import io.rong.methods.message.system.MsgSystem;
import io.rong.methods.user.User;
import io.rong.models.Result;
import io.rong.models.message.PrivateMessage;
import io.rong.models.message.SystemMessage;
import io.rong.models.response.ResponseResult;
import io.rong.models.response.TokenResult;
import io.rong.models.user.UserModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-14
 * Time: 11:11
 */
@RestController
@RequestMapping(value = "/chat/")
public class ChatController {





    private static final String appKey = "0vnjpoad03rzz";
    private static final String appSecret = "BbTOtrRIF5MOA";
    private static final String api = "http://api-cn.ronghub.com";
    private static final TxtMessage txtMessage = new TxtMessage("20190514测试消息发送", "helloExtra");

    @RequestMapping(value = "solo", method = RequestMethod.POST)
    public void testSolo(String tarId) throws Exception{
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        //自定义 api 地址方式
        //RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret,api);

        Private Private = rongCloud.message.msgPrivate;
        MsgSystem system = rongCloud.message.system;
        Group group = rongCloud.message.group;
        Chatroom chatroom = rongCloud.message.chatroom;
        Discussion discussion = rongCloud.message.discussion;
        History history = rongCloud.message.history;

        /**
         * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/message/system.html#send
         *
         * 发送单聊消息
         *
         */
        String[] targetIds = {tarId};
        PrivateMessage privateMessage = new PrivateMessage()
                .setSenderId("userxxd2")
                .setTargetId(targetIds)
                .setObjectName(txtMessage.getType())
                .setContent(txtMessage)
                .setPushContent("")
                .setPushData("{\"pushData\":\"hello\"}")
                .setCount("4")
                .setVerifyBlacklist(0)
                .setIsPersisted(0)
                .setIsCounted(0)
                .setIsIncludeSender(0);
        ResponseResult privateResult = Private.send(privateMessage);
        System.out.println(privateResult.toString());
    }

    @RequestMapping(value = "register",method = RequestMethod.POST)
    public Dto testRegister()throws Exception {
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        //自定义 api 地址方式
        // RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret,api);
        User User = rongCloud.user;

        /**
         * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/user/user.html#register
         *
         * 注册用户，生成用户在融云的唯一身份标识 Token
         */
        UserModel user = new UserModel()
                .setId("zhoujun")
                .setName("username")
                .setPortrait("http://www.rongcloud.cn/images/logo.png");
        TokenResult result = User.register(user);
        System.out.println("getToken:  " + result.toString());

        /**
         *
         * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/user/user.html#refresh
         *
         * 刷新用户信息方法
         */
        Result refreshResult = User.update(user);
        System.out.println("refresh:  " + refreshResult.toString());

        return DtoUtil.getSuccesWithDataDto("测试注册",result.toString(),100000);
    }
}
