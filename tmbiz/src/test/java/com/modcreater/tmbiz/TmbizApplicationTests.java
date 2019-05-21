package com.modcreater.tmbiz;

//import com.modcreater.tmbeans.pojo.TestSingEvent;
import com.modcreater.tmbeans.vo.eventvo.UploadingEventVo;
import com.modcreater.tmdao.mapper.EventMapper;
import com.modcreater.tmutils.DateUtil;
import io.rong.RongCloud;
import io.rong.messages.VoiceMessage;
import io.rong.methods.message._private.Private;
import io.rong.methods.message.chatroom.Chatroom;
import io.rong.methods.message.discussion.Discussion;
import io.rong.methods.message.group.Group;
import io.rong.methods.message.history.History;
import io.rong.methods.message.system.MsgSystem;
import io.rong.models.message.PrivateMessage;
import io.rong.models.response.ResponseResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TmbizApplicationTests {

    @Resource
    private EventMapper eventMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void contextLoads() throws Exception {
        /*Calendar calendar = Calendar.getInstance();
        Long timeStamp = (calendar.getTimeInMillis()) / 1000;
        Date time = new Date(timeStamp);
        System.out.println(time);
        System.out.println(timeStamp);*/
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = simpleDateFormat.parse("20190511");
        calendar.setTime(date);
        int week = calendar.get(Calendar.DAY_OF_WEEK)-1;
        if (week < 0){
            week = 0;
        }
        System.out.println(week);
    }

    @Test
    public void test1() {
        StringBuilder stringBuilder = new StringBuilder("20190506");
        System.out.println(stringBuilder.substring(6, 8));
    }

    @Test
    public void test2() {
        String s = "20190512";
        System.out.println(DateUtil.stringToWeek(s));
    }

    @Test
    public void test3() {/*
        TestSingEvent testSingEvent = new TestSingEvent();
        SingleEvent singleEvent = new SingleEvent();
        singleEvent.setUserid(11111L);
        testSingEvent.setSingleEvent(singleEvent);
        System.out.println(testSingEvent.getSingleEvent().getUserid());*/
    }
    @Test
    public void test4() {
        UploadingEventVo uploadingEventVo=new UploadingEventVo();
        uploadingEventVo.setUserId("10019");
//        System.out.println(SingleEventUtil.checkLogin(uploadingEventVo).toString());
    }

    @Test
    public void test5(){
        String repeatTime="[false,true,true,true,true,false,false]";
        String x=repeatTime.substring(1,(repeatTime.length()-1));
        System.out.println(x);
    }

    @Test
    public void test() throws Exception {
        RongCloud rongCloud = RongCloud.getInstance("0vnjpoad03rzz", "BbTOtrRIF5MOA");
        //自定义 api 地址方式
        //RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret,api);

        VoiceMessage voiceMessage = new VoiceMessage("花花说的很好打啊实打实大大大", "helloExtra", 20L);
        Private Private = rongCloud.message.msgPrivate;
        MsgSystem system = rongCloud.message.system;
        Group group = rongCloud.message.group;
        Chatroom chatroom = rongCloud.message.chatroom;
        Discussion discussion = rongCloud.message.discussion;
        History history = rongCloud.message.history;
        /**
         * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/message/private.html#send
         *
         * 发送单聊消息
         * */
        String[] targetIds = {"100033"};
        PrivateMessage privateMessage = new PrivateMessage()
                .setSenderId("100023")
                .setTargetId(targetIds)
                .setObjectName(voiceMessage.getType())
                .setContent(voiceMessage)
                .setPushContent("")
                .setPushData("{\"pushData\":\"hello\"}")
                .setCount("4")
                .setVerifyBlacklist(0)
                .setIsPersisted(0)
                .setIsCounted(0)
                .setIsIncludeSender(0);
        ResponseResult privateResult = Private.send(privateMessage);
        System.out.println("send private message:  " + privateResult.toString());
    }

}
