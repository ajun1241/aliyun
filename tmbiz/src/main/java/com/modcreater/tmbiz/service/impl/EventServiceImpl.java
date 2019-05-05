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
import java.util.Iterator;
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
                return DtoUtil.getSuccessDto("事件上传成功", 100000);
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
                return DtoUtil.getFalseDto("删除成功", 100000);
            }
            return DtoUtil.getFalseDto("删除事件失败", 21005);
        }
        return DtoUtil.getFalseDto("删除条件接收失败", 21006);
    }

    @Override
    public Dto updateEvents(UpdateEventVo updateEventVo) {
        if (!ObjectUtils.isEmpty(updateEventVo)){
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
            if (eventMapper.alterEventsByUserId(singleEvent) > 0){
                return DtoUtil.getSuccessDto("修改成功",100000);
            }
            return DtoUtil.getFalseDto("修改事件失败",21007);
        }
        return DtoUtil.getFalseDto("修改条件接收失败",21008);
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
        /*List<String> startTime=null;
        for (DayEvents dayEvents:synchronousUpdateVo.getDayEventsList()) {
            for (SingleEvent s:dayEvents.getMySingleEventList()) {
                if (ObjectUtils.isEmpty(s)){
                    return DtoUtil.getFalseDto("单一事件为空",25003);
                }
                startTime.add(s.getStarttime());
            }
        }*/
        Iterator<DayEvents> dayEventsIterator=synchronousUpdateVo.getDayEventsList().iterator();
        while (dayEventsIterator.hasNext()){
            System.out.println(dayEventsIterator.next().toString());
            Iterator singleEvents=dayEventsIterator.next().getMySingleEventList().iterator();
            while (singleEvents.hasNext()){
                System.out.println(singleEvents.next().toString());
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
        singleEvent.setEventid(22L);
        singleEvent.setUserid(1L);
        singleEvent.setEventname("测试事件被修改");
        singleEvent.setStarttime("1557035000");
        singleEvent.setEndtime("1557035001");
        singleEvent.setAddress("测试地址被修改");
        singleEvent.setLevel(1L);
        singleEvent.setFlag(1L);
        singleEvent.setPerson("1,2,3,4,5,6,7");
        singleEvent.setRemarks("测试");
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
