package com.modcreater.tmbiz.service.impl;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.vo.*;
import com.modcreater.tmbiz.service.EventService;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmdao.mapper.EventMapper;
import com.modcreater.tmutils.DateUtil;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmutils.SingleEventUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

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
public class EventServiceImpl implements EventService {

    @Resource
    private EventMapper eventMapper;

    @Resource
    private AccountMapper accountMapper;

    @Override
    public Dto addNewEvents(UploadingEventVo uploadingEventVo) {
        /*if (!ObjectUtils.isEmpty(uploadingEventVo)) {
            SingleEvent singleEvent = SingleEventUtil.getSingleEvent(uploadingEventVo);
            if (eventMapper.uploadingEvents(singleEvent) > 0 && !ObjectUtils.isEmpty(singleEvent)) {
                try {
                    String time = DateUtil.dateToStamp(new Date());
                    if (accountMapper.updateTimestampUnderAccount(singleEvent.getUserid().toString(), time) > 0) {
                        Map<String,String> timestamp = new HashMap<>();
                        timestamp.put("time",time);
                        return DtoUtil.getSuccesWithDataDto("事件上传成功",timestamp, 100000);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return DtoUtil.getFalseDto("事件上传失败", 21001);
        }
        return DtoUtil.getFalseDto("没有可上传的事件", 21002);*/
        SingleEvent singleEvent = SingleEventUtil.getSingleEvent(uploadingEventVo);
        singleEvent.setEventid(100L);
        singleEvent.setUserid(10086L);
        singleEvent.setEventname("测试名称");
        singleEvent.setStarttime("1557109312");
        singleEvent.setEndtime("1557109312");
        singleEvent.setAddress("测试地址");
        singleEvent.setLevel(1L);
        singleEvent.setFlag(1L);
        singleEvent.setPerson("测试人物");
        singleEvent.setRemarks("测试备注");
        singleEvent.setRepeaTtime("2");
        singleEvent.setIsOverdue(0L);
        singleEvent.setRemindTime("1557109312");
        singleEvent.setDay(5L);
        singleEvent.setMonth(5L);
        singleEvent.setYear(2019L);
        singleEvent.setType(1L);
        if (eventMapper.uploadingEvents(singleEvent) > 0 && !ObjectUtils.isEmpty(singleEvent)) {
            try {
                String time = DateUtil.dateToStamp(new Date());
                if (accountMapper.updateTimestampUnderAccount(singleEvent.getUserid().toString(), time) > 0) {
                    Map<String,String> timestamp = new HashMap<>();
                    timestamp.put("time",time);
                    return DtoUtil.getSuccesWithDataDto("事件上传成功",timestamp, 100000);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return DtoUtil.getFalseDto("事件上传失败", 21001);
    }

    @Override
    public Dto deleteEvents(DeleteEventVo deleteEventVo) {
        if (!ObjectUtils.isEmpty(deleteEventVo)) {
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

            SingleEvent singleEvent = SingleEventUtil.getSingleEvent(updateEventVo);
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
            StringBuilder date = new StringBuilder(searchEventVo.getDayEventId());
            SingleEvent singleEvent = new SingleEvent();
            singleEvent.setYear(Long.valueOf(date.substring(0, 4)));
            singleEvent.setMonth(Long.valueOf(date.substring(4, 6)));
            singleEvent.setDay(Long.valueOf(date.substring(6, 8)));
            singleEvent.setUserid(Long.valueOf(searchEventVo.getUserId()));
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
        List<Integer> dayEventIds=new ArrayList<>();
        for (DayEvents dayEvents:synchronousUpdateVo.getDayEventsList()){
            dayEventIds.add(dayEvents.getDayEventId());
        }
        //查询时间段内的事件
        StringBuffer stringBuffer=null;
        String year=null;
        String month=null;
        String day=null;
        List<SingleEvent> singleEvents=new ArrayList<>();
        for (int i = 0; i <dayEventIds.size() ; i++) {
            stringBuffer=new StringBuffer(dayEventIds.get(i).toString());
//            System.out.println(stringBuffer);
            year=stringBuffer.substring(0,4);
            month=stringBuffer.substring(4, 6);
            day=stringBuffer.substring(6, 8);
            SingleEvent singleEvent=new SingleEvent();
            singleEvent.setYear(Long.parseLong(year));
            singleEvent.setMonth(Long.parseLong(month));
            singleEvent.setDay(Long.parseLong(day));
            singleEvents=eventMapper.queryEvents(singleEvent);
            if (ObjectUtils.isEmpty(singleEvents)){
                return DtoUtil.getFalseDto("该时间段内没有事件",25003);
            }
        }
        //删除事件
        for (int i = 0; i <singleEvents.size() ; i++) {
            int delResult=eventMapper.withdrawEventsByUserId(singleEvents.get(i));
            if (delResult<=0){
                return DtoUtil.getFalseDto("删除失败",25004);
            }
        }
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
        try {
            int i=accountMapper.updateTimestampUnderAccount(synchronousUpdateVo.getUserId(),DateUtil.dateToStamp(new Date()));
            if (i<=0){
                return DtoUtil.getFalseDto("同步数据时修改时间戳失败",25006);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DtoUtil.getSuccessDto("数据同步成功",100000);
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
}
