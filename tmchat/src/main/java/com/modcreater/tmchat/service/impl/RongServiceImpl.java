package com.modcreater.tmchat.service.impl;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.uo.RongUo;
import com.modcreater.tmbeans.vo.rongVo.UserVo;
import com.modcreater.tmchat.service.RongService;
import com.modcreater.tmutils.DtoUtil;
import io.rong.RongCloud;
import io.rong.methods.user.User;
import io.rong.models.Result;
import io.rong.models.response.TokenResult;
import io.rong.models.user.UserModel;
import org.springframework.stereotype.Service;

@Service
public class RongServiceImpl implements RongService {
    @Override
    public Dto rongRegister(UserVo userVo) {
        TokenResult result = null;
        try {
            RongCloud rongCloud = RongCloud.getInstance(RongUo.appKey, RongUo.appSecret);
            //自定义 api 地址方式
            // RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret,api);
            User User = rongCloud.user;

            /**
             * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/user/user.html#register
             *
             * 注册用户，生成用户在融云的唯一身份标识 Token
             */
            UserModel user = new UserModel()
                    .setId(userVo.getId())
                    .setName(userVo.getName())
                    .setPortrait(userVo.getPortrait());


            result = User.register(user);

            System.out.println("getToken:  " + result.toString());

            /**
             *
             * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/user/user.html#refresh
             *
             * 刷新用户信息方法
             */
            Result refreshResult = User.update(user);
            System.out.println("refresh:  " + refreshResult.toString());
            } catch (Exception e) {
            e.printStackTrace();
        }
        return DtoUtil.getSuccesWithDataDto("获取token成功",result,30001);
    }
}
