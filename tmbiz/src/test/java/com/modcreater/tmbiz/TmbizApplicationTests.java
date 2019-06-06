package com.modcreater.tmbiz;

//import com.modcreater.tmbeans.pojo.TestSingEvent;
import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.dto.EventPersons;
import com.modcreater.tmdao.mapper.EventMapper;
import com.modcreater.tmutils.messageutil.InviteMessage;
import io.rong.RongCloud;
import io.rong.methods.message._private.Private;
import io.rong.models.message.PrivateMessage;
import io.rong.models.response.ResponseResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

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
        List<String> list=new ArrayList<>();
        System.out.println(list);
        for (String s:list) {
            System.out.println(s.split(","));
        }
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
        String[] as = new String[]{"0","1","2","3"};
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
        System.out.println(result2.toArray());
        System.out.println("******************************************");
        System.out.println(result3);

    }

    @Test
    public void test5() throws ParseException {
        String s="{\"friendsId\":\"100035,100030\",\"others\":\"薛腾\"}";
        System.out.println(s);
        EventPersons eventPersons= JSONObject.parseObject(s,EventPersons.class);
        System.out.println(eventPersons.getFriendsId());
    }

    @Test
    public void test() throws Exception {
        RongCloud rongCloud = RongCloud.getInstance("0vnjpoad03rzz", "BbTOtrRIF5MOA");
        String[] targetIds = {"100033"};
        InviteMessage inviteMessage = new InviteMessage("这是一条测试消息","2019/6/6","","");
        Private Private = rongCloud.message.msgPrivate;
        /*MsgSystem system = rongCloud.message.system;
        Group group = rongCloud.message.group;
        Chatroom chatroom = rongCloud.message.chatroom;
        Discussion discussion = rongCloud.message.discussion;
        History history = rongCloud.message.history;*/
        /**
         * API 文档: http://www.rongcloud.cn/docs/server_sdk_api/message/private.html#send
         *
         * 发送单聊消息
         * */

        PrivateMessage privateMessage = new PrivateMessage()
                .setSenderId("100023")
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
        System.out.println("send private message:  " + privateResult.toString());
    }

}
