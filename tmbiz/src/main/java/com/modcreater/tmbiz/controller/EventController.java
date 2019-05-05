package com.modcreater.tmbiz.controller;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.vo.*;
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

    /**
     * 添加一条事件
     *
     * @param uploadingEventVo
     * @return
     */
    @RequestMapping(value = "upl", method = RequestMethod.POST)
    public Dto uploadingEvents(@RequestBody UploadingEventVo uploadingEventVo) {
        return eventService.addNewEvents(uploadingEventVo);
    }

    /**
     * 删除一条事件
     *
     * @param deleteEventVo
     * @return
     */
    @RequestMapping(value = "del", method = RequestMethod.POST)
    public Dto deleteEvent(@RequestBody DeleteEventVo deleteEventVo) {
        return eventService.deleteEvents(deleteEventVo);
    }

    /**
     * 修改一条事件
     *
     * @param updateEventVo
     * @return
     */
    @RequestMapping(value = "upd", method = RequestMethod.POST)
    public Dto updateEvents(@RequestBody UpdateEventVo updateEventVo) {
        return eventService.updateEvents(updateEventVo);
    }

    /**
     * 查询事件
     *
     * @param searchEventVo
     * @return
     */
    @RequestMapping(value = "sea", method = RequestMethod.POST)
    public Dto searchEvents(@RequestBody SearchEventVo searchEventVo) {
        return eventService.searchEvents(searchEventVo);
    }

    /**
     * 同步本地数据
     *
     * @param synchronousUpdateVo
     * @return
     */
    @RequestMapping(value = "syup", method = RequestMethod.POST)
    public Dto synchronousUpdate(@RequestBody SynchronousUpdateVo synchronousUpdateVo) {
        return eventService.synchronousUpdate(synchronousUpdateVo);
    }

    @RequestMapping(value = "ctime",method = RequestMethod.POST)
    public Dto contrastTimestamp(@RequestBody ContrastTimestampVo contrastTimestampVo){
        return null;
    }

    @RequestMapping(value = "test")
    public Dto test() {
        return eventService.addNewEvents(null);
    }


}
