package com.modcreater.tmutils;

import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.show.MyToken;
import io.rong.RongCloud;
import io.rong.messages.BaseMessage;
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
import org.springframework.util.ObjectUtils;


/**
 * @Author: AJun
 * 生成token的工具类
 */

public class RongCloudMethodUtil {

    public final static String appKey = "0vnjpoad03rzz";// 申请的融云key
    public final static String appSecret = "BbTOtrRIF5MOA";// 申请的的云secret

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
    public ResponseResult sendSystemMessage(String userId,String friendId,String content,String pushContent,String pushData,String extra) throws Exception {

        TxtMessage txtMessage = new TxtMessage(content, extra);
        String[] targetIds = {friendId};
        SystemMessage systemMessage = new SystemMessage()
                .setSenderId(userId)
                .setTargetId(targetIds)
                .setObjectName(txtMessage.getType())
                .setContent(txtMessage)
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
    public ResponseResult sendPrivateMsg(String senderId, String targetId, BaseMessage baseMessage) throws Exception {
        PrivateMessage privateMessage = new PrivateMessage()
                .setSenderId(senderId)
                .setTargetId(new String[]{targetId})
                .setObjectName(baseMessage.getType())
                .setContent(baseMessage)
                .setPushContent("")
                .setPushData("")
                .setCount("")
                .setVerifyBlacklist(0)
                .setIsPersisted(0)
                .setIsCounted(0)
                .setIsIncludeSender(0);
        //发送单聊方法
        ResponseResult privateResult = Private.send(privateMessage);
        System.out.println("send private message:  " + privateResult.toString());
        return privateResult;
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
}



