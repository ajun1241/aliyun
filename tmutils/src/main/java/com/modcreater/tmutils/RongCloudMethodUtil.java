package com.modcreater.tmutils;

import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.show.MyToken;
import io.rong.RongCloud;
import io.rong.methods.user.User;
import io.rong.models.Result;
import io.rong.models.response.TokenResult;
import io.rong.models.user.UserModel;
import org.springframework.util.ObjectUtils;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import static com.modcreater.tmutils.RongCloudUtil.appKey;
import static com.modcreater.tmutils.RongCloudUtil.appSecret;

/**
 * 生成token的工具类
 */
public class RongCloudMethodUtil {

    public String createToken(String userId,String userName,String headImgUrl) throws Exception {

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
     * 推送系统信息
     *
     * @param content 消息内容
     * @param fromUserId 1
     * @param toUserId  userId
     * @param objectName  RC:TxtMsg
     * @param pushContent 消息标题
     * @param pushData 空-安卓  非空：苹果
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

    public static void pushFriendRequest(){
        String systemMessage="https://api-cn.ronghub.com/push.json";
    }
}
