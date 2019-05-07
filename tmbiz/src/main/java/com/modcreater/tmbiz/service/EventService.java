package com.modcreater.tmbiz.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.*;

import java.util.List;

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
     * @return
     */
    Dto addNewEvents(UploadingEventVo uploadingEventVo);

    /**
     * 删除事件
     *
     * @param deleteEventVo
     * @return
     */
    Dto deleteEvents(DeleteEventVo deleteEventVo);

    /**
     * 修改事件
     *
     * @param updateEventVo
     * @return
     */
    Dto updateEvents(UpdateEventVo updateEventVo);

    /**
     * 查询事件
     *
     * @param searchEventVo
     * @return
     */
    Dto searchEvents(SearchEventVo searchEventVo);

    /**
     * 同步本地数据到线上
     *
     * @param synchronousUpdateVo
     * @return
     */
    Dto synchronousUpdate(SynchronousUpdateVo synchronousUpdateVo);

    /**
     * 对比时间戳
     *
     * @param contrastTimestampVo
     * @return
     */
    Dto contrastTimestamp(ContrastTimestampVo contrastTimestampVo);

    /**
     * 第一次同步
     * @param synchronousUpdateVo
     * @return
     */
    Dto firstUplEvent(SynchronousUpdateVo synchronousUpdateVo);

    /**
     * 上传草稿
     * @param draftVo
     * @return
     */
    Dto uplDraft(DraftVo draftVo);
    /**
     * 根据日期查询事件并排序(单位:日)
     * @param searchEventVo
     * @return
     */
    Dto searchByDayEventIds(SearchEventVo searchEventVo);

    /**
     * 根据日期查询事件并排序(单位:月)
     * @param searchEventVo
     * @return
     */
    Dto searchByDayEventIdsInMonth(SearchEventVo searchEventVo);

    /**
     * 添加一个重复事件
     * @param uploadingEventVo
     * @return
     */
    Dto addNewLoopEvents(UploadingEventVo uploadingEventVo);

    /**
     * 根据日期查询事件并排序(单位:周)
     * @param searchEventVo
     * @return
     */
    Dto searchByDayEventIdsInWeek(SearchEventVo searchEventVo);
}
