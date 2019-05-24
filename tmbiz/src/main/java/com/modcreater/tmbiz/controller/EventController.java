package com.modcreater.tmbiz.controller;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.eventvo.*;
import com.modcreater.tmbiz.service.EventService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 根据"周"查询事件排序并带有用户是否给予他人查看权限
     * @return
     */
    @PostMapping(value = "seabyweekwithprivatepermission")
    public Dto seaByWeekWithPrivatePermission(@RequestBody SearchEventVo searchEventVo,HttpServletRequest request){
        return eventService.seaByWeekWithPrivatePermission(searchEventVo,request.getHeader("token"));
    }

    /**
     * 回应一条邀请事件
     * @param feedbackEventInviteVo
     * @return
     */
    @PostMapping(value = "feedbackeventinvite")
    @ApiOperation("回应一条邀请事件")
    public Dto feedbackEventInvite(@RequestBody FeedbackEventInviteVo feedbackEventInviteVo, HttpServletRequest request){
        return eventService.feedbackEventInvite(feedbackEventInviteVo,request.getHeader("token"));
    }
    /**
     * 添加一条邀请事件
     * @param addInviteEventVo
     * @return
     */
    @PostMapping(value = "addinviteevent")
    @ApiOperation("添加一条邀请事件")
    public Dto addInviteEvent(@RequestBody AddInviteEventVo addInviteEventVo, HttpServletRequest request){
        return eventService.addInviteEvent(addInviteEventVo,request.getHeader("token"));
    }

    /**
     * 根据天条件查询forIOS
     * @param searchConditionsForIOS
     * @param request
     * @return
     */
    @RequestMapping(value = "seabydayforios",method = RequestMethod.POST)
    public Dto searchByDayForIOS(@RequestBody SearchConditionsForIOS searchConditionsForIOS,HttpServletRequest request){
        return eventService.searchByDayForIOS(searchConditionsForIOS,request.getHeader("token"));
    }
}
