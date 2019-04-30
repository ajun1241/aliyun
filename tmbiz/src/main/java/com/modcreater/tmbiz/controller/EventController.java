package com.modcreater.tmbiz.controller;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.vo.DayEvents;
import com.modcreater.tmbeans.vo.UploadingEventVo;
import com.modcreater.tmbiz.service.EventService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-04-29
 * Time: 14:54
 */
@RestController
@RequestMapping(value = "/event/")
public class EventController {

    @Resource
    private EventService eventService;

    @RequestMapping(value = "upl", method = RequestMethod.POST)
    public Dto uploadingEvents(@RequestBody UploadingEventVo uploadingEventVo) {
        return eventService.addNewEvents(uploadingEventVo);
    }

    @RequestMapping(value = "del", method = RequestMethod.POST)
    public Dto deleteEvent(@RequestBody DayEvents dayEvents) {
        return eventService.deleteEvents(dayEvents);
    }

    @RequestMapping(value = "upd", method = RequestMethod.POST)
    public Dto updateEvents(@RequestBody DayEvents dayEvents) {
        return eventService.updateEvents(dayEvents);
    }

    @RequestMapping(value = "sea", method = RequestMethod.POST)
    public Dto searchEvents(@RequestBody DayEvents dayEvents) {
        return eventService.searchEvents(dayEvents);
    }

    @RequestMapping(value = "syup", method = RequestMethod.POST)
    public Dto synchronousUpdate(@RequestBody DayEvents dayEvents) {
        return null;
    }

    @RequestMapping(value = "test")
    public Dto test() {
        return eventService.updateEvents(new DayEvents());
    }


}
