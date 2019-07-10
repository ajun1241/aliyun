package com.modcreater.tmbiz;

//import com.modcreater.tmbeans.pojo.TestSingEvent;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.pojo.Informationsafety;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbiz.config.EventUtil;
import com.modcreater.tmbiz.config.aop.InfoSafety;
import com.modcreater.tmdao.mapper.BackerMapper;
import com.modcreater.tmdao.mapper.EventMapper;
import com.modcreater.tmutils.RongCloudUtil;
import com.modcreater.tmutils.SingleEventUtil;
import io.rong.RongCloud;
import io.rong.methods.message.history.History;
import io.rong.models.response.HistoryMessageResult;
import io.rong.models.response.ResponseResult;
import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TmbizApplicationTests {


    @Resource
    private EventMapper eventMapper;

    @Resource
    private EventUtil eventUtil;

    @Resource
    private BackerMapper backerMapper;

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

    public static void main(String[] args) {
        String[] s={"0", "1", "2", "3", "4", "5", "6", "7"};
        int a=3;
        int b=s.length%a==0?s.length/a:s.length/a+1;
        List<String[]> list=new ArrayList();
        for (int i = 0; i <b; i++) {
            String[] x=new String[a];
            for (int j = 0; j < a; j++) {
                int t=j+i*a;
                if (t>s.length-1){
                    break;
                }
                x[j]=s[t];
            }
            list.add(x);
        }
        for (String[] m:list) {
            System.out.println(Arrays.toString(m));
        }
    }

    @Test
    public void test1() {
        /*Informationsafety informationSafety=new Informationsafety();
        Map<String,Object> map=new HashMap<>();
        map.put("clientHardwareInfo","华为mete200");
        map.put("id","100033");
        map.put("shanchu1","13");
        String m=JSON.toJSONString(map);
        informationSafety= JSONObject.parseObject(m,Informationsafety.class);
        System.out.println(informationSafety.toString());*/
    }

    /**
     *
     * {"platform":["ios","android"],
     * "audience":{"tag":["女","年轻"],"tag_or":["北京","上海"],"userid":["123","456"],"is_to_all":false},
     * "notification":{"alert":"this is a push","ios": {"title": "标题","alert": "override alert","extras": {"id": "1","name": "2"}},"android": {"alert": "override alert","extras": {"id": "1","name": "2"}}}}
     *
     *  向指定用户发送
     * {"platform":["ios","android"],"audience":{"userid":["123","456"],"is_to_all":false},"notification":{"alert":"this is a push"}}
     */
    @Test
    public void test2() {

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
        /*String o="{\"address\":\"湖州\",\"day\":18,\"endtime\":1440,\"eventid\":1560855712,\"eventname\":\"就算不上班\",\"flag\":1,\"isOverdue\":0,\"level\":2,\"month\":6,\"person\":\"{\\\"friendsId\\\":\\\"100089\\\",\\\"others\\\":\\\"\\\"}\",\"remarks\":\"\",\"remindTime\":30,\"repeaTtime\":[false,false,false,false,false,false,false],\"starttime\":1146,\"type\":0,\"userid\":0,\"year\":2019}";
        String n="{\"address\":\"解开了\",\"day\":18,\"endtime\":1440,\"eventid\":1560855712,\"eventname\":\"就班\",\"flag\":1,\"isOverdue\":0,\"level\":5,\"month\":6,\"person\":\"{\\\"friendsId\\\":\\\"100089,100030\\\",\\\"others\\\":\\\"\\\"}\",\"remarks\":\"\",\"remindTime\":30,\"repeaTtime\":[false,false,false,false,false,false,false],\"starttime\":1146,\"type\":0,\"userid\":0,\"year\":2019}";
        SingleEvent singleEvent1=JSONObject.parseObject(o,SingleEvent.class);
        SingleEvent singleEvent2=JSONObject.parseObject(n,SingleEvent.class);
        List list=SingleEventUtil.eventDifferent(SingleEvent.toMap(singleEvent2),SingleEvent.toMap(singleEvent1));
        logger.info("差异数据"+list);*/
    }

    @Test
    public void test5() throws Exception {
        /*RongCloud rongCloud = RongCloud.getInstance("0vnjpoad0314z", "0uoZVUDt8lROGb");
        History history = rongCloud.message.history;
        ResponseResult removeHistoryMessageResult = history.remove("2018030210");
        System.out.println("remove history  message:  " + removeHistoryMessageResult.toString());*/
        /*
        MsgSystem system = rongCloud.message.system;
        User User = rongCloud.user;

        UserModel user = new UserModel()
                .setId("100000")
                .setName("智袖")
                .setPortrait("https://mdxc2019-1258779334.cos.ap-chengdu.myqcloud.com/icon/icon.png");
        TokenResult result = User.register(user);
        MyToken myToken= JSONObject.parseObject(result.toString(),MyToken.class);
        System.out.println("getToken:  " + result.toString());
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
