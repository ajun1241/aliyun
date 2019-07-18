package com.modcreater.tmbiz.config;

import com.modcreater.tmbeans.databaseparam.EventStatusScan;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.pojo.TimedTask;
import com.modcreater.tmdao.mapper.AchievementMapper;
import com.modcreater.tmdao.mapper.EventMapper;
import com.modcreater.tmdao.mapper.OrderMapper;
import com.modcreater.tmdao.mapper.TimedTaskMapper;
import com.modcreater.tmutils.DateUtil;
import com.modcreater.tmutils.RongCloudMethodUtil;
import com.modcreater.tmutils.SingleEventUtil;
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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Description:
 * EnableScheduling   1.开启定时任务
 * EnableAsync        2.开启多线程
 *
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

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private AchievementMapper achievementMapper;

    private Logger logger = LoggerFactory.getLogger(TimerConfig.class);

    /**
     * 以下为每分钟的第0s进行过期事件过滤修改
     */
    @Scheduled(cron = "0 * * * * ?")
    public void eventStatusScan() {
        StringBuilder today = new StringBuilder(DateUtil.getDay(0));
        StringBuilder yesterday = new StringBuilder(DateUtil.getDay(-1));
        EventStatusScan eventStatusScan = new EventStatusScan();
        eventStatusScan.setThisYear(Long.valueOf(today.substring(0, 4)));
        eventStatusScan.setThisMonth(Long.valueOf(today.substring(4, 6)));
        eventStatusScan.setToday(Long.valueOf(today.substring(6)));
        eventStatusScan.setLastYear(Long.valueOf(yesterday.substring(0, 4)));
        eventStatusScan.setLastMonth(Long.valueOf(yesterday.substring(4, 6)));
        eventStatusScan.setYesterday(Long.valueOf(yesterday.substring(6)));
        Integer time = Integer.valueOf(new SimpleDateFormat("HH").format(new Date())) * 60 + Integer.valueOf(new SimpleDateFormat("mm").format(new Date()));
        eventStatusScan.setTime((long) time);
        List<Long> userIds = eventMapper.queryExpiredEvents(eventStatusScan);
        logger.info("有" + userIds.size() + "条事件待修改");
        if (userIds.size() != 0) {
            Long result = eventMapper.updateExpiredEvents(eventStatusScan);
            logger.info("修改了" + result + "条事件");
        }
        List<SingleEvent> allLoopEventResults = eventMapper.queryAllLoopEvent(time);
        if (allLoopEventResults.size() > 0) {
            for (SingleEvent loopEvent : allLoopEventResults) {
                Boolean[] repeatTime = SingleEventUtil.getRepeatTime(loopEvent);
                int week = DateUtil.stringToWeek(null);
                week = week == 7 ? 0 : week;
                if (repeatTime[week]) {
                    week = week == 0 ? 6 : week - 1;
                    boolean result = ((Integer.parseInt(loopEvent.getEndtime()) == time) || (repeatTime[week] && Long.valueOf(loopEvent.getEndtime()) == 1440));
                    if (result) {
                        loopEvent.setYear(eventStatusScan.getThisYear());
                        loopEvent.setMonth(eventStatusScan.getThisMonth());
                        loopEvent.setDay(eventStatusScan.getToday());
                        loopEvent.setEventid(System.currentTimeMillis()/1000);
                        loopEvent.setIsOverdue(1L);
                        loopEvent.setIsLoop(0);
                        loopEvent.setRepeaTtime("[false,false,false,false,false,false,false]");
                        eventMapper.uploadingEvents(loopEvent);
                    }
                }
            }
        }
    }

    @Scheduled(cron = "30 * * * * ?")
    public void orderStatusScan() {
        Long timestamp = System.currentTimeMillis() / 1000;
        Long orders = orderMapper.queryExpiredOrders(timestamp);
        logger.info("有" + orders + "个订单待修改");
        if (orders != 0) {
            logger.info("修改了" + orderMapper.updateExpiredOrders(timestamp) + "个订单");
        }
    }
}