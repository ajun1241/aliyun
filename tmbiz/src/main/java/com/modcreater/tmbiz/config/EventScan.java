package com.modcreater.tmbiz.config;

import com.modcreater.tmdao.mapper.EventMapper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-31
 * Time: 16:42
 */
@Component
@EnableScheduling
public class EventScan {

    @Resource
    private EventMapper eventMapper;

    @Scheduled(cron = "0 0 17 * * ?")
    public void eventScan(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String time = simpleDateFormat.format(new Date());
        StringBuffer stringBuffer = new StringBuffer(time);
        String year = stringBuffer.substring(0,4);
        String month = stringBuffer.substring(4,6);
        String day = stringBuffer.substring(6,8);
        Map<String,Object> map = new HashMap<>();
        map.put("year",year);
        map.put("month",month);
        map.put("day",day);
        map.put("time",240);
//        eventMapper.queryEventsForScan();
    }

}
