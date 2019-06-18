package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.databaseparam.EventStatusScan;
import com.modcreater.tmbeans.databaseparam.QueryEventsCondition;
import com.modcreater.tmbeans.databaseparam.UserEventsGroupByInWeek;
import com.modcreater.tmbeans.databaseresult.GetUserEventsGroupByType;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.utils.NaturalWeek;
import com.modcreater.tmbeans.vo.eventvo.DeleteEventVo;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedIdIsOverdue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-04-29
 * Time: 9:20
 */
@Mapper
public interface EventMapper {
    /**
     * 上传新事件
     *
     * @param singleEvent
     * @return
     */
    int uploadingEvents(SingleEvent singleEvent);

    /**
     * 根据用户ID,年月日以及时间开始结束时间判断数据库是否存在时间冲突的事件
     * @param singleEvent
     * @return
     */
    int countIdByDate(SingleEvent singleEvent);

    /**
     * 撤销事件
     *
     * @param deleteEventVo
     * @return
     */
    int withdrawEventsByUserId(DeleteEventVo deleteEventVo);
    /**
     * 更新事件
     *
     * @param singleEvent
     * @return
     */
    int alterEventsByUserId(SingleEvent singleEvent);

    /**
     * 查询事件(未完成的,单一事件)
     *
     * @param singleEvent
     * @return
     */
    ArrayList<SingleEvent> queryEvents(SingleEvent singleEvent);

    /**
     * 按天查询事件forIOS(事件状态可选,单一事件)
     * @param singleEvent
     * @return
     */
    ArrayList<SingleEvent> queryEventsByDayForIOS(SingleEvent singleEvent);

    /**
     * 根据用户id查询事件总数
     * @param userId
     * @return
     */
    int queryEventByUserId(String userId);

    /**
     * 上传草稿
     * @param singleEvent
     * @return
     */
    int uplDraft(SingleEvent singleEvent);

    /**
     * 查询草稿条数
     * @param userId
     * @param eventId
     * @return
     */
    int queryDraftCount(String userId,String eventId);

    /**
     * 修改草稿
     * @param draftVo
     * @return
     */
    int updateDraft(SingleEvent draftVo);
    /**
     * 根据"日"查找事件并根据事件等级(level)排序(未完成,单一事件)
     * @param singleEvent
     * @return
     */
    List<SingleEvent> queryByDayOrderByLevel(SingleEvent singleEvent);

    /**
     * 根据"日"查找事件并根据事件等级(level)和事件开始时间(startTime)排序(未完成,单一事件)
     * @param singleEvent
     * @return
     */
    List<SingleEvent> queryByDayOrderByLevelAndDate(SingleEvent singleEvent);

    /**
     * 查询重复事件(未完成)
     *
     * @param userId
     * @return
     */
    List<SingleEvent> queryLoopEvents(String userId);

    /**
     * 根据用户ID查询用户当月有哪些天存在事件
     * @param singleEvent
     * @return
     */
    List<Integer> queryDays(SingleEvent singleEvent);

    /**
     * 根据userId和eventId查询事件的开始时间和结束时间
     * @param singleEvent
     * @return
     */
    SingleEvent querySingleEventTime(SingleEvent singleEvent);

    /**
     * 根据userId和完成状态查询单一事件表
     * @param receivedIdIsOverdue
     * @return
     */
    List<SingleEvent> queryUserEventsByUserIdIsOverdue(ReceivedIdIsOverdue receivedIdIsOverdue);

    /**
     * 根据筛选条件查询事件
     * @param singleEvent
     * @return
     */
    List<SingleEvent> queryEventsByConditions(QueryEventsCondition singleEvent);

    /**
     * 统计单一事件类型数量及用时分钟总和
     * @param userId
     * @return
     */
    List<GetUserEventsGroupByType> getUserEventsGroupByType(String userId);

    /**
     * 获取单一事件表中用户创建类型最多的类型
     * @param userId
     * @return
     */
    Long getMaxSingleEventType(String userId);

