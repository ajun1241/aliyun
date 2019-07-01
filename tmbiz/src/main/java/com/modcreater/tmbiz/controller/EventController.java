package com.modcreater.tmbiz.controller;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.QueryMsgStatusVo;
import com.modcreater.tmbeans.vo.eventvo.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedDeleteEventIds;
import com.modcreater.tmbiz.config.annotation.GLOT;
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

    @RequestMapping(value = "upl", method = RequestMethod.POST)
    @ApiOperation("添加一个事件")
    public Dto uploadingEvents(@RequestBody UploadingEventVo uploadingEventVo, HttpServletRequest request) {
        return eventService.addNewEvents(uploadingEventVo,request.getHeader("token"));
    }

    @RequestMapping(value = "del", method = RequestMethod.POST)
    @ApiOperation("修改事件状态")
    public Dto deleteEvent(@RequestBody DeleteEventVo deleteEventVo, HttpServletRequest request) {
        return eventService.deleteEvents(deleteEventVo,request.getHeader("token"));
    }

    @RequestMapping(value = "upd", method = RequestMethod.POST)
    @ApiOperation("修改一个事件")
    public Dto updateEvents(@RequestBody UpdateEventVo updateEventVo, HttpServletRequest request) {
        return eventService.updateEvents(updateEventVo,request.getHeader("token"));
    }

    @RequestMapping(value = "firupl",method = RequestMethod.POST)
    @ApiOperation("第一次登陆事件同步")
    public Dto firstUplEvent(@RequestBody SynchronousUpdateVo synchronousUpdateVo, HttpServletRequest request){
        return eventService.firstUplEvent(synchronousUpdateVo,request.getHeader("token"));
    }

    @RequestMapping(value = "upldraft",method = RequestMethod.POST)
    @ApiOperation("草稿上传")
    public Dto uplDraft(@RequestBody DraftVo draftVo, HttpServletRequest request){
        return eventService.uplDraft(draftVo,request.getHeader("token"));
    }

    @RequestMapping(value = "upddraft",method = RequestMethod.POST)
    @ApiOperation("修改草稿")
    public Dto updDraft(@RequestBody AddInviteEventVo addInviteEventVo, HttpServletRequest request){
        return eventService.updDraft(addInviteEventVo,request.getHeader("token"));
    }

    @GLOT
    @RequestMapping(value = "seabyday",method = RequestMethod.POST)
    @ApiOperation("查询一天的事件")
    public Dto searchByDay(@RequestBody SearchEventVo searchEventVo, HttpServletRequest request){
        return eventService.searchByDayEventIds(searchEventVo,request.getHeader("token"));
    }

    @GLOT
    @RequestMapping(value = "seabymon",method = RequestMethod.POST)
    @ApiOperation("查询一个月的事件")
    public Dto searchByMonth(@RequestBody SearchEventVo searchEventVo, HttpServletRequest request){
        return eventService.searchByDayEventIdsInMonth(searchEventVo,request.getHeader("token"));
    }

    @GLOT
    @RequestMapping(value = "seabyweek",method = RequestMethod.POST)
    @ApiOperation("查询一周")
    public Dto searchByWeek(@RequestBody SearchEventVo searchEventVo, HttpServletRequest request){
        return eventService.searchByDayEventIdsInWeek(searchEventVo,request.getHeader("token"));
    }

    @GLOT
    @PostMapping(value = "seabyweekwithprivatepermission")
    @ApiOperation("查看好友的周")
    public Dto seaByWeekWithPrivatePermission(@RequestBody SearchEventVo searchEventVo,HttpServletRequest request){
        return eventService.seaByWeekWithPrivatePermission(searchEventVo,request.getHeader("token"));
    }

    @PostMapping(value = "searchfriendeventonce")
    @ApiOperation("查询好友的事件详情")
    public Dto searchFriendEventOnce(@RequestBody ReceivedFriendEventOnce receivedFriendEventOnce,HttpServletRequest request){
        return eventService.searchFriendEventOnce(receivedFriendEventOnce,request.getHeader("token"));
    }

    /**
     * 回应修改邀请事件
     * @param feedbackEventInviteVo
     * @return
     */
    @PostMapping(value = "feedbackeventinvite")
    @ApiOperation("回应修改邀请事件")
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
     * 回应新增邀请
     * @param feedbackInviteVo
     * @return
     */
    @PostMapping(value = "feedbackinvite")
    @ApiOperation("回应新增邀请")
    public Dto feedbackInvite(@RequestBody FeedbackInviteVo feedbackInviteVo, HttpServletRequest request){
        return eventService.feedbackInvite(feedbackInviteVo,request.getHeader("token"));
    }

    /**
     * 修改邀请事件
     * @param addInviteEventVo
     * @return
     */
    @PostMapping(value = "updinviteevent")
    @ApiOperation("修改邀请事件")
    public Dto updInviteEvent(@RequestBody AddInviteEventVo addInviteEventVo, HttpServletRequest request){
        return eventService.updInviteEvent(addInviteEventVo,request.getHeader("token"));
    }

    /**
     * 修改邀请事件创建者选择
     * @param eventCreatorChooseVo
     * @return
     */
    @PostMapping(value = "eventcreatorchoose")
    @ApiOperation("修改邀请事件创建者选择")
    public Dto eventCreatorChoose(@RequestBody EventCreatorChooseVo eventCreatorChooseVo, HttpServletRequest request){
        return eventService.eventCreatorChoose(eventCreatorChooseVo,request.getHeader("token"));
    }

    /**
     * 邀请事件添加成员
     * @param addInviterVo
     * @return
     */
    @PostMapping(value = "addinviter")
    @ApiOperation("邀请事件添加成员")
    public Dto addInviter(@RequestBody AddInviterVo addInviterVo, HttpServletRequest request){
        return eventService.addInviter(addInviterVo,request.getHeader("token"));
    }

    /**
     * 根据天条件查询forIOS
     * @param searchConditionsForIOS
     * @param request
     * @return
     */
    @GLOT
    @RequestMapping(value = "seabydayforios",method = RequestMethod.POST)
    public Dto searchByDayForIOS(@RequestBody SearchConditionsForIOS searchConditionsForIOS,HttpServletRequest request){
        return eventService.searchByDayForIOS(searchConditionsForIOS,request.getHeader("token"));
    }

    /**
     *添加一条事件支持
     * @param addbackerVo
     * @param request
     * @return
     */
    @PostMapping(value = "addeventbacker")
    @ApiOperation("添加一条事件支持")
    public Dto addEventBacker(@RequestBody AddBackerVo addbackerVo, HttpServletRequest request){
        return eventService.addEventBacker(addbackerVo,request.getHeader("token"));
    }

    /**
     *修改一条事件支持
     * @param addbackerVo
     * @param request
     * @return
     */
    @PostMapping(value = "updbackerevent")
    @ApiOperation("修改一条事件支持")
    public Dto updBackerEvent(@RequestBody AddBackerVo addbackerVo, HttpServletRequest request){
        return eventService.updBackerEvent(addbackerVo,request.getHeader("token"));
    }

    /**
     *删除一条事件支持
     * @param deleteEventVo
     * @param request
     * @return
     */
    @PostMapping(value = "delbackerevent")
    @ApiOperation("删除一条事件支持")
    public Dto delBackerEvent(@RequestBody DeleteEventVo deleteEventVo, HttpServletRequest request){
        return eventService.delBackerEvent(deleteEventVo,request.getHeader("token"));
    }

    /**
     *回应事件支持
     * @param feedbackEventBackerVo
     * @param request
     * @return
     */
    @PostMapping(value = "feedbackeventbacker")
    @ApiOperation("回应事件支持")
    public Dto feedbackEventBacker(@RequestBody FeedbackEventBackerVo feedbackEventBackerVo,HttpServletRequest request){
        return eventService.feedbackEventBacker(feedbackEventBackerVo,request.getHeader("token"));
    }

    /**
     *
     * @param receivedSearchOnce
     * @param request
     * @return
     */
    @PostMapping(value = "delinviteevent")
    @ApiOperation("删除邀请事件")
    public Dto delInviteEvent(@RequestBody ReceivedSearchOnce receivedSearchOnce,HttpServletRequest request){
        return eventService.delInviteEvent(receivedSearchOnce,request.getHeader("token"));
    }

    /**
     * 将事件从事件表移除到草稿箱
     * @param addInviteEventVo
     * @param request
     * @return
     */
    /*@PostMapping(value = "eventmovetodraft")
    @ApiOperation("将事件从事件表移除到草稿箱")
    public Dto eventRemoveDraft(@RequestBody AddInviteEventVo addInviteEventVo,HttpServletRequest request){
        return eventService.eventRemoveDraft(addInviteEventVo,request.getHeader("token"));
    }*/

    @GLOT
    @PostMapping(value = "searchonce")
    @ApiOperation("查询一个事件详情")
    public Dto searchOnce(@RequestBody ReceivedSearchOnce receivedSearchOnce,HttpServletRequest request){
        return eventService.searchOnce(receivedSearchOnce,request.getHeader("token"));
    }

    @GLOT
    @PostMapping(value = "searchdraftonce")
    @ApiOperation("查询一个草稿箱事件")
    public Dto searchDraftOnce(@RequestBody ReceivedSearchOnce receivedSearchOnce,HttpServletRequest request){
        return eventService.searchDraftOnce(receivedSearchOnce,request.getHeader("token"));
    }

    @PostMapping(value = "deleteinbatches")
    @ApiOperation("批量删除")
    public Dto deleteInBatches(@RequestBody ReceivedDeleteEventIds receivedDeleteEventIds, HttpServletRequest request){
        return eventService.deleteInBatches(receivedDeleteEventIds,request.getHeader("token"));
    }

    /**
     * 查询一条消息状态
     * @param queryMsgStatusVo
     * @param request
     * @return
     */
    @PostMapping(value = "querymsgstatus")
    @ApiOperation("查询一条消息状态")
    public Dto queryMsgStatus(@RequestBody QueryMsgStatusVo queryMsgStatusVo, HttpServletRequest request){
        return eventService.queryMsgStatus(queryMsgStatusVo,request.getHeader("token"));
    }

}
