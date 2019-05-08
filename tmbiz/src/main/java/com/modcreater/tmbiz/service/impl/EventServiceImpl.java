package com.modcreater.tmbiz.service.impl;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.LoopEvent;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.vo.*;
import com.modcreater.tmbiz.service.EventService;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmdao.mapper.EventMapper;
import com.modcreater.tmutils.DateUtil;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmutils.SingleEventUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import javax.annotation.Resource;
import java.text.ParseException;
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
@Transactional
public class EventServiceImpl implements EventService {

    @Resource
    private EventMapper eventMapper;

    @Resource
    private AccountMapper accountMapper;

    @Override
    public Dto addNewEvents(UploadingEventVo uploadingEventVo) {
        if (!ObjectUtils.isEmpty(uploadingEventVo)) {
            System.out.println("================"+uploadingEventVo.toString());
            SingleEvent singleEvent = JSONObject.parseObject(uploadingEventVo.getSingleEvent(),SingleEvent.class);
            /*singleEvent.setEventid(uploadingEventVo.getEventid());
            singleEvent.setUserid(uploadingEventVo.getUserid());
            singleEvent.setEventname(uploadingEventVo.getEventname());
            singleEvent.setStarttime(uploadingEventVo.getStarttime());
            singleEvent.setEndtime(uploadingEventVo.getEndtime());
            singleEvent.setAddress(uploadingEventVo.getAddress());
            singleEvent.setLevel(uploadingEventVo.getLevel());
            singleEvent.setFlag(uploadingEventVo.getFlag());
            singleEvent.setPerson(uploadingEventVo.getPerson());
            singleEvent.setRemarks(uploadingEventVo.getRemarks());
            singleEvent.setRepeaTtime(uploadingEventVo.getRepeaTtime());
            singleEvent.setIsOverdue(uploadingEventVo.getIsOverdue());
            singleEvent.setRemindTime(uploadingEventVo.getRemindTime());
            singleEvent.setDay(uploadingEventVo.getDay());
            singleEvent.setMonth(uploadingEventVo.getMonth());
            singleEvent.setYear(uploadingEventVo.getYear());
            singleEvent.setType(uploadingEventVo.getType());*/
            singleEvent.setUserid(Long.valueOf(uploadingEventVo.getUserId()));
            if (!ObjectUtils.isEmpty(singleEvent) && eventMapper.uploadingEvents(singleEvent) > 0) {
                try {
                    String time = DateUtil.dateToStamp(new Date());
                    if (accountMapper.updateTimestampUnderAccount(singleEvent.getUserid().toString(), time) > 0) {
                        Map<String,String> timestamp = new HashMap<>();
                        timestamp.put("time",time);
                        return DtoUtil.getSuccesWithDataDto("事件上传成功",timestamp, 100000);
                    }else {
                        return DtoUtil.getSuccessDto("事件上传成功,时间戳添加失败", 100000);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return DtoUtil.getFalseDto("事件上传失败", 21001);
        }
        return DtoUtil.getFalseDto("没有可上传的事件", 21002);
    }

    @Override
    public Dto deleteEvents(DeleteEventVo deleteEventVo) {
        if (!ObjectUtils.isEmpty(deleteEventVo)) {
            System.out.println(deleteEventVo.toString());
            SingleEvent singleEvent = new SingleEvent();
            singleEvent.setUserid(Long.valueOf(deleteEventVo.getUserId()));
            singleEvent.setEventid(Long.valueOf(deleteEventVo.getEventId()));
            if (eventMapper.withdrawEventsByUserId(singleEvent) > 0) {
                try {
                String time = DateUtil.dateToStamp(new Date());
                    if (accountMapper.updateTimestampUnderAccount(singleEvent.getUserid().toString(), time) > 0) {
                        Map<String,String> timestamp = new HashMap<>();
                        timestamp.put("time",time);
                        return DtoUtil.getSuccesWithDataDto("删除成功", timestamp,100000);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return DtoUtil.getFalseDto("删除事件失败", 21005);
        }
        return DtoUtil.getFalseDto("删除条件接收失败", 21006);
    }


    @Override
    public Dto updateEvents(UpdateEventVo updateEventVo) {
        if (!ObjectUtils.isEmpty(updateEventVo)) {
            SingleEvent singleEvent = JSONObject.parseObject(updateEventVo.getSingleEvent(),SingleEvent.class);
            /*SingleEvent singleEvent = new SingleEvent();
            singleEvent.setEventid(updateEventVo.getEventid());
            singleEvent.setUserid(updateEventVo.getUserid());
            singleEvent.setEventname(updateEventVo.getEventname());
            singleEvent.setStarttime(updateEventVo.getStarttime());
            singleEvent.setEndtime(updateEventVo.getEndtime());
            singleEvent.setAddress(updateEventVo.getAddress());
            singleEvent.setLevel(updateEventVo.getLevel());
            singleEvent.setFlag(updateEventVo.getFlag());
            singleEvent.setPerson(updateEventVo.getPerson());
            singleEvent.setRemarks(updateEventVo.getRemarks());
            singleEvent.setRepeaTtime(updateEventVo.getRepeaTtime());
            singleEvent.setIsOverdue(updateEventVo.getIsOverdue());
            singleEvent.setRemindTime(updateEventVo.getRemindTime());
            singleEvent.setDay(updateEventVo.getDay());
            singleEvent.setMonth(updateEventVo.getMonth());
            singleEvent.setYear(updateEventVo.getYear());
            singleEvent.setType(updateEventVo.getType());*/
            singleEvent.setUserid(Long.valueOf(updateEventVo.getUserId()));
            if (eventMapper.alterEventsByUserId(singleEvent) > 0 && !ObjectUtils.isEmpty(singleEvent)) {
                try {
                    String time = DateUtil.dateToStamp(new Date());
                    if (accountMapper.updateTimestampUnderAccount(singleEvent.getUserid().toString(), time) > 0) {
                        Map<String,String> timestamp = new HashMap<>();
                        timestamp.put("time",time);
                        return DtoUtil.getSuccesWithDataDto("修改成功",timestamp, 100000);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return DtoUtil.getFalseDto("修改事件失败", 21007);
        }
        return DtoUtil.getFalseDto("修改条件接收失败", 21008);
    }

    @Override
    public Dto searchEvents(SearchEventVo searchEventVo) {
        if (!ObjectUtils.isEmpty(searchEventVo)) {
            System.out.println(searchEventVo.toString());
            SingleEvent singleEvent = SingleEventUtil.getSingleEvent(searchEventVo.getUserId(),searchEventVo.getDayEventId());
            List<SingleEvent> singleEventList = eventMapper.queryEvents(singleEvent);
            if (!ObjectUtils.isEmpty(singleEventList)) {
                return DtoUtil.getSuccesWithDataDto("查询成功", singleEventList, 100000);
            }
            return DtoUtil.getFalseDto("查询失败", 21003);
        }
        return DtoUtil.getFalseDto("查询条件接收失败", 21004);
    }

    @Override
    public Dto synchronousUpdate(SynchronousUpdateVo synchronousUpdateVo) {
        if (ObjectUtils.isEmpty(synchronousUpdateVo)) {
            return DtoUtil.getFalseDto("本地上传数据未获取到", 25001);
        }
        if (synchronousUpdateVo.getDayEventsList().size() <= 0) {
            return DtoUtil.getFalseDto("事件集未获取到", 25002);
        }
        System.out.println(synchronousUpdateVo.toString());
        List<Integer> dayEventIds=new ArrayList<>();
        for (DayEvents dayEvents:synchronousUpdateVo.getDayEventsList()){
            dayEventIds.add(dayEvents.getDayEventId());
        }
        //查询时间段内的事件
        StringBuffer stringBuffer=null;
        String year=null;
        String month=null;
        String day=null;
//        List<SingleEvent> singleEvents=new ArrayList<>();
        System.out.println(dayEventIds.toString());
        SingleEvent singleEvent=new SingleEvent();
        for (int i = 0; i <dayEventIds.size() ; i++) {
            try {
                stringBuffer=new StringBuffer(dayEventIds.get(i).toString());
//            System.out.println(stringBuffer);
                year=stringBuffer.substring(0,4);
                month=stringBuffer.substring(4, 6);
                day=stringBuffer.substring(6, 8);
                singleEvent.setYear(Long.parseLong(year));
                singleEvent.setMonth(Long.parseLong(month));
                singleEvent.setDay(Long.parseLong(day));
                singleEvent.setUserid(Long.parseLong(synchronousUpdateVo.getUserId()));
            } catch (Exception e) {
                e.printStackTrace();
            }
/*            singleEvents=eventMapper.queryEvents(singleEvent);
            if (ObjectUtils.isEmpty(singleEvents)){
                return DtoUtil.getFalseDto("该时间段内没有事件",25003);
            }*/
        }
        //删除事件
        /*for (int i = 0; i <singleEvents.size() ; i++) {
            int delResult=eventMapper.withdrawEventsByUserId(singleEvents.get(i));
            if (delResult<=0){
                return DtoUtil.getFalseDto("删除失败",25004);
            }
        }*/
        System.out.println(singleEvent.toString());
        int updResult=eventMapper.updOldEvent(singleEvent);
        /*if (updResult<=0){
            return DtoUtil.getFalseDto("云端删除失败",25004);
        }*/
       //上传事件
        for (int i = 0; i < synchronousUpdateVo.getDayEventsList().size(); i++) {
            DayEvents dayEvents=synchronousUpdateVo.getDayEventsList().get(i);
            for (int j = 0; j < dayEvents.getMySingleEventList().size(); j++) {
                int uplResult=eventMapper.uploadingEvents(dayEvents.getMySingleEventList().get(j));
                if (uplResult<=0){
                    return DtoUtil.getFalseDto("上传事件失败",25005);
                }
            }
        }
        //修改时间戳
        Map map=new HashMap();
        try {
            String time=DateUtil.dateToStamp(new Date());
            int i=accountMapper.updateTimestampUnderAccount(synchronousUpdateVo.getUserId(),time);
            if (i<=0){
                return DtoUtil.getFalseDto("同步数据时修改时间戳失败",25006);
            }
            map.put("time",time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DtoUtil.getSuccesWithDataDto("数据同步成功",map,100000);
    }

    @Override
    public Dto contrastTimestamp(ContrastTimestampVo contrastTimestampVo) {
        if (ObjectUtils.isEmpty(contrastTimestampVo)){
            return DtoUtil.getFalseDto("时间戳获取失败",24001);
        }
        String time=accountMapper.queryTime(contrastTimestampVo.getUserId());
        if (StringUtils.isEmpty(time)){
            return DtoUtil.getFalseDto("查询时间戳失败",24003);
        }
        if(Long.parseLong(contrastTimestampVo.getTime())-Long.parseLong(time)<=3){
            return DtoUtil.getFalseDto("不需要同步",24002);
        }
       return DtoUtil.getSuccessDto("需要同步",100000);
    }

    @Override
    public Dto firstUplEvent(SynchronousUpdateVo synchronousUpdateVo) {
        if (ObjectUtils.isEmpty(synchronousUpdateVo)){
            return DtoUtil.getFalseDto("同步数据未获取到",26001);
        }
        if (eventMapper.queryEventByUserId(synchronousUpdateVo.getUserId())>0){
            return DtoUtil.getFalseDto("该用户已经上传过了",26003);
        }
        //遍历拆分
        for (DayEvents dayEvents:synchronousUpdateVo.getDayEventsList()) {
            for (SingleEvent singleEvent:dayEvents.getMySingleEventList()) {
                //上传
                if (eventMapper.uploadingEvents(singleEvent)<=0){
                    return DtoUtil.getFalseDto("同步上传失败",26002);
                }
            }
        }
//        LoopEvent loopEvent=new LoopEvent();
        List<String> list=new ArrayList();
        list.add("0");
        if (synchronousUpdateVo.getLoopEventList().size()>0){
            for (int i = 0; i < synchronousUpdateVo.getLoopEventList().size(); i++) {
                for (SingleEvent singleEvent:synchronousUpdateVo.getLoopEventList().get(i)) {
                    //重复事件添加
                    /*loopEvent.setAddress(singleEvent.getAddress());
                    loopEvent.setDay(singleEvent.getDay().toString());
                    loopEvent.setEventId(singleEvent.getEventid());
                    loopEvent.setEndTime(singleEvent.getEndtime());
                    loopEvent.setStartTime(singleEvent.getStarttime());
                    loopEvent.setEventName(singleEvent.getEventname());
                    loopEvent.setFlag(singleEvent.getFlag());
                    loopEvent.setIsOverdue(singleEvent.getIsOverdue());
                    loopEvent.setLevel(singleEvent.getLevel());
                    loopEvent.setPerson(singleEvent.getPerson());
                    loopEvent.setRemarks(singleEvent.getRemarks());
                    loopEvent.setRemindTime(singleEvent.getRemindTime());
                    loopEvent.setType(singleEvent.getType());
                    loopEvent.setUserId(singleEvent.getUserid());
                    loopEvent.setWeek(singleEvent.getRepeaTtime());*/
                    if (i!=0){
                        for (String eventId:list) {
                            if (!eventId.equals(singleEvent.getEventid().toString())){
                                if(eventMapper.uplLoopEvent(singleEvent)<=0){
                                    return DtoUtil.getFalseDto("重复事件上传失败",26004);
                                }
                            }
                        }
                    }else {
                        if(eventMapper.uplLoopEvent(singleEvent)<=0){
                            return DtoUtil.getFalseDto("重复事件上传失败",26004);
                        }
                    }
                    list.add(singleEvent.getEventid().toString());
                }
            }
        }
        return DtoUtil.getSuccessDto("数据同步成功",100000);
    }

    @Override
    public Dto uplDraft(DraftVo draftVo) {
        if (ObjectUtils.isEmpty(draftVo)){
            return DtoUtil.getFalseDto("上传草稿未获取到",27001);
        }
        //查看草稿是否已存在
        String data=eventMapper.queryDraftByPhone(draftVo.getPhoneNum());
        if (StringUtils.isEmpty(data)){
            //第一次上传草稿
            if (eventMapper.uplDraft(draftVo)<=0){
                return DtoUtil.getFalseDto("第一次上传草稿失败",27002);
            }
        }else {
            //不是第一次上传
            if (eventMapper.updateDraft(draftVo)<=0){
                return DtoUtil.getFalseDto("非第一次上传草稿失败",27003);
            }
        }
        return DtoUtil.getSuccessDto("上传草稿成功",100000);
    }

    @Override
    public Dto searchByDayEventIds(SearchEventVo searchEventVo) {
        if (!ObjectUtils.isEmpty(searchEventVo)) {
            System.out.println(searchEventVo.toString());
            SingleEvent singleEvent = SingleEventUtil.getSingleEvent(searchEventVo.getUserId(),searchEventVo.getDayEventId());
            //只根据level升序
            List<SingleEvent> singleEventListOrderByLevel = eventMapper.queryByDayOrderByLevel(singleEvent);
            //根据level和事件升序
            List<SingleEvent> singleEventListOrderByLevelAndDate = eventMapper.queryByDayOrderByLevelAndDate(singleEvent);
            //添加一个未排序的结果集到dayEvents中
            DayEvents dayEvents = new DayEvents();
            ArrayList<SingleEvent> singleEventList = eventMapper.queryEvents(singleEvent);
            dayEvents.setUserId(singleEvent.getUserid().intValue());
            dayEvents.setTotalNum(singleEventList.size());
            dayEvents.setDayEventId(Integer.valueOf(searchEventVo.getDayEventId()));
            dayEvents.setMySingleEventList(singleEventList);
            Map<String,Object> result = new HashMap<>();
            result.put("singleEventListOrderByLevel",singleEventListOrderByLevel);
            result.put("singleEventListOrderByLevelAndDate",singleEventListOrderByLevelAndDate);
            result.put("dayEvents",dayEvents);
            if (!ObjectUtils.isEmpty(result)) {
                return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
            }
            return DtoUtil.getFalseDto("查询失败", 21003);
        }
        return DtoUtil.getFalseDto("查询条件接收失败", 21004);
    }

    @Override
    public Dto searchByDayEventIdsInMonth(SearchEventVo searchEventVo) {
        if (!ObjectUtils.isEmpty(searchEventVo)) {
            SingleEvent singleEvent = SingleEventUtil.getSingleEvent(searchEventVo.getUserId(),searchEventVo.getDayEventId());
            //只根据level升序
            List<SingleEvent> singleEventListOrderByLevel = eventMapper.queryByMonthOrderByLevel(singleEvent);
            //根据level和事件升序
            List<SingleEvent> singleEventListOrderByLevelAndDate = eventMapper.queryByMonthOrderByLevelAndDate(singleEvent);
            Map<String,List<SingleEvent>> result = new HashMap<>();
            result.put("singleEventListOrderByLevel",singleEventListOrderByLevel);
            result.put("singleEventListOrderByLevelAndDate",singleEventListOrderByLevelAndDate);
            if (!ObjectUtils.isEmpty(result)) {
                return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
            }
            return DtoUtil.getFalseDto("查询失败", 21003);
        }
        return DtoUtil.getFalseDto("查询条件接收失败", 21004);
    }

    @Override
    public Dto addNewLoopEvents(UploadingEventVo uploadingEventVo) {
        if (!ObjectUtils.isEmpty(uploadingEventVo)){
            SingleEvent singleEvent = JSONObject.parseObject(uploadingEventVo.getSingleEvent(),SingleEvent.class);
            /*SingleEvent singleEvent = new SingleEvent();
            singleEvent.setEventid(uploadingEventVo.getEventid());
            singleEvent.setUserid(uploadingEventVo.getUserid());
            singleEvent.setEventname(uploadingEventVo.getEventname());
            singleEvent.setStarttime(uploadingEventVo.getStarttime());
            singleEvent.setEndtime(uploadingEventVo.getEndtime());
            singleEvent.setAddress(uploadingEventVo.getAddress());
            singleEvent.setLevel(uploadingEventVo.getLevel());
            singleEvent.setFlag(uploadingEventVo.getFlag());
            singleEvent.setPerson(uploadingEventVo.getPerson());
            singleEvent.setRemarks(uploadingEventVo.getRemarks());
            singleEvent.setRepeaTtime(uploadingEventVo.getRepeaTtime());
            singleEvent.setIsOverdue(uploadingEventVo.getIsOverdue());
            singleEvent.setRemindTime(uploadingEventVo.getRemindTime());
            singleEvent.setDay(uploadingEventVo.getDay());
            singleEvent.setMonth(uploadingEventVo.getMonth());
            singleEvent.setYear(uploadingEventVo.getYear());
            singleEvent.setType(uploadingEventVo.getType());*/
            singleEvent.setUserid(Long.valueOf(uploadingEventVo.getUserId()));
            if (eventMapper.uploadingLoopEvents(singleEvent) > 0){
                return DtoUtil.getSuccessDto("上传重复事件成功",100000);
            }
            return DtoUtil.getFalseDto("上传重复事件失败",21009);
        }
        return DtoUtil.getFalseDto("没有可上传的重复事件",21010);
    }

    @Override
    public Dto searchByDayEventIdsInWeek(SearchEventVo searchEventVo) {
        if (!ObjectUtils.isEmpty(searchEventVo)){
            SingleEvent singleEvent;
            List<DayEvents> dayEventsList = new ArrayList<>();
            for (int i = 0; i <= 6; i++){
                DayEvents dayEvents = new DayEvents();
                String dayEventId = String.valueOf(Integer.valueOf(searchEventVo.getDayEventId()) + i);
                singleEvent = SingleEventUtil.getSingleEvent(searchEventVo.getUserId(),dayEventId);
                dayEvents.setMySingleEventList(eventMapper.queryByWeekOrderByStartTime(singleEvent));
                dayEvents.setTotalNum(dayEvents.getMySingleEventList().size());
                dayEvents.setUserId(Integer.valueOf(searchEventVo.getUserId()));
                dayEvents.setDayEventId(Integer.valueOf(dayEventId));
                dayEventsList.add(dayEvents);
            }


            List<LoopEvent> loopEventListInDataBase = eventMapper.queryLoopEvents(searchEventVo.getUserId());
            Map<String,List<LoopEvent>> result = new HashMap<>();
            List<SingleEvent> monLoopEventList = new ArrayList<>();
            List<SingleEvent> tueLoopEventList = new ArrayList<>();
            List<SingleEvent> wedLoopEventList = new ArrayList<>();
            List<SingleEvent> thuLoopEventList = new ArrayList<>();
            List<SingleEvent> friLoopEventList = new ArrayList<>();
            List<SingleEvent> satLoopEventList = new ArrayList<>();
            List<SingleEvent> sunLoopEventList = new ArrayList<>();
            for (LoopEvent loopEvent : loopEventListInDataBase){
                SingleEvent event = new SingleEvent();
                String repeatTime = loopEvent.getRepeatTime();
                event.setEventid(loopEvent.getEventId());
                event.setUserid(loopEvent.getUserId());
                event.setEventname(loopEvent.getEventName());
                event.setStarttime(loopEvent.getStartTime());
                event.setEndtime(loopEvent.getEndTime());
                event.setAddress(loopEvent.getAddress());
                event.setLevel(loopEvent.getLevel());
                event.setFlag(loopEvent.getFlag());
                event.setPerson(loopEvent.getPerson());
                event.setRemarks(loopEvent.getRemarks());
                event.setRepeaTtime(repeatTime);
                event.setIsOverdue(loopEvent.getIsOverdue());
                event.setRemindTime(loopEvent.getRemindTime());
                event.setDay(loopEvent.getDay());
                event.setMonth(loopEvent.getMonth());
                event.setYear(loopEvent.getYear());
                event.setType(loopEvent.getType());
                String[] s = repeatTime.split(",");
                for (int i = 0; i <= 6; i++){
                    if (i == 0 && s[i].equals("true")){
                        monLoopEventList.add(event);
                    }
                    if (i == 1 && s[i].equals("true")){
                        tueLoopEventList.add(event);
                    }
                    if (i == 2 && s[i].equals("true")){
                        wedLoopEventList.add(event);
                    }
                    if (i == 3 && s[i].equals("true")){
                        thuLoopEventList.add(event);
                    }
                    if (i == 4 && s[i].equals("true")){
                        friLoopEventList.add(event);
                    }
                    if (i == 5 && s[i].equals("true")){
                        satLoopEventList.add(event);
                    }
                    if (i == 6 && s[i].equals("true")){
                        sunLoopEventList.add(event);
                    }
                }
            }
            return DtoUtil.getSuccesWithDataDto("查询成功", dayEventsList, 100000);
        }
        return DtoUtil.getFalseDto("查询条件接收失败",21004);
    }
}
