package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.LoopEvent;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmbeans.vo.DraftVo;
import com.modcreater.tmbeans.vo.SingleEventForDatabase;
import com.modcreater.tmbeans.vo.UploadingEventVo;
import org.apache.ibatis.annotations.Mapper;

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
     * 撤销事件
     *
     * @param singleEvent
     * @return
     */
    int withdrawEventsByUserId(SingleEvent singleEvent);

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
     * 同步修改
     * @param singleEvent
     * @return
     */
    int updOldEvent(SingleEvent singleEvent);

    /**
     * 根据用户id查询事件
     * @param userId
     * @return
     */
    int queryEventByUserId(String userId);

    /**
     * 上传草稿
     * @param draftVo
     * @return
     */
    int uplDraft(DraftVo draftVo);

    /**
     * 查询草稿
     * @param phone
     * @return
     */
    String queryDraftByPhone(String phone);

    /**
     * 第二次上传修改草稿
     * @param draftVo
     * @return
     */
    int updateDraft(DraftVo draftVo);
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
     * 重复事件上传
     */
    int uplLoopEvent(SingleEvent singleEvent);
    /**
     * 根据"月"查找事件并根据事件
     * @param singleEvent
     * @return
     */
    List<SingleEvent> queryByDayEventIdsInMonth(SingleEvent singleEvent);

    /**
     * 添加一个重复事件
     * @param singleEvent
     * @return
     */
    int uploadingLoopEvents(SingleEvent singleEvent);

    /**
     * 撤销一个重复事件
     *
     * @param loopEvent
     * @return
     */
    int withdrawLoopEventsByUserId(LoopEvent loopEvent);

    /**
     * 更新一个重复事件
     *
     * @param loopEvent
     * @return
     */
    int alterLoopEventsByUserId(LoopEvent loopEvent);

    /**
     * 查询重复事件
     *
     * @param userId
     * @return
     */
    List<LoopEvent> queryLoopEvents(String userId);

    /**
     * 根据"周"查找事件并根据事件开始时间(startTime)排序
     * @param singleEvent
     * @return
     */
    ArrayList<SingleEvent> queryByWeekOrderByStartTime(SingleEvent singleEvent);

    /**
     * 根据用户ID查询用户当月有哪些天存在事件
     * @param singleEvent
     * @return
     */
    List<Integer> queryDays(SingleEvent singleEvent);


}
