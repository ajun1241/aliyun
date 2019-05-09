package com.modcreater.tmbiz.service.impl;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.LoopEvent;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.show.ShowSingleEvent;
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
            if (StringUtils.isEmpty(uploadingEventVo.getSingleEvent())) {
                return DtoUtil.getFalseDto("上传事件列表为空", 21012);
            }
            System.out.println("上传" + uploadingEventVo.toString());
            if (StringUtils.isEmpty(uploadingEventVo.getUserId())) {
                return DtoUtil.getFalseDto("请先登录", 21011);
            }
            SingleEvent singleEvent = JSONObject.parseObject(uploadingEventVo.getSingleEvent(), SingleEvent.class);
            System.out.println("是不是空=" + singleEvent);
            singleEvent.setUserid(Long.valueOf(uploadingEventVo.getUserId()));
            if (!ObjectUtils.isEmpty(singleEvent) && eventMapper.uploadingEvents(singleEvent) > 0) {
                try {
                    String time = DateUtil.dateToStamp(new Date());
                    if (accountMapper.updateTimestampUnderAccount(singleEvent.getUserid().toString(), time) > 0) {
                        Map<String, String> timestamp = new HashMap<>();
                        timestamp.put("time", time);
                        return DtoUtil.getSuccesWithDataDto("事件上传成功", timestamp, 100000);
                    } else {
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
            System.out.println("删除" + deleteEventVo.toString());
            if (StringUtils.isEmpty(deleteEventVo.getUserId())) {
                return DtoUtil.getFalseDto("请先登录", 21011);
            }
            SingleEvent singleEvent = new SingleEvent();
            singleEvent.setUserid(Long.valueOf(deleteEventVo.getUserId()));
            singleEvent.setEventid(Long.valueOf(deleteEventVo.getEventId()));
            if (eventMapper.withdrawEventsByUserId(singleEvent) > 0) {
                try {
                    String time = DateUtil.dateToStamp(new Date());
                    if (accountMapper.updateTimestampUnderAccount(singleEvent.getUserid().toString(), time) > 0) {
                        Map<String, String> timestamp = new HashMap<>();
                        timestamp.put("time", time);
                        return DtoUtil.getSuccesWithDataDto("删除成功", timestamp, 100000);
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
            System.out.println("修改" + updateEventVo.toString());
            if (StringUtils.isEmpty(updateEventVo.getUserId())) {
                return DtoUtil.getFalseDto("请先登录", 21011);
            }
            SingleEvent singleEvent = JSONObject.parseObject(updateEventVo.getSingleEvent(), SingleEvent.class);
            singleEvent.setUserid(Long.valueOf(updateEventVo.getUserId()));
            if (eventMapper.alterEventsByUserId(singleEvent) > 0 && !ObjectUtils.isEmpty(singleEvent)) {
                try {
                    String time = DateUtil.dateToStamp(new Date());
                    if (accountMapper.updateTimestampUnderAccount(singleEvent.getUserid().toString(), time) > 0) {
                        Map<String, String> timestamp = new HashMap<>();
                        timestamp.put("time", time);
                        return DtoUtil.getSuccesWithDataDto("修改成功", timestamp, 100000);
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
            System.out.println("查询单一" + searchEventVo.toString());
            if (StringUtils.isEmpty(searchEventVo.getUserId())) {
                return DtoUtil.getFalseDto("请先登录", 21011);
            }
            SingleEvent singleEvent = SingleEventUtil.getSingleEvent(searchEventVo.getUserId(), searchEventVo.getDayEventId());
            List<SingleEvent> singleEventList = eventMapper.queryEvents(singleEvent);
            if (!ObjectUtils.isEmpty(singleEventList)) {
                List<ShowSingleEvent> showSingleEventList = SingleEventUtil.getShowSingleEventList(singleEventList);
                return DtoUtil.getSuccesWithDataDto("查询成功", showSingleEventList, 100000);
            }
            return DtoUtil.getFalseDto("查询失败,没有数据", 200000);
        }
        return DtoUtil.getFalseDto("查询条件接收失败", 21004);
    }

    @Override
    public Dto synchronousUpdate(SynchronousUpdateVo synchronousUpdateVo) {
        if (ObjectUtils.isEmpty(synchronousUpdateVo)) {
            return DtoUtil.getFalseDto("本地上传数据未获取到", 25001);
        }
        System.out.println("本地数据上传" + synchronousUpdateVo.toString());
        if (StringUtils.isEmpty(synchronousUpdateVo.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (synchronousUpdateVo.getDayEventsList().size() <= 0) {
            return DtoUtil.getFalseDto("事件集未获取到", 25002);
        }
        List<Integer> dayEventIds = new ArrayList<>();
        for (DayEvents dayEvents : synchronousUpdateVo.getDayEventsList()) {
            dayEventIds.add(dayEvents.getDayEventId());
        }
        //查询时间段内的事件
        StringBuffer stringBuffer = null;
        String year = null;
        String month = null;
        String day = null;
//        List<SingleEvent> singleEvents=new ArrayList<>();
        System.out.println(dayEventIds.toString());
        SingleEvent singleEvent = new SingleEvent();
        /*            singleEvents=eventMapper.queryEvents(singleEvent);
            if (ObjectUtils.isEmpty(singleEvents)){
                return DtoUtil.getFalseDto("该时间段内没有事件",25003);
            }*/
        for (int i = 0; i < dayEventIds.size(); i++) {
            try {
                stringBuffer = new StringBuffer(dayEventIds.get(i).toString());
//            System.out.println(stringBuffer);
                year = stringBuffer.substring(0, 4);
                month = stringBuffer.substring(4, 6);
                day = stringBuffer.substring(6, 8);
                singleEvent.setYear(Long.parseLong(year));
                singleEvent.setMonth(Long.parseLong(month));
                singleEvent.setDay(Long.parseLong(day));
                singleEvent.setUserid(Long.parseLong(synchronousUpdateVo.getUserId()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //删除事件
        /*for (int i = 0; i <singleEvents.size() ; i++) {
            int delResult=eventMapper.withdrawEventsByUserId(singleEvents.get(i));
            if (delResult<=0){
                return DtoUtil.getFalseDto("删除失败",25004);
            }
        }*/
        System.out.println(singleEvent.toString());
        int updResult = eventMapper.updOldEvent(singleEvent);
        /*if (updResult<=0){
            return DtoUtil.getFalseDto("云端删除失败",25004);
        }*/
        //上传事件
        for (int i = 0; i < synchronousUpdateVo.getDayEventsList().size(); i++) {
            DayEvents<SingleEvent> dayEvents = synchronousUpdateVo.getDayEventsList().get(i);
            for (int j = 0; j < dayEvents.getMySingleEventList().size(); j++) {
                int uplResult = eventMapper.uploadingEvents(dayEvents.getMySingleEventList().get(j));
                if (uplResult <= 0) {
                    return DtoUtil.getFalseDto("上传事件失败", 25005);
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
        }
        return DtoUtil.getSuccesWithDataDto("数据同步成功", map, 100000);
    }

    @Override
    public Dto contrastTimestamp(ContrastTimestampVo contrastTimestampVo) {
        if (ObjectUtils.isEmpty(contrastTimestampVo)) {
            return DtoUtil.getFalseDto("时间戳获取失败", 24001);
        }
        if (StringUtils.isEmpty(contrastTimestampVo.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        String time = accountMapper.queryTime(contrastTimestampVo.getUserId());
        if (StringUtils.isEmpty(time)) {
            return DtoUtil.getFalseDto("查询时间戳失败", 24003);
        }
        if (Long.parseLong(contrastTimestampVo.getTime()) - Long.parseLong(time) <= 3) {
            return DtoUtil.getFalseDto("不需要同步", 24002);
        }
        return DtoUtil.getSuccessDto("需要同步", 100000);
    }

    @Override
    public Dto firstUplEvent(SynchronousUpdateVo synchronousUpdateVo) {
        if (ObjectUtils.isEmpty(synchronousUpdateVo)) {
            return DtoUtil.getFalseDto("同步数据未获取到", 26001);
        }
        System.out.println("第一次上传" + synchronousUpdateVo.toString());
        if (StringUtils.isEmpty(synchronousUpdateVo.getUserId())) {
            return DtoUtil.getFalseDto("请先登录", 21011);
        }
        if (eventMapper.queryEventByUserId(synchronousUpdateVo.getUserId()) > 0) {
            return DtoUtil.getFalseDto("该用户已经上传过了", 26003);
        }
        //遍历拆分
        for (DayEvents<SingleEvent> dayEvents : synchronousUpdateVo.getDayEventsList()) {
            for (SingleEvent singleEvent : dayEvents.getMySingleEventList()) {
                //上传
                if (eventMapper.uploadingEvents(singleEvent) <= 0) {
                    return DtoUtil.getFalseDto("同步上传失败", 26002);
                }
            }
        }
//        LoopEvent loopEvent=new LoopEvent();
        List<String> list = new ArrayList();
        list.add("0");
        if (synchronousUpdateVo.getLoopEventList().size() > 0) {
            for (int i = 0; i < synchronousUpdateVo.getLoopEventList().size(); i++) {
                for (SingleEvent singleEvent : synchronousUpdateVo.getLoopEventList().get(i)) {
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
                    if (i != 0) {
                        for (String eventId : list) {
                            if (!eventId.equals(singleEvent.getEventid().toString())) {
                                if (eventMapper.uplLoopEvent(singleEvent) <= 0) {
                                    return DtoUtil.getFalseDto("重复事件上传失败", 26004);
                                }
                            }
                        }
                    } else {
                        if (eventMapper.uplLoopEvent(singleEvent) <= 0) {
                            return DtoUtil.getFalseDto("重复事件上传失败", 26004);
                        }
                    }
                    list.add(singleEvent.getEventid().toString());
                }
            }
        }
        return DtoUtil.getSuccessDto("数据同步成功", 100000);
    }

    @Override
    public Dto uplDraft(DraftVo draftVo) {
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
    public Dto searchByDayEventIds(SearchEventVo searchEventVo) {
        if (!ObjectUtils.isEmpty(searchEventVo)) {
            if (StringUtils.isEmpty(searchEventVo.getUserId())) {
                return DtoUtil.getFalseDto("请先登录", 21011);
            }
            System.out.println("按天查" + searchEventVo.toString());
            boolean b = false;
            SingleEvent singleEvent = SingleEventUtil.getSingleEvent(searchEventVo.getUserId(), searchEventVo.getDayEventId());
            //只根据level升序
            List<SingleEvent> singleEventListOrderByLevel = eventMapper.queryByDayOrderByLevel(singleEvent);
            List<ShowSingleEvent> showSingleEventListOrderByLevel = new ArrayList<>();
            //根据level和事件升序
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
                b = true;
            }
            dayEvents.setUserId(singleEvent.getUserid().intValue());
            dayEvents.setTotalNum(singleEventList.size());
            dayEvents.setDayEventId(Integer.valueOf(searchEventVo.getDayEventId()));
            dayEvents.setMySingleEventList(showSingleEventList);
            Map<String, Object> result = new HashMap<>();
            result.put("ShowSingleEventListOrderByLevel", showSingleEventListOrderByLevel);
            result.put("ShowSingleEventListOrderByLevelAndDate", showSingleEventListOrderByLevelAndDate);
            result.put("dayEvents", dayEvents);
            if (b) {
                return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
            }
            return DtoUtil.getFalseDto("查询失败,没有数据", 200000);
        }
        return DtoUtil.getFalseDto("查询条件接收失败", 21004);
    }

    @Override
    public Dto searchByDayEventIdsInMonth(SearchEventVo searchEventVo) {
        if (!ObjectUtils.isEmpty(searchEventVo)) {
            System.out.println("按月查" + searchEventVo.toString());
            if (StringUtils.isEmpty(searchEventVo.getUserId())) {
                return DtoUtil.getFalseDto("请先登录", 21011);
            }
            SingleEvent singleEvent = SingleEventUtil.getSingleEvent(searchEventVo.getUserId(), searchEventVo.getDayEventId());
            /*List<SingleEvent> singleEventList = eventMapper.queryByDayEventIdsInMonth(singleEvent);
            List<DayEvents<ShowSingleEvent>> dayEventsList = new ArrayList<>();
            List<Integer> days = eventMapper.queryUserId(singleEvent);
            if (singleEventList.size() != 0) {
                b = true;
            }

            for (SingleEvent singleEvent1 : singleEventList) {
                DayEvents<ShowSingleEvent> dayEvents = new DayEvents<>();
                ArrayList<ShowSingleEvent> showSingleEventList = new ArrayList<>();
                dayEvents.setUserId(singleEvent.getUserid().intValue());
                StringBuilder dayEventId = new StringBuilder();
                dayEventId.append(singleEvent1.getYear());
                if (singleEvent1.getMonth() < 10) {
                    dayEventId.append(0);
                }
                dayEventId.append(singleEvent1.getMonth());
                String day1 = "00";
                for (Integer day : days) {
                    if (singleEvent1.getDay().toString().equals(day.toString())) {
                        if (day < 10) {
                            dayEventId.append(0);
                        }
                        day1 = day.toString();
                        showSingleEventList.add(SingleEventUtil.getShowSingleEvent(singleEvent1));
                    }
                }
                dayEventId.append(day1);
                dayEvents.setDayEventId(Integer.valueOf(dayEventId.toString()));
                dayEvents.setTotalNum(showSingleEventList.size());
                dayEvents.setMySingleEventList(showSingleEventList);
                dayEventsList.add(dayEvents);

            }*/
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

    @Override
    public Dto addNewLoopEvents(UploadingEventVo uploadingEventVo) {
        if (!ObjectUtils.isEmpty(uploadingEventVo)) {
            System.out.println("添加重复事件" + uploadingEventVo.toString());
            if (StringUtils.isEmpty(uploadingEventVo.getUserId())) {
                return DtoUtil.getFalseDto("请先登录", 21011);
            }
            SingleEvent singleEvent = JSONObject.parseObject(uploadingEventVo.getSingleEvent(), SingleEvent.class);
            singleEvent.setUserid(Long.valueOf(uploadingEventVo.getUserId()));
            if (eventMapper.uploadingLoopEvents(singleEvent) > 0) {
                return DtoUtil.getSuccessDto("上传重复事件成功", 100000);
            }
            return DtoUtil.getFalseDto("上传重复事件失败", 21009);
        }
        return DtoUtil.getFalseDto("没有可上传的重复事件", 21010);
    }

    @Override
    public Dto searchByDayEventIdsInWeek(SearchEventVo searchEventVo) {
        if (!ObjectUtils.isEmpty(searchEventVo)) {
            System.out.println("按周查" + searchEventVo.toString());
            if (StringUtils.isEmpty(searchEventVo.getUserId())) {
                return DtoUtil.getFalseDto("请先登录", 21011);
            }
            //按周查询单一事件
            SingleEvent singleEvent;
            List<DayEvents> dayEventsList = new ArrayList<>();
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
            List<LoopEvent> loopEventListInDataBase = eventMapper.queryLoopEvents(searchEventVo.getUserId());
            Map result = new HashMap<>();
            List<List<ShowSingleEvent>> loopEventList = new ArrayList<>();
            //创建七个几个代表一周七天
            List<ShowSingleEvent> sunShowLoopEventList = new ArrayList<>();
            List<ShowSingleEvent> monShowLoopEventList = new ArrayList<>();
            List<ShowSingleEvent> tueShowLoopEventList = new ArrayList<>();
            List<ShowSingleEvent> wedShowLoopEventList = new ArrayList<>();
            List<ShowSingleEvent> thuShowLoopEventList = new ArrayList<>();
            List<ShowSingleEvent> friShowLoopEventList = new ArrayList<>();
            List<ShowSingleEvent> satShowLoopEventList = new ArrayList<>();
            for (LoopEvent loopEvent : loopEventListInDataBase) {
                ShowSingleEvent showSingleEvent = new ShowSingleEvent();
                Boolean[] booleans = new Boolean[7];
                String[] s = loopEvent.getRepeatTime().split(",");
                for (int i = 0; i <= 6; i++) {
                    booleans[i] = "true".equals(s[i]);
                }
                showSingleEvent.setEventid(loopEvent.getEventId());
                showSingleEvent.setUserid(loopEvent.getUserId());
                showSingleEvent.setEventname(loopEvent.getEventName());
                showSingleEvent.setStarttime(loopEvent.getStartTime());
                showSingleEvent.setEndtime(loopEvent.getEndTime());
                showSingleEvent.setAddress(loopEvent.getAddress());
                showSingleEvent.setLevel(loopEvent.getLevel());
                showSingleEvent.setFlag(loopEvent.getFlag());
                showSingleEvent.setPerson(loopEvent.getPerson());
                showSingleEvent.setRemarks(loopEvent.getRemarks());
                showSingleEvent.setRepeaTtime(booleans);
                showSingleEvent.setIsOverdue(loopEvent.getIsOverdue());
                showSingleEvent.setRemindTime(loopEvent.getRemindTime());
                showSingleEvent.setDay(loopEvent.getDay());
                showSingleEvent.setMonth(loopEvent.getMonth());
                showSingleEvent.setYear(loopEvent.getYear());
                showSingleEvent.setType(loopEvent.getType());
                System.out.println(showSingleEvent);
                //根据拆分出来的boolean数组进行判断并添加到一周的各个天数中
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
            result.put("dayEventsList", dayEventsList);
            result.put("loopEventList", loopEventList);
            return DtoUtil.getSuccesWithDataDto("查询成功", result, 100000);
        }
        return DtoUtil.getFalseDto("查询条件接收失败", 21004);
    }

}
