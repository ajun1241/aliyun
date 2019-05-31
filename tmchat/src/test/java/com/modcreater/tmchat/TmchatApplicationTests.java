package com.modcreater.tmchat;

import com.modcreater.tmutils.RongCloudMethodUtil;
import io.rong.RongCloud;
import io.rong.messages.VoiceMessage;
import io.rong.methods.message._private.Private;
import io.rong.methods.user.User;
import io.rong.models.Result;
import io.rong.models.message.PrivateMessage;
import io.rong.models.response.ResponseResult;
import io.rong.models.response.TokenResult;
import io.rong.models.user.UserModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.junit4.SpringRunner;

import static com.modcreater.tmutils.RongCloudMethodUtil.appKey;
import static com.modcreater.tmutils.RongCloudMethodUtil.appSecret;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TmchatApplicationTests {


    @Test
    @Scheduled(cron = "*/5 * * * * ?")
    public void contextLoads() throws Exception {
        System.out.println("定时器工作了");
    }


    @Test
    public void test1(){
        VoiceMessage voiceMessage = new VoiceMessage("hello", "helloExtra", 20L);
        Private Private=new Private("0vnjpoad03rzz", "BbTOtrRIF5MOA");
        String[] targetId={"100024"};
        PrivateMessage privateMessage = new PrivateMessage()
                .setSenderId("100023")
                .setTargetId(targetId)
                .setObjectName(voiceMessage.getType())
                .setContent(voiceMessage)
                .setPushContent("")
                .setPushData("{\"pushData\":\"hello\"}")
                .setCount("4")
                .setVerifyBlacklist(0)
                .setIsPersisted(0)
                .setIsCounted(0)
                .setIsIncludeSender(0);
        ResponseResult privateResult = null;
        try {
            privateResult = Private.send(privateMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("send private message:  " + privateResult.toString());
    }
    @Test
    public void test2() throws Exception {




    }
}
