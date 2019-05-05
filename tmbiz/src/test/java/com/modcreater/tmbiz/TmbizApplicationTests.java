package com.modcreater.tmbiz;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TmbizApplicationTests {

    @Test
    public void contextLoads() throws Exception{
        Calendar calendar = Calendar.getInstance();
        Long timeStamp = (calendar.getTimeInMillis())/1000;
        Date time = new Date(timeStamp);
        System.out.println(time);
        System.out.println(timeStamp);
    }

    @Test
    public void test1(){
        StringBuilder stringBuilder = new StringBuilder("20190506");
        System.out.println(stringBuilder.substring(6,8));
    }

}
