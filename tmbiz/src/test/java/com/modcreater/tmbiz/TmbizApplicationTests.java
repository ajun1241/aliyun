package com.modcreater.tmbiz;

//import com.modcreater.tmbeans.pojo.TestSingEvent;
import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.dto.EventPersons;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.show.MyToken;
import com.modcreater.tmbiz.config.EventUtil;
import com.modcreater.tmbiz.service.impl.EventServiceImpl;
import com.modcreater.tmdao.mapper.EventMapper;
import com.modcreater.tmutils.messageutil.InviteMessage;
import com.modcreater.tmutils.messageutil.NotifyMessage;
import io.rong.RongCloud;
import io.rong.messages.TxtMessage;
import io.rong.methods.message._private.Private;
import io.rong.methods.message.system.MsgSystem;
import io.rong.methods.user.User;
import io.rong.models.Result;
import io.rong.models.message.PrivateMessage;
import io.rong.models.response.ResponseResult;
import io.rong.models.response.TokenResult;
import io.rong.models.user.UserModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TmbizApplicationTests {


    @Resource
    private EventMapper eventMapper;

    @Resource
    private EventUtil eventUtil;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    private Logger logger = LoggerFactory.getLogger(TmbizApplicationTests.class);
    @Test
    public void contextLoads() throws Exception {
        /*Calendar calendar = Calendar.getInstance();
        Long timeStamp = (calendar.getTimeInMillis()) / 1000;
        Date time = new Date(timeStamp);
        System.out.println(time);
        System.out.println(timeStamp);*/
        /*Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = simpleDateFormat.parse("20190511");
        calendar.setTime(date);
        int week = calendar.get(Calendar.DAY_OF_WEEK)-1;
        if (week < 0){
            week = 0;
        }
        System.out.println(week);*/
    }

    @Test
    public void test1() {
        /*try {
            ArrayList<String> personList1 = new ArrayList<>();
            personList1.add("1");
            personList1.add("2");
            personList1.add("3");
            personList1.add("4");
            personList1.add("5");
            logger.debug("圣埃蒂安");
            for (int i = 0; i < personList1.size()+1; i++) {
                System.out.println(personList1.get(i));

            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            System.out.println(e.getMessage());
        }*/
    }

    @Test
    public void test2() {
        List list1=new ArrayList();
        list1.add("1");
        list1.add("2");
        list1.add("3");
        List list2=new ArrayList();
        list2.add("4");
        list2.add("2");
        list2.add("6");
        list1.addAll(list2);
        System.out.println(Arrays.toString(list1.toArray(new String[list1.size()])));
    }

    @Test
    public void test3() {
        /*Map<String,Object> m1=new HashMap<>();
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
        System.out.println(different.replace(different.length()-1,different.length(),"."));*/
    }
    @Test
    public void test4() {
        /*String[] as = new String[]{"0","1","2","3"};
        String[] bs = new String[]{"1","2","5"};
        List<String> list1 = Arrays.asList(bs);
        List<String> list2 = Arrays.asList(as);
        //用来装差集
        List<String> result1 = new ArrayList<String>();
        List<String> result2 = new ArrayList<String>();
        List<String> result3 = new ArrayList<String>();
        for(String a : as){
            //判断是否包含
            if(!list1.contains(a)){
                result1.add(a);
            }else {
                result3.add(a);
            }
        }
        for(String b : bs){
            //判断是否包含
            if(!list2.contains(b)){
                result2.add(b);
            }
        }
        System.out.println(Arrays.toString(result1.toArray()));
        System.out.println("******************************************");
        System.out.println(result2.toArray().toString());
        System.out.println("******************************************");
        System.out.println(result3);*/

    }

    @Test
    public void test5() throws Exception {
        /*RongCloud rongCloud = RongCloud.getInstance("0vnjpoad0314z", "0uoZVUDt8lROGb");
        MsgSystem system = rongCloud.message.system;
        User User = rongCloud.user;

        UserModel user = new UserModel()
                .setId("100000")
                .setName("智袖")
                .setPortrait("https://mdxc2019-1258779334.cos.ap-chengdu.myqcloud.com/icon/icon.png");
        TokenResult result = User.register(user);
        MyToken myToken= JSONObject.parseObject(result.toString(),MyToken.class);
        System.out.println("getToken:  " + result.toString());

        *//**
         *
         * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/user/user.html#refresh
         *
         * 刷新用户信息方法
         *//*
        Result refreshResult = User.update(user);
        System.out.println("refresh:  " + refreshResult.toString());*/
    }

    @Test
    public void test() throws Exception {
        /*RongCloud rongCloud = RongCloud.getInstance("0vnjpoad0314z", "0uoZVUDt8lROGb");
        String[] targetIds = {"100033"};
//        InviteMessage inviteMessage = new InviteMessage("这是一条测试消息","2019/6/6","","","");
//        TxtMessage txtMessage=new TxtMessage("猜猜我是谁","");
        NotifyMessage notifyMessage=new NotifyMessage("jkasfjj艰苦撒旦弗兰克","1");
        Private Private = rongCloud.message.msgPrivate;
        *//*MsgSystem system = rongCloud.message.system;
        Group group = rongCloud.message.group;
        Chatroom chatroom = rongCloud.message.chatroom;
        Discussion discussion = rongCloud.message.discussion;
        History history = rongCloud.message.history;*//*
        *//**
         * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/message/private.html#send
         *
         * 发送单聊消息
         * *//*

        PrivateMessage privateMessage = new PrivateMessage()
                .setSenderId("100000")
                .setTargetId(targetIds)
                .setObjectName(notifyMessage.getType())
                .setContent(notifyMessage)
                .setPushContent("")
                .setPushData("{\"pushData\":\"hello\"}")
                .setCount("4")
                .setVerifyBlacklist(0)
                .setIsPersisted(0)
                .setIsCounted(0)
                .setIsIncludeSender(0);
        ResponseResult privateResult = Private.send(privateMessage);
        System.out.println("send private message:  " + privateResult.toString());*/
    }

}
