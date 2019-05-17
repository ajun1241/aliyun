package com.modcreater.tmutils;

import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.show.MyToken;
import io.rong.RongCloud;
import io.rong.methods.user.User;
import io.rong.models.Result;
import io.rong.models.response.TokenResult;
import io.rong.models.user.UserModel;
import org.springframework.util.ObjectUtils;

/**
 * 生成token的工具类
 */
public class RongCloudUtil {
    /**
     * 此处替换成您的appKey
     * */
    private static final String appKey = "0vnjpoad03rzz";
    /**
     * 此处替换成您的appSecret
     * */
    private static final String appSecret = "BbTOtrRIF5MOA";

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
}
