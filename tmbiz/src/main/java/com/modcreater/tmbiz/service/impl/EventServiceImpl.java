package com.modcreater.tmbiz.service.impl;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.vo.DayEvents;
import com.modcreater.tmbiz.service.EventService;
import com.modcreater.tmdao.mapper.EventMapper;
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
