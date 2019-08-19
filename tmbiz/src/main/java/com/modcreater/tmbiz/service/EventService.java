package com.modcreater.tmbiz.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.Draft;
import com.modcreater.tmbeans.vo.QueryMsgStatusVo;
import com.modcreater.tmbeans.vo.eventvo.*;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedDeleteEventIds;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-04-29
 * Time: 11:32
 */
public interface EventService {

    /**
     * 添加新的事件
     *
     * @param uploadingEventVo
     * @param token
     * @return
     */
    Dto addNewEvents(UploadingEventVo uploadingEventVo,String token);

    /**
     * 删除事件
     *
     * @param deleteEventVo
     * @param token
     * @return
     */
    Dto deleteEvents(DeleteEventVo deleteEventVo, String token);

    /**
     * 修改事件
     *
     * @param updateEventVo
     * @param token
     * @return
     */
    Dto updateEvents(UpdateEventVo updateEventVo,String token);

    /**
     * 第一次同步
     * @param synchronousUpdateVo
     * @param token
     * @return
     */
    Dto firstUplEvent(SynchronousUpdateVo synchronousUpdateVo,String token);

    /**
     * 上传草稿
     * @param draftVo
     * @param token
     * @return
     */
    Dto uplDraft(DraftVo draftVo, String token);

    /**
     * 修改一个草稿
     * @param addInviteEventVo
     * @param token
     * @return
     */
    Dto updDraft(AddInviteEventVo addInviteEventVo,String token);

    /**
     * 根据日期查询事件并排序(单位:日)
     * @param searchEventVo
     * @param token
     * @return
     */
    Dto searchByDayEventIds(SearchEventVo searchEventVo, String token);

    /**
     * 根据日期查询事件并排序(单位:月)
     * @param searchEventVo
     * @param token
     * @return
     */
    Dto searchByDayEventIdsInMonth(SearchEventVo searchEventVo,String token);

    /**
     * 根据日期查询事件并排序(单位:周)
     * @param searchEventVo
     * @param token
     * @return
     */
    Dto searchByDayEventIdsInWeek(SearchEventVo searchEventVo,String token);

    /**
     * 添加一条邀请事件
     * @param addInviteEventVo
     * @param token
     * @return
     */
    Dto addInviteEvent(AddInviteEventVo addInviteEventVo,String token);

    /**
     * 新增邀请事件回应邀请
     * @param feedbackInviteVo
     * @param token
     * @return
     */
    Dto feedbackInvite(FeedbackInviteVo feedbackInviteVo,String token);

    /**
     * 修改一条邀请事件
     * @param addInviteEventVo
     * @param token
     * @return
     */
    Dto updInviteEvent(AddInviteEventVo addInviteEventVo,String token);

    /**
     * 回应邀请事件修改
     * @param feedbackEventInviteVo
     * @param token
     * @return
     */
    Dto feedbackEventInvite(FeedbackEventInviteVo feedbackEventInviteVo,String token);

    /**
     * 修改事件时创建者选择
     * @param eventCreatorChooseVo
     * @param token
     * @return
     */
    Dto eventCreatorChoose(EventCreatorChooseVo eventCreatorChooseVo,String token);

    /**
     * 删除一条邀请事件
     * @param receivedSearchOnce
     * @param token
     * @return
     */
    Dto delInviteEvent(ReceivedSearchOnce receivedSearchOnce,String token);

    /**
     * 根据天条件查询forIOS
     * @param searchConditionsForIOS
     * @param token
     * @return
     */
    Dto searchByDayForIOS(SearchConditionsForIOS searchConditionsForIOS, String token);

    /**
     * 根据"周"查询事件排序并带有用户是否给予他人查看权限
     * @param searchEventVo
     * @param token
     * @return
     */
    Dto seaByWeekWithPrivatePermission(SearchEventVo searchEventVo,String token);

    /**
     * 查询一个事件
     * @param receivedSearchOnce
     * @param token
     * @return
     */
    Dto searchOnce(ReceivedSearchOnce receivedSearchOnce, String token);

    /**
     * 根据Id的数组批量删除事件
     * @param receivedDeleteEventIds
     * @param token
     * @return
     */
    Dto deleteInBatches(ReceivedDeleteEventIds receivedDeleteEventIds, String token);

    /**
     * 查询一个草稿事件
     * @param receivedSearchOnce
     * @param token
     * @return
     */
    Dto searchDraftOnce(ReceivedSearchOnce receivedSearchOnce, String token);

    /**
     * 将事件从事件表移除到草稿箱
     * @param addInviteEventVo
     * @param token
     * @return
     */
    Dto eventRemoveDraft(AddInviteEventVo addInviteEventVo,String token);

    /**
     * 查询一条消息的状态（暂用邀请事件）
     * @param queryMsgStatusVo
     * @param token
     * @return
     */
    Dto queryMsgStatus(QueryMsgStatusVo queryMsgStatusVo, String token);

    /**
     * 查询好友的事件详情
     * @param receivedFriendEventOnce
     * @param token
     * @return
     */
    Dto searchFriendEventOnce(ReceivedFriendEventOnce receivedFriendEventOnce, String token);

    /**
     * 查询今天的计划
     * @param receivedId 接收参数
     * @param token token
     * @return 工具类
     */
    Dto getTodayPlans(ReceivedId receivedId, String token);

    /**
     * 判断邀请事件最高权限
     * @param receivedSearchOnce
     * @param token
     * @return
     */
    Dto inviteEventJudge(ReceivedSearchOnce receivedSearchOnce,String token);

    /**
     * 上传草稿为普通事件
     * @param draftToEventVo
     * @param token
     * @return
     */
    Dto draftToSingleEvent(DraftToEventVo draftToEventVo,String token);

    /**
     * 上传草稿为邀请事件
     * @param draftToEventVo
     * @param token
     * @return
     */
    Dto draftToInviteEvent(DraftToEventVo draftToEventVo,String token);

    /**
     * 查询用户草稿箱所有草稿
     * @param receivedId
     * @param token
     * @return
     */
    Dto getAllDrafts(ReceivedId receivedId, String token);

    /**
     * 获取今天计划(IOS)
     * @param receivedId
     * @param token
     * @return
     */
    Dto searchByWeekForIos(ReceivedId receivedId, String token);
}
