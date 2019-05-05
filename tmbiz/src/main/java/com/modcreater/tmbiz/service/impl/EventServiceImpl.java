package com.modcreater.tmbiz.service.impl;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.vo.*;
import com.modcreater.tmbiz.service.EventService;
import com.modcreater.tmdao.mapper.EventMapper;
import com.modcreater.tmutils.DateUtil;
import com.modcreater.tmutils.DtoUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Override
    public Dto addNewEvents(UploadingEventVo uploadingEventVo) {
        /*if (!ObjectUtils.isEmpty(uploadingEventVo)) {
            SingleEventForDatabase singleEventForDatabase = new SingleEventForDatabase();
            singleEventForDatabase.setEventId(uploadingEventVo.getEventid());
            singleEventForDatabase.setUserId(uploadingEventVo.getUserid());
            singleEventForDatabase.setEventName(uploadingEventVo.getEventname());
            singleEventForDatabase.setStartTime(uploadingEventVo.getStarttime());
            singleEventForDatabase.setEndTime(uploadingEventVo.getEndtime());
            singleEventForDatabase.setAddress(uploadingEventVo.getAddress());
            singleEventForDatabase.setLevel(uploadingEventVo.getLevel());
            singleEventForDatabase.setFlag(uploadingEventVo.getFlag());
            singleEventForDatabase.setPerson(uploadingEventVo.getPerson());
            singleEventForDatabase.setRemarks(uploadingEventVo.getRemarks());
            singleEventForDatabase.setRepeatTime(uploadingEventVo.getRepeaTtime());
            singleEventForDatabase.setIsOverdue(uploadingEventVo.getIsOverdue());
            singleEventForDatabase.setRemindTime(uploadingEventVo.getRemindTime());
            singleEventForDatabase.setDay(uploadingEventVo.getDay().intValue());
            singleEventForDatabase.setMonth(uploadingEventVo.getMonth().intValue());
            singleEventForDatabase.setYear(uploadingEventVo.getYear().intValue());
            singleEventForDatabase.setType(uploadingEventVo.getType().intValue());
            if (eventMapper.uploadingEvents(singleEventForDatabase) > 0) {
                return DtoUtil.getSuccessDto("事件上传成功", 100000);
            }
            return DtoUtil.getFalseDto("事件上传失败", 21001);
        }
        return DtoUtil.getFalseDto("没有可上传的事件", 21002);*/
        if (eventMapper.uploadingEvents(get()) > 0) {
            return DtoUtil.getSuccessDto("事件上传成功", 100000);
        }
        return DtoUtil.getFalseDto("事件上传失败", 21001);
    }

    @Override
    public Dto deleteEvents(DeleteEventVo deleteEventVo) {
        return null;
    }

    @Override
    public Dto updateEvents(UpdateEventVo updateEventVo) {
        return null;
    }

    @Override
    public Dto searchEvents(SearchEventVo searchEventVo) {
        /*if (!ObjectUtils.isEmpty(searchEventVo)){
            StringBuilder date = new StringBuilder(searchEventVo.getDayEventId());
            SingleEvent singleEvent = new SingleEvent();
            singleEvent.setYear(Long.valueOf(date.substring(0,4)));
            singleEvent.setMonth(Long.valueOf(date.substring(4,6)));
            singleEvent.setDay(Long.valueOf(date.substring(6,8)));
            singleEvent.setUserid(Long.valueOf(searchEventVo.getUserId()));
            List<SingleEvent> singleEventList = eventMapper.queryEvents(singleEvent);
            if (!ObjectUtils.isEmpty(singleEventList)){
                return DtoUtil.getSuccesWithDataDto("查询成功",singleEventList,100000);
            }
            return DtoUtil.getFalseDto("查询失败",21003);
        }
        return DtoUtil.getFalseDto("查询条件接收失败",21004);*/
        SingleEvent singleEvent = new SingleEvent();
        singleEvent.setYear(2019L);
        singleEvent.setMonth(5L);
        singleEvent.setDay(5L);
        singleEvent.setUserid(1L);
        List<SingleEvent> singleEventList = eventMapper.queryEvents(singleEvent);
        if (!ObjectUtils.isEmpty(singleEventList)){
            return DtoUtil.getSuccesWithDataDto("查询成功",singleEventList,100000);
        }
        return DtoUtil.getFalseDto("查询失败",21003);
    }

    @Override
    public Dto synchronousUpdate(SynchronousUpdateVo synchronousUpdateVo) {
        if (ObjectUtils.isEmpty(synchronousUpdateVo)){
            return DtoUtil.getFalseDto("本地上传数据未获取到",25001);
        }
        if (synchronousUpdateVo.getDayEventsList().size()<=0){
            return DtoUtil.getFalseDto("事件集未获取到",25002);
        }
         List<String> startTime=null;
        for (DayEvents dayEvents:synchronousUpdateVo.getDayEventsList()) {
            for (SingleEvent s:dayEvents.getMySingleEventList()) {
                if (ObjectUtils.isEmpty(s)){
                    return DtoUtil.getFalseDto("单一事件为空",25003);
                }
                startTime.add(s.getStarttime());
            }
        }

        return null;
    }

    @Override
    public Dto contrastTimestamp(ContrastTimestampVo contrastTimestampVo) {
        return null;
    }

    private static SingleEvent get() {
        SingleEvent singleEvent = new SingleEvent();
        singleEvent.setEventid(20L);
        singleEvent.setUserid(1L);
        singleEvent.setEventname("测试事件");
        singleEvent.setStarttime("1557035561");
        singleEvent.setEndtime("1557035562");
        singleEvent.setAddress("测试地址");
        singleEvent.setLevel(1L);
        singleEvent.setFlag(1L);
        singleEvent.setPerson("1");
        singleEvent.setRemarks("测试");
        singleEvent.setRepeaTtime("10");
        singleEvent.setIsOverdue(1L);
        singleEvent.setRemindTime("1557035561");
        singleEvent.setDay(5L);
        singleEvent.setMonth(5L);
        singleEvent.setYear(2019L);
        singleEvent.setType(1L);
        return singleEvent;
    }


    /*@Override
    public Dto synchronousUpdate(DayEvents dayEvents) {
        return null;
    }

    @Override
    public Dto addNewEvents(SingleEvent singleEvent) {
        if (!ObjectUtils.isEmpty(singleEvent)) {
            if (eventMapper.uploadingEvents(singleEvent.getAddress()) > 0) {
                return DtoUtil.getSuccessDto("事件上传成功", 100000);
            } else {
                return DtoUtil.getFalseDto("事件上传失败", 21001);
            }
        }
        return DtoUtil.getFalseDto("没有内容", 21002);
    }

    @Override
    public Dto deleteEvents(DayEvents dayEvents) {
        if (!ObjectUtils.isEmpty(dayEvents)) {
            if (eventMapper.withdrawEventsByUserId(dayEvents.getMySingleEventList()) > 0) {
                return DtoUtil.getSuccessDto("事件删除成功", 100000);
            } else {
                return DtoUtil.getFalseDto("事件删除失败", 21003);
            }
        }
        return DtoUtil.getFalseDto("没有内容", 21002);
    }

    @Override
    public Dto updateEvents(DayEvents dayEvents) {
        if (!ObjectUtils.isEmpty(dayEvents)) {
            if (eventMapper.alterEventsByUserId(dayEvents.getMySingleEventList()) > 0) {
                return DtoUtil.getSuccessDto("事件修改成功", 100000);
            } else {
                return DtoUtil.getFalseDto("事件修改失败", 21004);
            }
        }
        return DtoUtil.getFalseDto("没有内容", 21002);
    }

    @Override
    public Dto searchEvents(DayEvents dayEvents) {
        if (!ObjectUtils.isEmpty(dayEvents)){
            List<SingleEvent> singleEventList = eventMapper.queryEvents();
            if(!ObjectUtils.isEmpty(singleEventList)){
                return DtoUtil.getSuccesWithDataDto("查询成功",singleEventList,100000);
            }else {
                return DtoUtil.getFalseDto("查询失败",21005);
            }
        }
        return DtoUtil.getFalseDto("没有内容",21002);
    }*/
}
