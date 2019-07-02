package com.modcreater.tmutils;

import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.show.MyToken;
import io.rong.RongCloud;
import io.rong.messages.BaseMessage;
import io.rong.messages.ContactNtfMessage;
import io.rong.messages.TxtMessage;
import io.rong.messages.VoiceMessage;
import io.rong.methods.message.chatroom.Chatroom;
import io.rong.methods.message.discussion.Discussion;
import io.rong.methods.message.group.Group;
import io.rong.methods.message.history.History;
import io.rong.methods.message.system.MsgSystem;
import io.rong.methods.user.User;
import io.rong.methods.user.blacklist.Blacklist;
import io.rong.models.Result;
import io.rong.models.message.PrivateMessage;
import io.rong.models.message.SystemMessage;
import io.rong.models.response.ResponseResult;
import io.rong.models.response.TokenResult;
import io.rong.models.user.UserModel;
import io.rong.util.GsonUtil;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * @Author: AJun
 * 生成token的工具类
 */

public class RongCloudMethodUtil {

    public final static String appKey = "0vnjpoad0314z";// 申请的融云key
    public final static String appSecret = "0uoZVUDt8lROGb";// 申请的的云secret

    VoiceMessage voiceMessage = new VoiceMessage("hello", "helloExtra", 20L);

    RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
    //自定义 api 地址方式
    //RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret,api);

    io.rong.methods.message._private.Private Private = rongCloud.message.msgPrivate;
    MsgSystem system = rongCloud.message.system;
    Group group = rongCloud.message.group;
    Chatroom chatroom = rongCloud.message.chatroom;
    Discussion discussion = rongCloud.message.discussion;
    History history = rongCloud.message.history;

    /**
     * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/user/user.html#register
     *
     * 注册用户，生成用户在融云的唯一身份标识 Token
     */
    public String createToken(String userId,String userName,String headImgUrl) throws Exception {

        User User = rongCloud.user;

        UserModel user = new UserModel()
                .setId(userId)
                .setName(userName)
                .setPortrait(headImgUrl);
        TokenResult result = User.register(user);
        if (ObjectUtils.isEmpty(result)){
            return null;
        }
        MyToken myToken= JSONObject.parseObject(result.toString(),MyToken.class);
        System.out.println("getToken:  " + result.toString());

        /**
         *
         * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/user/user.html#refresh
         *
         * 刷新用户信息方法
         */
        Result refreshResult = User.update(user);
        System.out.println("refresh:  " + refreshResult.toString());
        return myToken.getToken();
    }

    /**
     * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/message/system.html#send
     *
     * 发送系统消息
     *
     */
    public ResponseResult sendSystemMessage(String userId,String[] friendId,BaseMessage contactNtfMessage,String pushContent,String pushData) throws Exception {
        SystemMessage systemMessage = new SystemMessage()
                .setSenderId(userId)
                .setTargetId(friendId)
                .setObjectName(contactNtfMessage.getType())
                .setContent(contactNtfMessage)
                .setPushContent(pushContent)
                .setPushData(pushData)
                .setIsPersisted(0)
                .setIsCounted(0)
                .setContentAvailable(0);

        ResponseResult result = system.send(systemMessage);
        System.out.println("send system message:  " + result.toString());
        return result;
    }

    /**
     * 通过融云发送单聊消息
     */
    public ResponseResult sendPrivateMsg(String senderId, String[] targetId, Integer isIncludeSender,BaseMessage baseMessage) throws Exception {
        PrivateMessage privateMessage = new PrivateMessage()
                .setSenderId(senderId)
                .setTargetId(targetId)
                .setObjectName(baseMessage.getType())
                .setContent(baseMessage)
                .setPushContent("")
                .setPushData("")
                .setCount("")
                .setVerifyBlacklist(0)
                .setIsPersisted(1)
                .setIsCounted(0)
                .setIsIncludeSender(isIncludeSender);
        //发送单聊方法
        ResponseResult privateResult = Private.send(privateMessage);
        System.out.println("send private message:  " + privateResult.toString());
        return privateResult;
    }

    /**
     * 融云系统推送
     * @param content
     * @param fromUserId
     * @param toUserId
     * @param objectName
     * @param pushContent
     * @param pushData
     */
    public static void pushSystemMessage(String content, String fromUserId,
                                         String toUserId, String objectName, String pushContent,
                                         String pushData) {

        String systemMessage = "https://api.cn.rong.io/message/system/publish.json";
        Map<String, String> params = new HashMap<String, String>();
        // String content="{\"content\":\"2\"}";
        params.put("content", content);
        params.put("fromUserId", fromUserId);
        params.put("toUserId", toUserId);
        params.put("objectName", objectName);
        params.put("pushContent", pushContent);
        params.put("pushData", pushData);
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(systemMessage, params, "UTF-8",
                    20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("发送信息出错了");
        }

    }

    /**
     * 移除黑名单
     */
    public void removeBlackList(String userId,String friendId) throws Exception {
        UserModel user = getUserModel(userId,friendId);
        rongCloud.user.blackList.remove(user);

    }
    /**
     * 添加黑名单
     */
    public void addBlackList(String userId,String friendId) throws Exception {
        UserModel user = getUserModel(userId,friendId);
        rongCloud.user.blackList.add(user);

    }

    private  UserModel getUserModel(String userId,String friendId){
        UserModel blackUser = new UserModel().setId(userId);
        UserModel[] blacklist = {blackUser};
        UserModel user = new UserModel()
                .setId(friendId)
                .setBlacklist(blacklist);
        return user;
    }

    public String refreshHeadImg(String userId,String userName,String headImgUrl) throws Exception {
        User User = rongCloud.user;
        UserModel user = new UserModel()
                .setId(userId)
                .setName(userName)
                .setPortrait(headImgUrl);

        /**
         *
         * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/user/user.html#refresh
         *
         * 刷新用户信息方法
         */
        Result refreshResult = User.update(user);
        System.out.println("refresh:  " + refreshResult.toString());
        return refreshResult.toString();
    }
}



