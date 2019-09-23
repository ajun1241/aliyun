package com.modcreater.tmbiz.controller;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.QueryMsgStatusVo;
import com.modcreater.tmbeans.vo.eventvo.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedDeleteEventIds;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmbiz.config.annotation.GLOT;
import com.modcreater.tmbiz.config.annotation.Safety;
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

    @Safety
    @RequestMapping(value = "upl", method = RequestMethod.POST)
    @ApiOperation("添加一个事件")
    public Dto uploadingEvents(@RequestBody UploadingEventVo uploadingEventVo, HttpServletRequest request) {
        return eventService.addNewEvents(uploadingEventVo,request.getHeader("token"));
    }

    @Safety
    @RequestMapping(value = "del", method = RequestMethod.POST)
    @ApiOperation("修改事件状态")
    public Dto deleteEvent(@RequestBody DeleteEventVo deleteEventVo, HttpServletRequest request) {
        return eventService.deleteEvents(deleteEventVo,request.getHeader("token"));
    }

    @Safety
    @RequestMapping(value = "upd", method = RequestMethod.POST)
    @ApiOperation("修改一个事件")
    public Dto updateEvents(@RequestBody UploadingEventVo updateEventVo, HttpServletRequest request) {
        return eventService.updateEvents(updateEventVo,request.getHeader("token"));
    }

    @Safety
    @RequestMapping(value = "firupl",method = RequestMethod.POST)
    @ApiOperation("第一次登陆事件同步")
    public Dto firstUplEvent(@RequestBody SynchronousUpdateVo synchronousUpdateVo, HttpServletRequest request){
        return eventService.firstUplEvent(synchronousUpdateVo,request.getHeader("token"));
    }

    @Safety
    @RequestMapping(value = "upldraft",method = RequestMethod.POST)
    @ApiOperation("草稿上传")
    public Dto uplDraft(@RequestBody DraftVo draftVo, HttpServletRequest request){
        return eventService.uplDraft(draftVo,request.getHeader("token"));
    }

    @Safety
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

    @Safety
    @GLOT
    @PostMapping(value = "searchbyweekforios")
    @ApiOperation("查询我的一周")
    public Dto searchByWeekForIos(@RequestBody SeaByWeekForIOS seaByWeekForIOS, HttpServletRequest request){
        return eventService.searchByWeekForIos(seaByWeekForIOS,request.getHeader("token"));
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
    @Safety
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
    @Safety
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
    @Safety
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
    @Safety
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
    @Safety
    @PostMapping(value = "eventcreatorchoose")
    @ApiOperation("修改邀请事件创建者选择")
    public Dto eventCreatorChoose(@RequestBody EventCreatorChooseVo eventCreatorChooseVo, HttpServletRequest request){
        return eventService.eventCreatorChoose(eventCreatorChooseVo,request.getHeader("token"));
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
     *
     * @param receivedSearchOnce
     * @param request
     * @return
     */
    @Safety
    @PostMapping(value = "delinviteevent")
    @ApiOperation("删除邀请事件")
    public Dto delInviteEvent(@RequestBody ReceivedSearchOnce receivedSearchOnce,HttpServletRequest request){
        return eventService.delInviteEvent(receivedSearchOnce,request.getHeader("token"));
    }

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

    @Safety
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

    @GLOT
    @PostMapping(value = "gettodayplans")
    @ApiOperation("查询今天的计划")
    public Dto getTodayPlans(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return eventService.getTodayPlans(receivedId,request.getHeader("token"));
    }

    @PostMapping(value = "inviteeventjudge")
    @ApiOperation("判断邀请事件的最高权限")
    public Dto inviteEventJudge(@RequestBody ReceivedSearchOnce receivedSearchOnce, HttpServletRequest request){
        return eventService.inviteEventJudge(receivedSearchOnce,request.getHeader("token"));
    }

    /**
     * 保存草稿为普通事件
     * @param draftToEventVo
     * @param request
     * @return
     */
    @Safety
    @PostMapping(value = "draftToSingleEvent")
    @ApiOperation("保存草稿为普通事件")
    public Dto draftToSingleEvent(@RequestBody DraftToEventVo draftToEventVo, HttpServletRequest request){
        return eventService.draftToSingleEvent(draftToEventVo,request.getHeader("token"));
    }

    /**
     * 保存草稿为邀请事件
     * @param draftToEventVo
     * @param request
     * @return
     */
    @Safety
    @PostMapping(value = "draftToInviteEvent")
    @ApiOperation("保存草稿为邀请事件")
    public Dto draftToInviteEvent(@RequestBody DraftToEventVo draftToEventVo, HttpServletRequest request){
        return eventService.draftToInviteEvent(draftToEventVo,request.getHeader("token"));
    }

    @PostMapping(value = "getalldrafts")
    @ApiOperation("获取用户草稿箱所有数据")
    public Dto getAllDrafts(@RequestBody ReceivedId receivedId, HttpServletRequest request){
        return eventService.getAllDrafts(receivedId,request.getHeader("token"));
    }

    @PostMapping(value = "earlyeventtermination")
    @ApiOperation("提前完成事件")
    public Dto earlyEventTermination(@RequestBody EarlyEventTermination earlyEventTermination,HttpServletRequest request){
        return eventService.earlyEventTermination(earlyEventTermination,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "updateBacklogList")
    @ApiOperation("修改清单状态")
    public Dto updateBacklogList(@RequestBody BacklogListVo backlogListVo,HttpServletRequest request){
        return eventService.updateBacklogList(backlogListVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "updateBacklogListDetail")
    @ApiOperation("修改清单内容")
    public Dto updateBacklogListDetail(@RequestBody BacklogListVo backlogListVo,HttpServletRequest request){
        return eventService.updateBacklogListDetail(backlogListVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "addbackloglist")
    @ApiOperation("在事件中添加清单")
    public Dto addBacklogList(@RequestBody BacklogListVo backlogListVo,HttpServletRequest request){
        return eventService.addBacklogList(backlogListVo,request.getHeader("token"));
    }

    @Safety
    @PostMapping(value = "deletebackloglist")
    @ApiOperation("在事件中删除清单")
    public Dto deleteBacklogList(@RequestBody BacklogListVo backlogListVo,HttpServletRequest request){
        return eventService.deleteBacklogList(backlogListVo,request.getHeader("token"));
    }
}
