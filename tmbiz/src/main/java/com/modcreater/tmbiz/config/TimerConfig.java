package com.modcreater.tmbiz.config;

import com.modcreater.tmbeans.databaseparam.EventStatusScan;
import com.modcreater.tmbeans.pojo.AppType;
import com.modcreater.tmbeans.pojo.DiscountUser;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmdao.mapper.*;
import com.modcreater.tmutils.DateUtil;
import com.modcreater.tmutils.IOSPushUtil;
import com.modcreater.tmutils.SingleEventUtil;
import com.modcreater.tmutils.mobserver.MobPushUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
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
    private AppTypeMapper appTypeMapper;
    @Resource
    private UserSettingsMapper userSettingsMapper;
    @Resource
    private AchievementMapper achievementMapper;

    private static Logger logger = LoggerFactory.getLogger(TimerConfig.class);

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
        int time = DateUtil.getCurrentMinutes();
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
                        loopEvent.setEventid(System.currentTimeMillis() / 1000);
                        loopEvent.setIsOverdue(1L);
                        loopEvent.setFlag(5L);
                        loopEvent.setIsLoop(0);
                        loopEvent.setRepeaTtime("[false,false,false,false,false,false,false]");
                        eventMapper.uploadingEvents1(loopEvent);
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


    @Scheduled(cron = "32 * * * * ?")
    public void discountCouponStatusScan() {
        List<DiscountUser> discountUsers = orderMapper.getBindingDiscountCoupons();
        for (DiscountUser discountUser : discountUsers) {
            String status = orderMapper.getUserOrder(discountUser.getOrderId().toString()).getOrderStatus();
            if ("3".equals(status) || "4".equals(status)) {
                logger.info("已将" + orderMapper.setDiscountCouponOrderId(discountUser.getId(), "0", "0") + "个优惠券状态改为未使用");
            }
        }
    }

    @Scheduled(cron = "0 * * * * ?")
    public void pushTask() {
//        IOSPushUtil.APNSPush("c83be9ffcbe7128a0248d78268b8be2f9ccc226c9afb65edaebabfa162d1d242","IOS,推送测试",1);
        logger.info("推送提醒");
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        //今天
        String[] time1=simpleDateFormat.format(new Date()).split("-");
        Calendar cal1 = new GregorianCalendar();
        cal1.setTime(new Date());
        cal1.add(Calendar.DATE,1);
        //明天
        String[] time2= simpleDateFormat.format(cal1.getTime()).split("-");
        //不重复事件
        List<SingleEvent> singleEventList=eventMapper.querySingleEventByTime(time1[0],time1[1],time1[2]);
        List<SingleEvent> singleEventList1=eventMapper.querySingleEventByTime(time2[0],time2[1],time2[2]);
        long now=(Long.valueOf(time1[3])*60+Long.valueOf(time1[4]));
        for (SingleEvent singleEvent : singleEventList) {
            String startTime=singleEvent.getStarttime();
            String remindTime=singleEvent.getRemindTime();
            long remind=(Long.valueOf(startTime)-Long.valueOf(remindTime)) < 0 ? 1440-Long.valueOf(remindTime) : (Long.valueOf(startTime)-Long.valueOf(remindTime));
            if (now == remind){
                //符合时间开始推送
                AppType appType=appTypeMapper.queryAppType(singleEvent.getUserid().toString());
                if (!ObjectUtils.isEmpty(appType) && !StringUtils.isEmpty(appType.getDeviceToken())) {
                    //判断勿扰模式
                    if (userSettingsMapper.getDND(singleEvent.getUserid().toString()) == 1L) {
                        //安卓
                        if (appType.getAppType() == 1L) {
                            logger.info("开始要推送事件" + singleEvent.toString());
                            MobPushUtils.pushTask("您的事件" + singleEvent.getEventname() + "就要开始啦", new String[]{appType.getDeviceToken()});
                        }else if (appType.getAppType() == 2L){
                            logger.info("IOS开始要推送事件" + singleEvent.toString());
//                            IOSPushUtil.APNSPush(appType.getDeviceToken(),"您的事件" + singleEvent.getEventname() + "就要开始啦",1);
                        }
                    }
                }
            }
        }
        for (SingleEvent singleEvent:singleEventList1) {
            //如果明天的事件提醒事件是今天
            long remind=(Long.valueOf(singleEvent.getStarttime())-Long.valueOf(singleEvent.getRemindTime()));
            if ( remind< 0){
                remind=1440-Long.valueOf(singleEvent.getRemindTime());
                if (now == remind){
                    //符合时间开始推送
                    AppType appType=appTypeMapper.queryAppType(singleEvent.getUserid().toString());
                    if (!ObjectUtils.isEmpty(appType) && !StringUtils.isEmpty(appType.getDeviceToken())) {
                        //判断勿扰模式
                        if (userSettingsMapper.getDND(singleEvent.getUserid().toString()) == 1L) {
                            //安卓
                            if (appType.getAppType() == 1L) {
                                logger.info("开始要推送事件" + singleEvent.toString());
                                MobPushUtils.pushTask("您的事件" + singleEvent.getEventname() + "就要开始啦", new String[]{appType.getDeviceToken()});
                            }else if (appType.getAppType() == 2L){
                                logger.info("IOS开始要推送事件" + singleEvent.toString());
//                                IOSPushUtil.APNSPush(appType.getDeviceToken(),"您的事件" + singleEvent.getEventname() + "就要开始啦",1);
                            }
                        }
                    }
                }
            }
        }
        //重复事件
        List<SingleEvent> loopEventList=eventMapper.queryLoopEventByTime();
        //今天的星期
        int week1=DateUtil.stringToWeek(time1[0]+time1[1]+time1[2]) == 7 ? 0 : DateUtil.stringToWeek(time1[0]+time1[1]+time1[2]);
        int week2=DateUtil.stringToWeek(time2[0]+time2[1]+time2[2]) == 7 ? 0 : DateUtil.stringToWeek(time2[0]+time2[1]+time2[2]);
        for (SingleEvent loopEvent:loopEventList) {
            String[] le=loopEvent.getRepeaTtime().substring(1,loopEvent.getRepeaTtime().length()-1).split(",");
            //如果今天有重复事件
            String startTime=loopEvent.getStarttime();
            String remindTime=loopEvent.getRemindTime();
            if ("true".equals(le[week1])){
                long remind=(Long.valueOf(startTime)-Long.valueOf(remindTime));
                if (now == remind){
                    //符合时间开始推送
                    AppType appType=appTypeMapper.queryAppType(loopEvent.getUserid().toString());
                    if (!ObjectUtils.isEmpty(appType) && !StringUtils.isEmpty(appType.getDeviceToken())) {
                        //判断勿扰模式
                        if (userSettingsMapper.getDND(loopEvent.getUserid().toString()) == 1L) {
                            //安卓
                            if (appType.getAppType() == 1L) {
                                logger.info("安卓开始要推送事件" + loopEvent.toString());
                                MobPushUtils.pushTask("您的事件" + loopEvent.getEventname() + "就要开始啦", new String[]{appType.getDeviceToken()});
                            }else if (appType.getAppType() == 2L){
                                logger.info("IOS开始要推送事件" + loopEvent.toString());
//                                IOSPushUtil.APNSPush(appType.getDeviceToken(),"您的事件" + loopEvent.getEventname() + "就要开始啦",1);
                            }
                        }
                    }
                }
            }
            //如果明天有重复事件
            if ("true".equals(le[week2])){
                long remind=(Long.valueOf(startTime)-Long.valueOf(remindTime));
                //如果明天的事件提醒时间是今天
                if (remind<0){
                    remind=1440-Long.valueOf(remindTime);
                    if (now == remind){
                        //符合时间开始推送
                        AppType appType=appTypeMapper.queryAppType(loopEvent.getUserid().toString());
                        if (!ObjectUtils.isEmpty(appType) && !StringUtils.isEmpty(appType.getDeviceToken())) {
                            //判断勿扰模式
                            if (userSettingsMapper.getDND(loopEvent.getUserid().toString()) == 1L) {
                                //安卓
                                if (appType.getAppType() == 1L) {
                                    logger.info("开始要推送事件" + loopEvent.toString());
                                    MobPushUtils.pushTask("您的事件" + loopEvent.getEventname() + "就要开始啦", new String[]{appType.getDeviceToken()});
                                }else if (appType.getAppType() == 2L){
                                    logger.info("IOS开始要推送事件" + loopEvent.toString());
//                                    IOSPushUtil.APNSPush(appType.getDeviceToken(),"您的事件" + loopEvent.getEventname() + "就要开始啦",1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}