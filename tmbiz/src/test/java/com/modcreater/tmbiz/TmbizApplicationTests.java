package com.modcreater.tmbiz;

import com.modcreater.tmbeans.pojo.SingleEvent;
//import com.modcreater.tmbeans.pojo.TestSingEvent;
import com.modcreater.tmbeans.vo.UploadingEventVo;
import com.modcreater.tmutils.SingleEventUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TmbizApplicationTests {

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
        UploadingEventVo uploadingEventVo = new UploadingEventVo();
      /*  uploadingEventVo.setEventid(100L);
        uploadingEventVo.setUserid(10086L);
        uploadingEventVo.setEventname("测试名称");
        uploadingEventVo.setStarttime("1557109312");
        uploadingEventVo.setEndtime("1557109312");
        uploadingEventVo.setAddress("测试地址");
        uploadingEventVo.setLevel(1L);
        uploadingEventVo.setFlag(1L);
        uploadingEventVo.setPerson("测试人物");
        uploadingEventVo.setRemarks("测试备注");
        uploadingEventVo.setRepeaTtime("2");
        uploadingEventVo.setIsOverdue(0L);
        uploadingEventVo.setRemindTime("1557109312");
        uploadingEventVo.setDay(5L);
        uploadingEventVo.setMonth(5L);
        uploadingEventVo.setYear(2019L);
        uploadingEventVo.setType(1L);*/
//        System.out.println(SingleEventUtil.getSingleEvent(uploadingEventVo));
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

}
