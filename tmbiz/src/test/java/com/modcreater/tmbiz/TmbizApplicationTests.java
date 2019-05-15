package com.modcreater.tmbiz;

//import com.modcreater.tmbeans.pojo.TestSingEvent;
import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.vo.DayEvents;
import com.modcreater.tmbeans.vo.UploadingEventVo;
import com.modcreater.tmdao.mapper.EventMapper;
import com.modcreater.tmutils.DateUtil;
import com.modcreater.tmutils.SingleEventUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TmbizApplicationTests {

    @Resource
    private EventMapper eventMapper;

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
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        System.out.println(week);
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

}
