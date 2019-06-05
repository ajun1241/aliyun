package com.modcreater.tmbiz.config;

import com.modcreater.tmbeans.databaseparam.EventStatusScan;
import com.modcreater.tmbeans.pojo.TimedTask;
import com.modcreater.tmdao.mapper.EventMapper;
import com.modcreater.tmdao.mapper.TimedTaskMapper;
import com.modcreater.tmutils.DateUtil;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmutils.RongCloudMethodUtil;
import io.rong.messages.TxtMessage;
import io.rong.models.response.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Description:
 *  EnableScheduling   1.开启定时任务
 *  EnableAsync        2.开启多线程
 * @Author: AJun
 * @Date: 2019/5/31 13:58
 */
@Component
@EnableScheduling
@EnableAsync
@Transactional(rollbackFor = Exception.class)
public class TimerConfig {

    @Resource
    private TimedTaskMapper timedTaskMapper;

    @Resource
    private EventMapper eventMapper;

    private Logger logger= LoggerFactory.getLogger(TimerConfig.class);

    @Scheduled(fixedDelay = 60000)
    public void backerEventTimer(){
        try {
            List<TimedTask> taskList=timedTaskMapper.queryWaitExecute();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            Date date1=new Date();
            Date date2;
            for (TimedTask task:taskList) {
                date2=simpleDateFormat.parse(task.getTimer());
                System.out.println(task.getTimer());
                if (date1.getTime()/60000==date2.getTime()/60000){
                    //发送消息给支持者
                    RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
                    TxtMessage txtMessage=new TxtMessage(task.getContent(),"");
                    String[] targetId={task.getBackerId().toString()};
                    ResponseResult result = rongCloudMethodUtil.sendSystemMessage(task.getUserId().toString(),targetId,txtMessage,"","");
                    if (result.getCode()!=200){
                        logger.info("发送消息失败，任务编号："+task.getId());
                    }else {
                        logger.info("发送消息成功，任务编号："+task.getId());
                    }
                    //修改任务状态
                    TimedTask timedTask=new TimedTask();
                    timedTask.setTaskStatus(1L);
                    timedTask.setUserId(task.getUserId());
                    timedTask.setEventId(task.getEventId());
                    if (timedTaskMapper.updateTimedTask(timedTask)==0){
                        logger.info("修改状态失败，任务编号："+task.getId());
                    }else {
                        logger.info("修改状态成功，任务编号："+task.getId());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("第一个定时任务开始 : " + LocalDateTime.now().toLocalTime() + "\r\n线程 : " + Thread.currentThread().getName());
    }

    /**
     * 以下为每分钟的第30s进行过期事件过滤修改
     */
    @Scheduled(cron = "30 * * * * ?")
    public void  eventStatusScan(){
        StringBuilder today = new StringBuilder(DateUtil.getDay(0));
        EventStatusScan eventStatusScan = new EventStatusScan();
        eventStatusScan.setThisYear(today.substring(0,4));
        eventStatusScan.setThisMonth(today.substring(4,6));
        eventStatusScan.setToday(today.substring(6));
        Integer time = Integer.valueOf(new SimpleDateFormat("HH").format(new Date()))*60+Integer.valueOf(new SimpleDateFormat("mm").format(new Date()));
        eventStatusScan.setTime(time.toString());
        System.out.println(eventStatusScan.toString());
        logger.info("有"+eventMapper.queryExpiredEvents(eventStatusScan)+"条事件待修改");
        logger.info("修改了"+eventMapper.updateExpiredEvents(eventStatusScan)+"条事件");
    }

    /**
     * 以下为每日凌晨4点整进行过期事件过滤修改
     */
    /*@Scheduled(cron = "0 0 4 * * ?")
    public void  eventStatusScan(){
        StringBuilder date = new StringBuilder(DateUtil.getDay(-1));
        StringBuilder today = new StringBuilder(DateUtil.getDay(0));
        EventStatusScan eventStatusScan = new EventStatusScan();
        eventStatusScan.setYear(date.substring(0,4));
        eventStatusScan.setMonth(date.substring(4,6));
        eventStatusScan.setDay(date.substring(6));
        eventStatusScan.setThisYear(today.substring(0,4));
        eventStatusScan.setThisMonth(today.substring(4,6));
        eventStatusScan.setToday(today.substring(6));
        Integer time = Integer.valueOf(new SimpleDateFormat("HH").format(new Date()))*60+Integer.valueOf(new SimpleDateFormat("mm").format(new Date()));
        eventStatusScan.setTime(time.toString());
        logger.info("有"+eventMapper.queryExpiredEvents(eventStatusScan)+"条事件待修改");
        logger.info("修改了"+eventMapper.updateExpiredEvents(eventStatusScan)+"条事件");
    }*/
}