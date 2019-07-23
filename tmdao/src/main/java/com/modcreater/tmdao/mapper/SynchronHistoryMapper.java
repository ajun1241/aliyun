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




}
