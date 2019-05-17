package com.modcreater.tmbiz.controller;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.eventvo.*;
import com.modcreater.tmbiz.service.EventService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
    public Dto uploadingEvents(@RequestBody UploadingEventVo uploadingEventVo, HttpServletRequest request) {
        return eventService.addNewEvents(uploadingEventVo,request.getHeader("token"));
    }

    /**
     * 修改事件状态
     *
     * @param deleteEventVo
     * @return
     */
    @RequestMapping(value = "del", method = RequestMethod.POST)
    public Dto deleteEvent(@RequestBody DeleteEventVo deleteEventVo, HttpServletRequest request) {
        return eventService.deleteEvents(deleteEventVo,request.getHeader("token"));
    }

    /**
     * 修改一条事件
     *
     * @param updateEventVo
     * @return
     */
    @RequestMapping(value = "upd", method = RequestMethod.POST)
    public Dto updateEvents(@RequestBody UpdateEventVo updateEventVo, HttpServletRequest request) {
        return eventService.updateEvents(updateEventVo,request.getHeader("token"));
    }

    /**
     * 同步本地数据
     *
     * @param synchronousUpdateVo
     * @return
     */
    /*@RequestMapping(value = "syup", method = RequestMethod.POST)
    public Dto synchronousUpdate(@RequestBody SynchronousUpdateVo synchronousUpdateVo) {
        return eventService.synchronousUpdate(synchronousUpdateVo);
    }*/

    /**
     * 对比时间戳
     * @param contrastTimestampVo
     * @return
     */
    @RequestMapping(value = "ctime",method = RequestMethod.POST)
    public Dto contrastTimestamp(@RequestBody ContrastTimestampVo contrastTimestampVo, HttpServletRequest request){
        return eventService.contrastTimestamp(contrastTimestampVo,request.getHeader("token"));
    }

    /**
     * 第一次登录事件数据同步
     * @param synchronousUpdateVo
     * @return
     */
    @RequestMapping(value = "firupl",method = RequestMethod.POST)
    public Dto firstUplEvent(@RequestBody SynchronousUpdateVo synchronousUpdateVo, HttpServletRequest request){
        return eventService.firstUplEvent(synchronousUpdateVo,request.getHeader("token"));
    }

    /**
     * 草稿上传
     * @param draftVo
     * @return
     */
    @RequestMapping(value = "upldraft",method = RequestMethod.POST)
    public Dto uplDraft(@RequestBody DraftVo draftVo, HttpServletRequest request){
        return eventService.uplDraft(draftVo,request.getHeader("token"));
    }


    /**
     * 根据"日"查找事件
     * @param searchEventVo
     * @return
     */
    @RequestMapping(value = "seabyday",method = RequestMethod.POST)
    public Dto searchByDay(@RequestBody SearchEventVo searchEventVo, HttpServletRequest request){
        return eventService.searchByDayEventIds(searchEventVo,request.getHeader("token"));
    }

    /**
     * 根据"月"查找事件
     * @param searchEventVo
     * @return
     */
    @RequestMapping(value = "seabymon",method = RequestMethod.POST)
    public Dto searchByMonth(@RequestBody SearchEventVo searchEventVo, HttpServletRequest request){
        return eventService.searchByDayEventIdsInMonth(searchEventVo,request.getHeader("token"));
    }

    /**
     * 根据"周"查找事件
     * @param searchEventVo
     * @return
     */
    @RequestMapping(value = "seabyweek",method = RequestMethod.POST)
    public Dto searchByWeek(@RequestBody SearchEventVo searchEventVo, HttpServletRequest request){
        return eventService.searchByDayEventIdsInWeek(searchEventVo,request.getHeader("token"));
    }
}
