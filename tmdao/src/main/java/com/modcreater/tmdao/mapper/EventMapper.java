package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.databaseparam.QueryEventsCondition;
import com.modcreater.tmbeans.databaseresult.GetUserEventsGroupByType;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.show.userinfo.ShowCompletedEvents;
import com.modcreater.tmbeans.vo.eventvo.DeleteEventVo;
import com.modcreater.tmbeans.vo.eventvo.DraftVo;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedEventConditions;
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
     * 查询事件
     *
     * @param singleEvent
     * @return
     */
    ArrayList<SingleEvent> queryEvents(SingleEvent singleEvent);

    /**
     * 按天查询事件forIOS
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
     * 根据"日"查找事件并根据事件等级(level)排序
     * @param singleEvent
     * @return
     */
    List<SingleEvent> queryByDayOrderByLevel(SingleEvent singleEvent);

    /**
     * 根据"日"查找事件并根据事件等级(level)和事件开始时间(startTime)排序
     * @param singleEvent
     * @return
     */
    List<SingleEvent> queryByDayOrderByLevelAndDate(SingleEvent singleEvent);

    /**
     * 添加一个重复事件
     * @param singleEvent
     * @return
     */
    int uploadingLoopEvents(SingleEvent singleEvent);

    /**
     * 查询重复事件
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
     * 根据userId查询事件表中共有多少条有效(未完成和已完成1/2)事件
     * @param userId
     * @return
     */
    Long countEvents(String userId);

    /**
     * 查询冲突事件
     * @param singleEvent
     * @return
     */
    List<SingleEvent> queryClashEventList(SingleEvent singleEvent);

    /**
     * 根据userId和eventId查询一条事件
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
    int deleteByDeleteType(@Param("eventId") Long eventId,@Param("deleteType") String deleteType);

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
}
