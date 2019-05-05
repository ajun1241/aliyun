package com.modcreater.tmbiz.service.impl;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.vo.*;
import com.modcreater.tmbiz.service.EventService;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmdao.mapper.EventMapper;
import com.modcreater.tmutils.DateUtil;
import com.modcreater.tmutils.DtoUtil;
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
        if (!ObjectUtils.isEmpty(uploadingEventVo)) {
            SingleEvent singleEvent = new SingleEvent();
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
            singleEvent.setType(uploadingEventVo.getType());
            if (eventMapper.uploadingEvents(singleEvent) > 0) {
                try {
                    if (accountMapper.updateTimestampUnderAccount(singleEvent.getUserid().toString(), DateUtil.dateToStamp(new Date())) > 0) {
                        return DtoUtil.getSuccessDto("事件上传成功", 100000);
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
            SingleEvent singleEvent = new SingleEvent();
            singleEvent.setUserid(Long.valueOf(deleteEventVo.getUserId()));
            singleEvent.setEventid(Long.valueOf(deleteEventVo.getEventId()));
            if (eventMapper.withdrawEventsByUserId(singleEvent) > 0) {
                try {
                    if (accountMapper.updateTimestampUnderAccount(singleEvent.getUserid().toString(), DateUtil.dateToStamp(new Date())) > 0) {
                        return DtoUtil.getFalseDto("删除成功", 100000);
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
            SingleEvent singleEvent = new SingleEvent();
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
            singleEvent.setType(updateEventVo.getType());
            if (eventMapper.alterEventsByUserId(singleEvent) > 0) {
                try {
                    if (accountMapper.updateTimestampUnderAccount(singleEvent.getUserid().toString(), DateUtil.dateToStamp(new Date())) > 0) {
                        return DtoUtil.getSuccessDto("修改成功", 100000);
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
            return DtoUtil.getFalseDto("查询时间戳失败",200000);
        }
        Map map=new HashMap();
        map.put("time",time);
        return DtoUtil.getSuccesWithDataDto("查询时间戳成功",map,100000);
    }

    private static SingleEvent get() {
        SingleEvent singleEvent = new SingleEvent();
        singleEvent.setEventid(22L);
        singleEvent.setUserid(100019L);
        singleEvent.setEventname("测试事件修改时间戳");
        singleEvent.setStarttime("1557035000");
        singleEvent.setEndtime("1557035001");
        singleEvent.setAddress("测试地址修改时间戳");
        singleEvent.setLevel(1L);
        singleEvent.setFlag(1L);
        singleEvent.setPerson("1,2,3,4,5,6,7");
        singleEvent.setRemarks("测试修改时间戳");
        singleEvent.setRepeaTtime("10");
        singleEvent.setIsOverdue(2L);
        singleEvent.setRemindTime("1557035000");
        singleEvent.setDay(4L);
        singleEvent.setMonth(4L);
        singleEvent.setYear(2014L);
        singleEvent.setType(2L);
        return singleEvent;
    }
}