    /**
     * 获取单一事件表中用户创建类型最少的类型
     * @param userId
     * @return
     */
    Long getMinSingleEventType(String userId);

    /**
     * 根据userId查询事件表中共有多少条有效(已完成)事件
     * @param userEventsGroupByInWeek
     * @return
     */
    Long countEvents(UserEventsGroupByInWeek userEventsGroupByInWeek);

    /**
     * 查询冲突事件
     * @param singleEvent
     * @return
     */
    List<SingleEvent> queryClashEventList(SingleEvent singleEvent);

    /**
     * 根据userId和eventId查询一条(未过期的)事件
     * @param userId
     * @param eventId
     * @return
     */
    SingleEvent queryEventOne(String userId,String eventId);

    /**
     * 查询所有草稿箱
     * @param singleEvent
     * @return
     */
    List<SingleEvent> queryDraft(QueryEventsCondition singleEvent);

    /**
     * 根据删除类型删除(普通事件草稿箱)
     * @param eventId
     * @param deleteType
     * @return
     */
    int deleteByDeleteType(@Param("eventId") Long eventId,@Param("deleteType") String deleteType,@Param("userId") String userId);

    /**
     * 查询一个草稿事件
     * @param userId
     * @param eventId
     * @return
     */
    SingleEvent queryDraftOne(String userId, String eventId);

    /**
     * 从事件表彻底删除事件
     * @param userId
     * @param eventId
     * @return
     */
    int deleteSingleEvent(String userId,String eventId);

    /**
     * 从草稿箱彻底删除事件
     *@param userId
     *@param eventId
     * @return
     */
    int deleteDraft(String userId,String eventId);


    /**
     * 查询已完成但没及时被修改的事件的数量
     * @param eventStatusScan
     * @return
     */
    List<Long> queryExpiredEvents(EventStatusScan eventStatusScan);


    /**
     * 修改已完成但没及时被修改的事件的数量
     * @param eventStatusScan
     * @return
     */
    Long updateExpiredEvents(EventStatusScan eventStatusScan);

    /**
     * 根据开始日期和结束日期查询事件数量
     * @param naturalWeek
     * @return
     */
    Long getEventsNum(NaturalWeek naturalWeek);

    /**
     * 查询前一周的事件类型统计
     * @param userEventsGroupByInWeek
     * @return
     */
    List<GetUserEventsGroupByType> getUserEventsGroupByTypeInWeek(UserEventsGroupByInWeek userEventsGroupByInWeek);

    /**
     * 查询和用户一起已完成的事件的好友
     * @param userId
     * @return
     */
    List<String> queryEventInBestFriends(String userId);

    /**
     * 根据日期查询已完成的事件
     * @param singleEvent
     * @return
     */
    List<SingleEvent> queryCompletedEvents(SingleEvent singleEvent);

    /**
     * 查询将要修改状态的事件的状态
     * @param deleteEventVo
     * @return
     */
    SingleEvent getChangingEventStatus(DeleteEventVo deleteEventVo);

    /**
     * 查询事件(只查询eventId,userId,eventName,startTime,endTime)
     * @param singleEvent
     * @return
     */
    List<SingleEvent> queryEventsWithFewInfo(SingleEvent singleEvent);

    /**
     * 查询重复事件(只查询eventId,userId,eventName,startTime,endTime)
     * @param friendId
     * @return
     */
    List<SingleEvent> queryLoopEventsWithFewInfo(String friendId);

    /**
     * 获取一条事件(未完成,已完成,普通事件,草稿箱)
     * @param userId
     * @param eventId
     * @param type
     * @return
     */
    SingleEvent getAEvent(@Param("userId") String userId,@Param("eventId") Long eventId,@Param("type") String type);

    /**
     * 查询所有重复事件
     * @return
     */
    List<SingleEvent> queryAllLoopEvent(Integer time);
}
