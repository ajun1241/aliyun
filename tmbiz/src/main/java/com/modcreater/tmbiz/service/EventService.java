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
    Dto deleteEvents(DeleteEventVo deleteEventVo,String token);

    /**
     * 修改事件
     *
     * @param updateEventVo
     * @param token
     * @return
     */
    Dto updateEvents(UpdateEventVo updateEventVo,String token);

    /**
     * 同步本地数据到线上
     *
     * @param synchronousUpdateVo
     * @param token
     * @return
     */
    Dto synchronousUpdate(SynchronousUpdateVo synchronousUpdateVo,String token);

    /**
     * 对比时间戳
     *
     * @param contrastTimestampVo
     *@param token
     * @return
     */
    Dto contrastTimestamp(ContrastTimestampVo contrastTimestampVo,String token);

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
    Dto uplDraft(DraftVo draftVo,String token);
    /**
     * 根据日期查询事件并排序(单位:日)
     * @param searchEventVo
     * @param token
     * @return
     */
    Dto searchByDayEventIds(SearchEventVo searchEventVo,String token);

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
}
