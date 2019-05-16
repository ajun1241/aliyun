package com.modcreater.tmbiz.service.impl;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.show.ShowSingleEvent;
import com.modcreater.tmbeans.vo.*;
import com.modcreater.tmbiz.service.EventService;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmdao.mapper.EventMapper;
import com.modcreater.tmutils.DateUtil;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmutils.SingleEventUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import com.alibaba.fastjson.JSONObject;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-04-29
 * Time: 11:32
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class EventServiceImpl implements EventService {

    @Resource
    private EventMapper eventMapper;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Dto addNewEvents(UploadingEventVo uploadingEventVo,String token) {
        if (StringUtils.hasText(uploadingEventVo.getUserId())) {
            if (!StringUtils.hasText(token)){
                return DtoUtil.getFalseDto("操作失败,token未获取到",21013);
            }
            if (!token.equals(stringRedisTemplate.opsForValue().get(uploadingEventVo.getUserId()))){
                return DtoUtil.getFalseDto("token过期请先登录",21014);
            }
            if (StringUtils.hasText(uploadingEventVo.getSingleEvent())) {
                System.out.println("上传" + uploadingEventVo.toString());
                SingleEvent singleEvent = JSONObject.parseObject(uploadingEventVo.getSingleEvent(), SingleEvent.class);
                //对解析得到的SingleEvent进行检测
                Dto dto = SingleEventUtil.isSingleEventStandard(singleEvent);
                if (!ObjectUtils.isEmpty(dto)){
                    return dto;
                }
                singleEvent.setUserid(Long.valueOf(uploadingEventVo.getUserId()));

                //如果查询Id的数量为0才能继续添加的操作(单一事件)
                if (eventMapper.countIdByDate(singleEvent) != 0){
                    return DtoUtil.getFalseDto("时间段冲突,无法添加事件",21012);
                }
                //判断重复事件表中是否有冲突的时间段的事件
                List<SingleEvent> loopEventList = eventMapper.queryLoopEvents(singleEvent.getUserid().toString());
                String day = singleEvent.getDay().toString();
                String month = singleEvent.getMonth().toString();
                if (day.length() < 2){
                    day = "0"+day;
                }
                if (month.length() < 2){
                    month = "0"+month;
                }
                int week = (DateUtil.stringToWeek(singleEvent.getYear().toString()+month+day))-1;
                for (SingleEvent loopEvent : loopEventList){
                    Boolean[] loopEventRepeatTimeInDataBase = SingleEventUtil.getRepeatTime(loopEvent);
                    if (loopEventRepeatTimeInDataBase[week]){
                        int startTimeInDataBase = Integer.valueOf(loopEvent.getStarttime());
                        int endTimeInDataBase = Integer.valueOf(loopEvent.getStarttime());
                        int startTime = Integer.valueOf(singleEvent.getStarttime());
                        int endTime = Integer.valueOf(singleEvent.getEndtime());
                        if (((startTime>startTimeInDataBase && endTime<endTimeInDataBase)
                                || (startTime>=startTimeInDataBase && startTime <= endTimeInDataBase)
                                || (endTime>=startTimeInDataBase && endTime<=endTimeInDataBase)
                                || (startTime<=startTimeInDataBase && endTime>=endTimeInDataBase))){
                            return DtoUtil.getFalseDto("时间段冲突,无法添加事件",21012);
                        }
                    }
                }
                //这里开始判断是否是一个重复事件,如果状态值为真,则该事件为重复事件
                if (SingleEventUtil.isLoopEvent(singleEvent.getRepeaTtime())) {
                    if (!ObjectUtils.isEmpty(singleEvent) && eventMapper.uploadingLoopEvents(singleEvent) > 0) {
                        return DtoUtil.getSuccessDto("事件上传成功", 100000);
                    }
                } else {
                    if (!ObjectUtils.isEmpty(singleEvent) && eventMapper.uploadingEvents(singleEvent) > 0) {
                        return DtoUtil.getSuccessDto("事件上传成功", 100000);
                    }
                }
                return DtoUtil.getFalseDto("事件上传失败", 21001);
            }
            return DtoUtil.getFalseDto("没有可上传的事件", 21002);
        }
        return DtoUtil.getFalseDto("请先登录", 21011);
    }

    @Override
    public Dto deleteEvents(DeleteEventVo deleteEventVo,String token) {
        if (StringUtils.hasText(deleteEventVo.getUserId())) {
            if (!StringUtils.hasText(token)){
                return DtoUtil.getFalseDto("操作失败,token未获取到",21013);
            }
            if (!token.equals(stringRedisTemplate.opsForValue().get(deleteEventVo.getUserId()))){
                return DtoUtil.getFalseDto("token过期请先登录",21014);
            }
            if (StringUtils.hasText(deleteEventVo.getEventId())) {
                System.out.println("删除" + deleteEventVo.toString());
                SingleEvent singleEvent = new SingleEvent();
                singleEvent.setUserid(Long.valueOf(deleteEventVo.getUserId()));
                singleEvent.setEventid(Long.valueOf(deleteEventVo.getEventId()));
                if (eventMapper.withdrawEventsByUserId(singleEvent) > 0 || eventMapper.withdrawLoopEventsByUserId(singleEvent) > 0) {
                    return DtoUtil.getSuccessDto("删除成功", 100000);
                }
                return DtoUtil.getFalseDto("删除事件失败", 21005);
            }
            return DtoUtil.getFalseDto("删除条件接收失败", 21006);
        }
        return DtoUtil.getFalseDto("请先登录", 21011);
    }


    @Override
    public Dto updateEvents(UpdateEventVo updateEventVo,String token) {
        if (StringUtils.hasText(updateEventVo.getUserId())) {
            if (!StringUtils.hasText(token)){
                return DtoUtil.getFalseDto("操作失败,token未获取到",21013);
            }
            if (!token.equals(stringRedisTemplate.opsForValue().get(updateEventVo.getUserId()))){
                return DtoUtil.getFalseDto("token过期请先登录",21014);
            }
            if (!ObjectUtils.isEmpty(updateEventVo)) {
                System.out.println("修改" + updateEventVo.toString());
                SingleEvent singleEvent = JSONObject.parseObject(updateEventVo.getSingleEvent(), SingleEvent.class);
                //对解析得到的SingleEvent进行检测
                Dto dto = SingleEventUtil.isSingleEventStandard(singleEvent);
                if (!ObjectUtils.isEmpty(dto)){
                    return dto;
                }
                singleEvent.setUserid(Long.valueOf(updateEventVo.getUserId()));
                //这里开始判断是否是一个重复事件,如果状态值为真,则该事件为重复事件
                if (SingleEventUtil.isLoopEvent(singleEvent.getRepeaTtime())) {
                    //根据userId和eventId查询重复事件表
                    SingleEvent loopEvent2 = eventMapper.queryLoopSingleEvent(singleEvent);
                    //如果查询结果不为空,则把将要修改的事件插入到重复事件表中并将原有的那条数据覆盖
                    if (!ObjectUtils.isEmpty(loopEvent2)){
                        if (eventMapper.alterLoopEventsByUserId(singleEvent) > 0){
                            return DtoUtil.getSuccessDto("修改成功",100000);
                        }
                    }else {
                        //反之,将单一事件表中的单一事件删除,再将事件添加到重复事件表中
                        if (eventMapper.deleteEventsByUserId(singleEvent) > 0){
                            if (eventMapper.uploadingLoopEvents(singleEvent) > 0){
                                return DtoUtil.getSuccessDto("修改成功",100000);
                            }
                        }
                    }
                } else {
                    //根据userId和eventId查询单一事件表
                    SingleEvent singleEvent1 = eventMapper.querySingleEvent(singleEvent);
                    //如果查询结果不为空,则把将要修改的事件插入到单一事件表中并将原有的那条数据覆盖
                    if (!ObjectUtils.isEmpty(singleEvent1)){
                        if (eventMapper.alterEventsByUserId(singleEvent) > 0){
                            return DtoUtil.getSuccessDto("修改成功",100000);
                        }
                    }else {
                        //反之,将重复事件表中的重复事件删除,再将事件添加到单一事件表中
                        if (eventMapper.deleteLoopEventsByUserId(singleEvent) > 0){
                            if (eventMapper.uploadingEvents(singleEvent) > 0){
                                return DtoUtil.getSuccessDto("修改成功",100000);
                            }
                        }
                    }
                }
                return DtoUtil.getFalseDto("修改事件失败", 21007);
            }
            return DtoUtil.getFalseDto("修改条件接收失败", 21008);
        }
        return DtoUtil.getFalseDto("请先登录", 21011);
    }

    @Override
    public Dto synchronousUpdate(SynchronousUpdateVo synchronousUpdateVo,String token) {
        if (ObjectUtils.isEmpty(synchronousUpdateVo)) {
            return DtoUtil.getFalseDto("本地上传数据未获取到", 25001);
        }
        System.out.println("本地数据上传" + synchronousUpdateVo.toString());
        if (StringUtils.isEmpty(synchronousUpdateVo.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("操作失败,token未获取到",21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(synchronousUpdateVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        /*if (synchronousUpdateVo.getDayEventsList().size() <= 0) {
            return DtoUtil.getFalseDto("事件集未获取到", 25002);
        }
        List<Integer> dayEventIds = new ArrayList<>();
        //查询时间段内的事件
        for (DayEvents dayEvents : synchronousUpdateVo.getDayEventsList()) {
            dayEventIds.add(dayEvents.getDayEventId());
        }
        System.out.println(dayEventIds.toString());
        SingleEvent singleEvent = new SingleEvent();
        for (Integer dayEventId:dayEventIds) {
            try {
                StringBuffer stringBuffer = new StringBuffer(dayEventId.toString());
                String year = stringBuffer.substring(0, 4);
                String month = stringBuffer.substring(4, 6);
                String day = stringBuffer.substring(6, 8);
                singleEvent.setYear(Long.parseLong(year));
                singleEvent.setMonth(Long.parseLong(month));
                singleEvent.setDay(Long.parseLong(day));
                singleEvent.setUserid(Long.parseLong(synchronousUpdateVo.getUserId()));
                //删除事件
                System.out.println(singleEvent.toString());
                int updResult = eventMapper.updOldEvent(singleEvent);
                if (updResult<=0){
                    return DtoUtil.getFalseDto("云端删除"+dayEventId+"失败",25004);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //上传普通事件
        for (DayEvents<SingleEvent> dayEvents:synchronousUpdateVo.getDayEventsList()) {
            for (SingleEvent singleEvent1:dayEvents.getMySingleEventList()) {
                //插入用户id
                singleEvent1.setUserid(Long.parseLong(synchronousUpdateVo.getUserId()));
                int uplResult = eventMapper.uploadingEvents(singleEvent1);
                if (uplResult <= 0) {
                    return DtoUtil.getFalseDto("上传事件"+singleEvent1.getEventid()+"失败", 25005);
                }
            }
        }
        //上传重复事件
        for (List<SingleEvent> loopEvents:synchronousUpdateVo.getLoopEventList()) {
            for (SingleEvent loopEvent:loopEvents) {
                int i=eventMapper.uploadingLoopEvents(loopEvent);
                if (i<=0){
                    return DtoUtil.getFalseDto("上传重复事件"+loopEvent.getEventid()+"失败",25006);
                }
            }
        }
        //修改时间戳
        Map map = new HashMap();
        try {
            String time = DateUtil.dateToStamp(new Date());
            int i = accountMapper.updateTimestampUnderAccount(synchronousUpdateVo.getUserId(), time);
            if (i <= 0) {
                return DtoUtil.getFalseDto("同步数据时修改时间戳失败", 25006);
            }
            map.put("time", time);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        return DtoUtil.getSuccesWithDataDto("数据同步成功", null, 100000);
    }

    @Override
    public Dto contrastTimestamp(ContrastTimestampVo contrastTimestampVo,String token) {
        if (ObjectUtils.isEmpty(contrastTimestampVo)) {
            return DtoUtil.getFalseDto("时间戳获取失败", 24001);
        }
        if (StringUtils.isEmpty(contrastTimestampVo.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("操作失败,token未获取到",21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(contrastTimestampVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        String time = accountMapper.queryTime(contrastTimestampVo.getUserId());
        if (StringUtils.isEmpty(time)) {
            return DtoUtil.getFalseDto("查询时间戳失败", 24003);
        }
        //noinspection AlibabaUndefineMagicConstant
        if (Long.parseLong(contrastTimestampVo.getTime()) - Long.parseLong(time) <= 3) {
            return DtoUtil.getFalseDto("不需要同步", 24002);
        }
        return DtoUtil.getSuccessDto("需要同步", 100000);
    }

    @Override
    public Dto firstUplEvent(SynchronousUpdateVo synchronousUpdateVo,String token) {
        if (ObjectUtils.isEmpty(synchronousUpdateVo)) {
            return DtoUtil.getFalseDto("同步数据未获取到", 26001);
        }
        System.out.println("第一次上传" + synchronousUpdateVo.toString());
        if (StringUtils.isEmpty(synchronousUpdateVo.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (!StringUtils.hasText(token)){
            return DtoUtil.getFalseDto("操作失败,token未获取到",21013);
        }
        if (!token.equals(stringRedisTemplate.opsForValue().get(synchronousUpdateVo.getUserId()))){
            return DtoUtil.getFalseDto("token过期请先登录",21014);
        }
        //判断是否第一次上传
        int ie=eventMapper.queryEventByUserId(synchronousUpdateVo.getUserId());
        List list=eventMapper.queryLoopEvents(synchronousUpdateVo.getUserId());
       /* System.out.println("222222222222222222222222====>"+ie);
        System.out.println("2222222222222222222222222==》"+list.size());*/
        if ( ie> 0 || (list.size()>0)) {
            return DtoUtil.getFalseDto("该用户已经上传过了", 26003);
        }
        boolean flag=false;
        if (!StringUtils.isEmpty(synchronousUpdateVo.getDayEventList())){
            //转换集合
            List<ArrayList> dayEvents=JSONObject.parseObject(synchronousUpdateVo.getDayEventList(),ArrayList.class);
            //上传普通事件
            for (Object dayEventsList:dayEvents) {
                //转换成DayEvents
                DayEvents dayEvents1=JSONObject.parseObject(dayEventsList.toString(),DayEvents.class);
                //把getMySingleEventList()转换成集合
                ArrayList<SingleEvent> singleEventList= JSONObject.parseObject(dayEvents1.getMySingleEventList().toString(),ArrayList.class);
                for (Object singleEvent:singleEventList) {
                    //把遍历出的元素转换成对象
                    SingleEvent singleEvent1=JSONObject.parseObject(singleEvent.toString(),SingleEvent.class);
                    //对解析得到的SingleEvent进行检测
                    Dto dto = SingleEventUtil.isSingleEventStandard(singleEvent1);
                    if (!ObjectUtils.isEmpty(dto)){
                        return dto;
                    }
                    //插入用户id
                    singleEvent1.setUserid(Long.parseLong(synchronousUpdateVo.getUserId()));
                    //上传
                    int uplResult = eventMapper.uploadingEvents(singleEvent1);
                    if (uplResult <= 0) {
                        return DtoUtil.getFalseDto("上传事件"+singleEvent1.getEventid()+"失败", 25005);
                    }
                }
            }
            flag=true;
        }
        if (!StringUtils.isEmpty(synchronousUpdateVo.getLoopEventList())) {
            //外层集合转换
            List<ArrayList> loopEvents = JSONObject.parseObject(synchronousUpdateVo.getLoopEventList(), ArrayList.class);
            //上传重复事件
            for (List<SingleEvent> singleEvents : loopEvents) {
                //第二层转换
                List<SingleEvent> singleEventList=JSONObject.parseObject(singleEvents.toString(),ArrayList.class);
                for (Object loopEvent : singleEventList) {
                    //第三层转换
                    SingleEvent singleEvent=JSONObject.parseObject(loopEvent.toString(),SingleEvent.class);
                    singleEvent.setUserid(Long.parseLong(synchronousUpdateVo.getUserId()));
                    int i = eventMapper.uploadingLoopEvents(singleEvent);
                    if (i <= 0) {
                        return DtoUtil.getFalseDto("上传重复事件" + singleEvent.getEventid() + "失败", 25006);
                    }
                }
            }
            flag=true;
        }
        if (flag){
            return DtoUtil.getSuccessDto("数据同步成功", 100000);
        }else {
            return DtoUtil.getSuccessDto("数据同步失败", 25008);
        }
    }

    @Override
    public Dto uplDraft(DraftVo draftVo,String token) {
        if (ObjectUtils.isEmpty(draftVo)) {
            return DtoUtil.getFalseDto("上传草稿未获取到", 27001);
        }

        //查看草稿是否已存在
        String data = eventMapper.queryDraftByPhone(draftVo.getPhoneNum());
        if (StringUtils.isEmpty(data)) {
            //第一次上传草稿
            if (eventMapper.uplDraft(draftVo) <= 0) {
                return DtoUtil.getFalseDto("第一次上传草稿失败", 27002);
            }
        } else {
            //不是第一次上传
            if (eventMapper.updateDraft(draftVo) <= 0) {
                return DtoUtil.getFalseDto("非第一次上传草稿失败", 27003);
            }
        }
        return DtoUtil.getSuccessDto("上传草稿成功", 100000);
    }

    @Override
    public Dto searchByDayEventIds(SearchEventVo searchEventVo,String token) {
        if (StringUtils.hasText(searchEventVo.getUserId())) {
            if (!StringUtils.hasText(token)){
                return DtoUtil.getFalseDto("操作失败,token未获取到",21013);
            }
            if (!token.equals(stringRedisTemplate.opsForValue().get(searchEventVo.getUserId()))){
                return DtoUtil.getFalseDto("token过期请先登录",21014);
            }
            if (StringUtils.hasText(searchEventVo.getDayEventId())) {
                System.out.println("按天查" + searchEventVo.toString());
                /*boolean singleResult = false;
                boolean loopResult = false;*/
                //拆分dayEventId并将查询条件逐一添加到对象中
                SingleEvent singleEvent = SingleEventUtil.getSingleEvent(searchEventVo.getUserId(), searchEventVo.getDayEventId());
                //只根据level升序
                List<SingleEvent> singleEventListOrderByLevel = eventMapper.queryByDayOrderByLevel(singleEvent);
                List<ShowSingleEvent> showSingleEventListOrderByLevel = new ArrayList<>();
                //根据level和开始时间升序
                List<SingleEvent> singleEventListOrderByLevelAndDate = eventMapper.queryByDayOrderByLevelAndDate(singleEvent);
                List<ShowSingleEvent> showSingleEventListOrderByLevelAndDate = new ArrayList<>();
                //添加一个未排序的结果集到dayEvents中
                DayEvents<ShowSingleEvent> dayEvents = new DayEvents<>();
                ArrayList<SingleEvent> singleEventList = eventMapper.queryEvents(singleEvent);
                ArrayList<ShowSingleEvent> showSingleEventList = new ArrayList<>();
                if (singleEventListOrderByLevel.size() != 0 && singleEventListOrderByLevelAndDate.size() != 0 && singleEventList.size() != 0) {
                    showSingleEventListOrderByLevel = SingleEventUtil.getShowSingleEventList(singleEventListOrderByLevel);
                    showSingleEventListOrderByLevelAndDate = SingleEventUtil.getShowSingleEventList(singleEventListOrderByLevelAndDate);
                    showSingleEventList = (ArrayList<ShowSingleEvent>) SingleEventUtil.getShowSingleEventList(singleEventList);
//                    singleResult = true;
                }
                dayEvents.setUserId(singleEvent.getUserid().intValue());
                dayEvents.setTotalNum(singleEventList.size());
                dayEvents.setDayEventId(Integer.valueOf(searchEventVo.getDayEventId()));
                dayEvents.setMySingleEventList(showSingleEventList);

                /**
                 * 查询重复事件
                 */
                int week = DateUtil.stringToWeek(searchEventVo.getDayEventId());
                //根据用户ID查询重复事件
                List<SingleEvent> loopEventListInDataBase = eventMapper.queryLoopEvents(searchEventVo.getUserId());
                //判断上一条查询结果是否有数据
                if (loopEventListInDataBase.size() != 0) {
                    //遍历集合并将符合repeatTime = 星期 的对象分别添加到集合中
                    for (SingleEvent singleEvent1 : loopEventListInDataBase) {
                        ShowSingleEvent showSingleEvent = SingleEventUtil.getShowSingleEvent(singleEvent1);
                        if (showSingleEvent.getRepeaTtime()[week]) {
                            showSingleEventListOrderByLevel.add(showSingleEvent);
                            showSingleEventListOrderByLevelAndDate.add(showSingleEvent);
//                            loopResult = true;
                        }
                    }
                }
                /**
                 * 将得到的数据封装到map作为返回
                 */
                Map<String, Object> result = new HashMap<>(3);
                result.put("ShowSingleEventListOrderByLevel", showSingleEventListOrderByLevel);
                result.put("ShowSingleEventListOrderByLevelAndDate", showSingleEventListOrderByLevelAndDate);
                result.put("dayEvents", dayEvents);
                if (!ObjectUtils.isEmpty(dayEvents) ) {
                    return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
                }
                return DtoUtil.getFalseDto("查询失败,没有数据", 200000);
            }
            return DtoUtil.getFalseDto("查询条件接收失败", 21004);
        }
        return DtoUtil.getFalseDto("请先登录", 21011);
    }

    @Override
    public Dto searchByDayEventIdsInMonth(SearchEventVo searchEventVo,String token) {
        if (StringUtils.hasText(searchEventVo.getUserId())) {
            if (!StringUtils.hasText(token)){
                return DtoUtil.getFalseDto("操作失败,token未获取到",21013);
            }
            if (!token.equals(stringRedisTemplate.opsForValue().get(searchEventVo.getUserId()))){
                return DtoUtil.getFalseDto("token过期请先登录",21014);
            }
            if (StringUtils.hasText(searchEventVo.getDayEventId())) {
                System.out.println("按月查" + searchEventVo.toString());
                SingleEvent singleEvent = SingleEventUtil.getSingleEvent(searchEventVo.getUserId(), searchEventVo.getDayEventId());
                //查询在该月内存在事件的日的集合
                List<Integer> days = eventMapper.queryDays(singleEvent);
                List<DayEvents<ShowSingleEvent>> dayEventsList = new ArrayList<>();
                if (days.size() != 0) {
                    for (Integer day : days) {
                        singleEvent.setDay(day.longValue());
                        ArrayList<SingleEvent> singleEventList = eventMapper.queryEvents(singleEvent);
                        if (singleEventList.size() != 0) {
                            ArrayList<ShowSingleEvent> showSingleEventList = (ArrayList<ShowSingleEvent>) SingleEventUtil.getShowSingleEventList(singleEventList);
                            DayEvents<ShowSingleEvent> dayEvents = new DayEvents<>();
                            dayEvents.setUserId(singleEvent.getUserid().intValue());
                            dayEvents.setTotalNum(singleEventList.size());
                            dayEvents.setDayEventId(Integer.valueOf(singleEvent.getYear().toString() + singleEvent.getMonth().toString() + singleEvent.getDay().toString()));
                            dayEvents.setMySingleEventList(showSingleEventList);
                            dayEventsList.add(dayEvents);
                        }
                    }
                    return DtoUtil.getSuccesWithDataDto("查询成功", dayEventsList, 100000);
                }
                return DtoUtil.getFalseDto("查询失败,没有数据", 200000);
            }
            return DtoUtil.getFalseDto("查询条件接收失败", 21004);
        }
        return DtoUtil.getFalseDto("请先登录", 21011);
    }

    @Override
    public Dto searchByDayEventIdsInWeek(SearchEventVo searchEventVo,String token) {
        if (StringUtils.hasText(searchEventVo.getUserId())) {
            if (!StringUtils.hasText(token)){
                return DtoUtil.getFalseDto("操作失败,token未获取到",21013);
            }
            if (!token.equals(stringRedisTemplate.opsForValue().get(searchEventVo.getUserId()))){
                return DtoUtil.getFalseDto("token过期请先登录",21014);
            }
            if (!ObjectUtils.isEmpty(searchEventVo)) {
                System.out.println("按周查" + searchEventVo.toString());
                //按周查询单一事件
                SingleEvent singleEvent;
                List<DayEvents> dayEventsList = new ArrayList<>();
                //noinspection AlibabaUndefineMagicConstant
                for (int i = 0; i <= 6; i++) {
                    DayEvents<ShowSingleEvent> dayEvents = new DayEvents();
                    String dayEventId = String.valueOf(Integer.valueOf(searchEventVo.getDayEventId()) + i);
                    singleEvent = SingleEventUtil.getSingleEvent(searchEventVo.getUserId(), dayEventId);
                    List<SingleEvent> singleEventList = eventMapper.queryByWeekOrderByStartTime(singleEvent);
                    ArrayList<ShowSingleEvent> showSingleEventList = (ArrayList<ShowSingleEvent>) SingleEventUtil.getShowSingleEventList(singleEventList);
                    dayEvents.setMySingleEventList(showSingleEventList);
                    dayEvents.setTotalNum(dayEvents.getMySingleEventList().size());
                    dayEvents.setUserId(Integer.valueOf(searchEventVo.getUserId()));
                    dayEvents.setDayEventId(Integer.valueOf(dayEventId));
                    dayEventsList.add(dayEvents);
                }
                //按周查询重复事件
                List<SingleEvent> loopEventListInDataBase = eventMapper.queryLoopEvents(searchEventVo.getUserId());
                List<List<ShowSingleEvent>> loopEventList = new ArrayList<>();
                //创建七个几个代表一周七天
                List<ShowSingleEvent> sunShowLoopEventList = new ArrayList<>();
                List<ShowSingleEvent> monShowLoopEventList = new ArrayList<>();
                List<ShowSingleEvent> tueShowLoopEventList = new ArrayList<>();
                List<ShowSingleEvent> wedShowLoopEventList = new ArrayList<>();
                List<ShowSingleEvent> thuShowLoopEventList = new ArrayList<>();
                List<ShowSingleEvent> friShowLoopEventList = new ArrayList<>();
                List<ShowSingleEvent> satShowLoopEventList = new ArrayList<>();
                for (SingleEvent singleEvent1 : loopEventListInDataBase) {
                    ShowSingleEvent showSingleEvent = SingleEventUtil.getShowSingleEvent(singleEvent1);
                    Boolean[] booleans = showSingleEvent.getRepeaTtime();
                    //根据拆分出来的boolean数组进行判断并添加到一周的各个天数中
                    //noinspection AlibabaUndefineMagicConstant
                    for (int i = 0; i <= 6; i++) {
                        if (i == 0 && booleans[i]) {
                            sunShowLoopEventList.add(showSingleEvent);
                        }
                        if (i == 1 && booleans[i]) {
                            monShowLoopEventList.add(showSingleEvent);
                        }
                        if (i == 2 && booleans[i]) {
                            tueShowLoopEventList.add(showSingleEvent);
                        }
                        if (i == 3 && booleans[i]) {
                            wedShowLoopEventList.add(showSingleEvent);
                        }
                        if (i == 4 && booleans[i]) {
                            thuShowLoopEventList.add(showSingleEvent);
                        }
                        if (i == 5 && booleans[i]) {
                            friShowLoopEventList.add(showSingleEvent);
                        }
                        if (i == 6 && booleans[i]) {
                            satShowLoopEventList.add(showSingleEvent);
                        }
                    }
                }
                loopEventList.add(sunShowLoopEventList);
                loopEventList.add(monShowLoopEventList);
                loopEventList.add(tueShowLoopEventList);
                loopEventList.add(wedShowLoopEventList);
                loopEventList.add(thuShowLoopEventList);
                loopEventList.add(friShowLoopEventList);
                loopEventList.add(satShowLoopEventList);
                Map<String,Object> result = new HashMap<>(2);
                result.put("dayEventsList", dayEventsList);
                result.put("loopEventList", loopEventList);
                return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
            }
            return DtoUtil.getFalseDto("查询条件接收失败", 21004);
        }
        return DtoUtil.getFalseDto("请先登录", 21011);
    }
}
