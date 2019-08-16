package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.BacklogList;

import java.util.List;

/**
 * Description: 事件待办事项操作
 *
 * @Author: AJun
 * @Date: 2019/8/16 10:09
 */
public interface BacklogMapper {

    /**
     * 批量新增待办事项
     * @param backlogLists
     * @return
     */
    int insertBacklog(List<BacklogList> backlogLists);

    /**
     * 修改待办事项
     * @param backlogList
     * @return
     */
    int updateBacklog(BacklogList backlogList);

    /**
     * 查询待办事项
     * @param eventId
     * @param userId
     * @return
     */
    List<BacklogList> queryBacklogList(String userId,String eventId);

    /**
     * 删除待办事项
     * @param id
     * @return
     */
    int deleteBacklog(String id);
}
