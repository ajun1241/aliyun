package com.modcreater.tmbiz;

import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.vo.UploadingEventVo;
import com.modcreater.tmbiz.service.impl.EventServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TmbizApplicationTests {

    @Test
    public void contextLoads() throws Exception {
        Calendar calendar = Calendar.getInstance();
        Long timeStamp = (calendar.getTimeInMillis()) / 1000;
        Date time = new Date(timeStamp);
        System.out.println(time);
        System.out.println(timeStamp);
    }

    @Test
    public void test1() {
        StringBuilder stringBuilder = new StringBuilder("20190506");
        System.out.println(stringBuilder.substring(6, 8));
    }

    @Test
    public void test2() {
        UploadingEventVo uploadingEventVo = new UploadingEventVo();
        uploadingEventVo.setEventid(1L);
        uploadingEventVo.setUserid(1L);
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
        uploadingEventVo.setType(1L);
        SingleEvent singleEvent = EventServiceImpl.get(uploadingEventVo);
        System.out.println(singleEvent);
    }
}
