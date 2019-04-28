package com.modcreater.tmbiz.service.impl;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.vo.QueryEventVo;
import com.modcreater.tmbiz.service.EventService;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl implements EventService {
    @Override
    public Dto queryEvent(QueryEventVo queryEventVo) {
        return null;
    }

    @Override
    public Dto addEvent(SingleEvent singleEvent) {
        return null;
    }

    @Override
    public Dto deleteEvent(String eventId) {
        return null;
    }

    @Override
    public Dto updateEvent(String eventId) {
        return null;
    }
}
