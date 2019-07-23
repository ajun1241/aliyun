package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.SynchronHistory;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/7/23 15:12
 */
public interface SynchronHistoryMapper {

    /**
     * 新增同步历史
     *
     * synchronHistory
     * @return
     */
    int addSynchronHistory(SynchronHistory synchronHistory);

    /**
     * 修改状态
     * @param synchronHistory
     * @return
     */
    int updSynchronHistory(SynchronHistory synchronHistory);

    /**
     * 查询发起的邀请事件通过的数量
     * @param userId
     * @return
     */
    Long countSucceedSynchronHistory(String userId);

    /**
     * 查询发起的邀请事件被拒绝的数量
     * @param userId
     * @return
     */
    Long countFailedSynchronHistory(String userId);

    /**
     * 查询拒绝的邀请事件的数量
     * @param userId
     * @return
     */
    Long countRefusedSynchronHistory(String userId);

    /**
     * 查询同意的邀请事件的数量
     * @param userId
     * @return
     */
    Long countAgreedSynchronHistory(String userId);
}
