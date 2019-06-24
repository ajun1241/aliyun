package com.modcreater.tmbiz;

//import com.modcreater.tmbeans.pojo.TestSingEvent;
import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.dto.EventPersons;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.show.MyToken;
import com.modcreater.tmbiz.config.EventUtil;
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
        /*stringRedisTemplate.opsForValue().set("2","3");*/
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
        System.out.println(result2.toArray().toString());
        System.out.println("******************************************");
        System.out.println(result3);

    }

    @Test
    public void test5() throws Exception {
        RongCloud rongCloud = RongCloud.getInstance("0vnjpoad0314z", "0uoZVUDt8lROGb");
        MsgSystem system = rongCloud.message.system;
        User User = rongCloud.user;

        UserModel user = new UserModel()
                .setId("100000")
                .setName("智袖")
                .setPortrait("https://mdxc2019-1258779334.cos.ap-chengdu.myqcloud.com/icon/icon.png");
        TokenResult result = User.register(user);
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
    }

    @Test
    public void test() throws Exception {

    }

}
