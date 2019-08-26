package com.modcreater.tmutils.mobserver;

import mob.push.api.MobPushConfig;
import mob.push.api.exception.ApiException;
import mob.push.api.model.PushWork;
import mob.push.api.push.PushClient;
import mob.push.api.utils.AndroidNotifyStyleEnum;
import mob.push.api.utils.PlatEnum;
import mob.push.api.utils.PushTypeEnum;
import mob.push.api.utils.TargetEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/8/19 10:08
 */
public class MobPushUtils {

    private static Logger logger= LoggerFactory.getLogger(MobPushUtils.class);

    static {
        MobPushConfig.appkey = "2b67cc0a960ba";
        MobPushConfig.appSecret = "e3ea57c5b32f5821324cd25c3f2eaa3d";
    }

    public static void pushTask(String content,String[] registrationIds) {
        logger.info("deviceToken："+ Arrays.toString(registrationIds));
        PushWork push = new PushWork(PlatEnum.all.getCode(),content , PushTypeEnum.notify.getCode()) //初始化基础信息
                .buildTarget(TargetEnum._4.getCode(), null, null, registrationIds, null, null)  // 设置推送范围
                .buildAndroid("Android Title", AndroidNotifyStyleEnum.normal.getCode(), null, true, true, true) //定制android样式
                .bulidIos("ios Title", "ios Subtitle", null, 1, null, null, null, null) //定制ios设置
                .buildExtra(1, "{\"key1\":\"value\"}", 1) // 设置扩展信息
                ;
        PushClient client = new PushClient();
        try {
            client.sendPush(push);
        } catch (ApiException e) {
            //错误请求状态码
            e.getStatus();
            //错误状态码
            e.getErrorCode();
            //错误信息
            e.getErrorMessage();
            logger.error(e.getErrorMessage(),e);
        }
        logger.info("success,推送成功");
    }
}
