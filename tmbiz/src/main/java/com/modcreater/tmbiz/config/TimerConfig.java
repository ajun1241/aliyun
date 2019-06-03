package com.modcreater.tmbiz.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/31 13:58
 */
//@Component注解用于对那些比较中立的类进行注释；
//相对与在持久层、业务层和控制层分别采用 @Repository、@Service 和 @Controller 对分层中的类进行注释
@Component
@EnableScheduling   // 1.开启定时任务
@EnableAsync        // 2.开启多线程
public class TimerConfig {

    private String content;
    private String userId;
    private String friendId;
    private String date;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Scheduled(fixedDelay = 1000)  //间隔1秒
    public String setTimer() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        Date date2;
        if (StringUtils.isEmpty(getDate())){
            date2 = simpleDateFormat.parse("1970-01-01 00-00-00");
        }else {
            date2 = simpleDateFormat.parse(getDate());
        }
        System.out.println("*********************");
        System.out.println(simpleDateFormat.format(date2));
        System.out.println("*********************");
        Date date1 = new Date();
        if (date2.getTime() / 1000 == date1.getTime() / 1000) {
            System.out.println();
            System.out.println("发消息"+getContent()+"给融云");
            System.out.println();
            return null;
        }
        System.out.println("第一个定时任务开始 : " + LocalDateTime.now().toLocalTime() + "\r\n线程 : " + Thread.currentThread().getName());
        return null;
    }

}