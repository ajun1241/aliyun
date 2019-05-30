package com.modcreater.tmbiz;

//import com.modcreater.tmbeans.pojo.TestSingEvent;
import com.alibaba.fastjson.JSON;
import com.modcreater.tmbeans.vo.eventvo.UploadingEventVo;
import com.modcreater.tmdao.mapper.EventMapper;
import com.modcreater.tmutils.DateUtil;
import io.rong.RongCloud;
import io.rong.messages.ContactNtfMessage;
import io.rong.messages.TxtMessage;
import io.rong.messages.VoiceMessage;
import io.rong.methods.message._private.Private;
import io.rong.methods.message.chatroom.Chatroom;
import io.rong.methods.message.discussion.Discussion;
import io.rong.methods.message.group.Group;
import io.rong.methods.message.history.History;
import io.rong.methods.message.system.MsgSystem;
import io.rong.models.message.PrivateMessage;
import io.rong.models.response.ResponseResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Array;
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
        String[] s ={"1","2","3","4","5"};
        String[] x=new String[s.length+1];
        for (int i = 0; i <x.length-1 ; i++) {
            x[i]=s[i];
        }
        x[x.length-1]="6";
        System.out.println(Arrays.toString(x));
    }

    @Test
    public void test3() {
        Map<String,Object> m1=new HashMap<>();
        m1.put("1","1");
        m1.put("2","2");
        m1.put("3","asd");
        m1.put("4","gd");
        m1.put("5","dfg");
        //原来的信息
        Map<String,Object> m2=new HashMap<>();
        m2.put("1","1");
        m2.put("2","2");
        m2.put("3","3");
        m2.put("4","4");
        m2.put("5","5");
        //比较差异
        StringBuffer different=new StringBuffer();
        for (String key: m1.keySet()) {
            if (!m1.get(key).equals(m2.get(key))){
                different.append(key+"更改为"+m1.get(key)+";");
            }
        }
        System.out.println(different.replace(different.length()-1,different.length(),"."));
    }
    @Test
    public void test4() {
        StringBuffer stringBuffer=new StringBuffer();

        System.out.println(StringUtils.isEmpty(stringBuffer.toString()));
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
        String[] targetIds = {"100033"};
//        String operation, String extra, String sourceUserId, String targetUserId, String message

        ContactNtfMessage voiceMessage = new ContactNtfMessage("","","100023","100033", "花花说的很好打啊实打实大大大");
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
