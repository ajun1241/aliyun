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
import com.modcreater.tmutils.messageutil.InviteMessage;
import com.modcreater.tmutils.messageutil.UpdateInviteMessage;
import io.rong.RongCloud;
import io.rong.methods.message._private.Private;
import io.rong.methods.message.chatroom.Chatroom;
import io.rong.methods.message.discussion.Discussion;
import io.rong.methods.message.group.Group;
import io.rong.methods.message.history.History;
import io.rong.methods.message.system.MsgSystem;
import io.rong.models.message.PrivateMessage;
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
       /* SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        LoggerFactory.getLogger(TmbizApplicationTests.class).info("时间："+simpleDateFormat.format(new Date()));*/
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
        /*String s="[1,2]".replace("[","").replace("]","");
        System.out.println(Arrays.toString(s.split(",")));*/
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
        String n="{\"address\":\"解开了\",\"day\":2,\"endtime\":1480,\"eventid\":1560855712,\"eventname\":\"就班\",\"flag\":1,\"isOverdue\":0,\"level\":5,\"month\":6,\"person\":\"{\\\"friendsId\\\":\\\"100089,100030\\\",\\\"others\\\":\\\"\\\"}\",\"remarks\":\"\",\"remindTime\":30,\"repeaTtime\":[false,false,false,false,false,false,false],\"starttime\":1146,\"type\":0,\"userid\":0,\"year\":2219}";
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
        Private Private = rongCloud.message.msgPrivate;
        String[] targetIds = {"100148"};
        InviteMessage inviteMessage = new InviteMessage("这是一条测试消息","2019/6/6","","","");*/
//        TxtMessage txtMessage=new TxtMessage("猜猜我是谁","");
        /*List<Map<String,String>> list=new ArrayList<>();
        Map<String,String> map1=new HashMap<>();
        map1.put("title","名称更改为：");
        map1.put("content","士大夫打爱上帝");
        Map<String,String> map2=new HashMap<>();
        map2.put("title","地址更改为：");
        map2.put("content","是单独发给");
        Map<String,String> map3=new HashMap<>();
        map3.put("title","优先级更改为：");
        map3.put("content","不紧迫也不重要");
        Map<String,String> map4=new HashMap<>();
        map4.put("title","日期更改为：");
        map4.put("content","2019年7月12日");
        Map<String,String> map5=new HashMap<>();
        map5.put("title","开始时间更改为：");
        map5.put("content","13：45");
        Map<String,String> map6=new HashMap<>();
        map6.put("title","结束时间更改为：");
        map6.put("content","16：45");
        Map<String,String> map7=new HashMap<>();
        map7.put("title","重复时间更改为：");
        map7.put("content","每周5重复");
        Map<String,String> map8=new HashMap<>();
        map8.put("title","提醒时间更改为：");
        map8.put("content","开始前10分钟");
        Map<String,String> map9=new HashMap<>();
        map9.put("title","类型更改为：");
        map9.put("content","学习");
        Map<String,String> map10=new HashMap<>();
        map10.put("title","备注更改为：");
        map10.put("content","今晚打老虎");
        list.add(map1);
        list.add(map2);
        list.add(map3);
        list.add(map4);
        list.add(map5);
        list.add(map6);
        list.add(map7);
        list.add(map8);
        list.add(map9);
        list.add(map10);
        UpdateInviteMessage updateInviteMessage=new UpdateInviteMessage("1","测试数据",String.valueOf(list.size()),"2",list,"233","1");

        MsgSystem system = rongCloud.message.system;
        Group group = rongCloud.message.group;
        Chatroom chatroom = rongCloud.message.chatroom;
        Discussion discussion = rongCloud.message.discussion;
        History history = rongCloud.message.history;*/
       /**
         * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/message/private.html#send
         *
         * 发送单聊消息
         * */

        /*PrivateMessage privateMessage = new PrivateMessage()
                .setSenderId("100000")
                .setTargetId(targetIds)
                .setObjectName(inviteMessage.getType())
                .setContent(inviteMessage)
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
