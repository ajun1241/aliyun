package com.modcreater.tmbiz;

//import com.modcreater.tmbeans.pojo.TestSingEvent;
import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.vo.DayEvents;
import com.modcreater.tmbeans.vo.UploadingEventVo;
import com.modcreater.tmdao.mapper.EventMapper;
        import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
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

    @Test
    public void test5(){
        String a="[{\"dayEventId\":20190510,\"mySingleEventList\":[{\"address\":\"积极性\",\"day\":10,\"endtime\":1151,\"eventid\":1557468410,\"eventname\":\"普通事件\",\"flag\":1,\"isOverdue\":0,\"level\":5,\"month\":5,\"person\":\"我\",\"remarks\":\"\",\"remindTime\":10,\"repeaTtime\":[false,false,false,false,false,false,false],\"starttime\":971,\"type\":4,\"userid\":0,\"year\":2019}],\"totalNum\":1,\"userId\":100035},{\"dayEventId\":20190511,\"mySingleEventList\":[],\"totalNum\":0,\"userId\":100035},{\"dayEventId\":20190512,\"mySingleEventList\":[],\"totalNum\":0,\"userId\":100035},{\"dayEventId\":20190513,\"mySingleEventList\":[],\"totalNum\":0,\"userId\":100035},{\"dayEventId\":20190514,\"mySingleEventList\":[],\"totalNum\":0,\"userId\":100035},{\"dayEventId\":20190515,\"mySingleEventList\":[],\"totalNum\":0,\"userId\":100035},{\"dayEventId\":20190516,\"mySingleEventList\":[],\"totalNum\":0,\"userId\":100035}]";
        ArrayList arrayList= (ArrayList) JSONObject.parseArray(a,ArrayList.class);
        for (Object dayEvents:arrayList) {
            DayEvents days= JSONObject.parseObject(dayEvents.toString(),DayEvents.class);
           /* System.out.println("1"+dayEvents.toString());
            System.out.println("2"+dayEvents.getMySingleEventList().toString());*/
        }
    }

}
