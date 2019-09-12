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
     * 批量新增临时待办事项
     * @param backlogLists
     * @return
     */
    int insertTempBacklog(List<BacklogList> backlogLists);

    /**
     * 修改待办事项
     * @param backlogList
     * @return
     */
    int updateBacklog(BacklogList backlogList);

    /**
     * 查询待办事项
     * @param singleEventId
     * @return
     */
    List<BacklogList> queryBacklogList(Long singleEventId);

    /**
     * 根据id查询待办事项
     * @param id
     * @return
     */
    BacklogList queryBacklogListById(Long id);

    /**
     * 删除待办事项
     * @param id
     * @return
     */
    int deleteBacklog(Long id);

    /**
     * 增加清单
     * @param backlogList
     * @return
     */
    int addBacklog(BacklogList backlogList);

    /**
     * 草稿批量新增待办事项
     * @param backlogLists
     * @return
     */
    int insertDraftBacklog(List<BacklogList> backlogLists);

    /**
     * 草稿查询待办事项
     * @param singleEventId
     * @return
     */
    List<BacklogList> queryDraftBacklogList(Long singleEventId);

    /**
     * 删除草稿箱清单
     * @param singleEventId
     * @return
     */
    int deleteDraftBacklogList(Long singleEventId);

    /**
     * 根据事件Id修改同步状态
     * @param backlogList
     * @return
     */
    int updateBacklogIsSyncByEventId(BacklogList backlogList);

    /**
     * 根据事件Id修改临时状态
     * @param oldSingleEventId
     * @param newSingleEventId
     * @return
     */
    int updateBacklogIsTestByEventId(Long newSingleEventId,Long oldSingleEventId);

    /**
     * 查询临时清单
     * @param id
     * @return
     */
    List<BacklogList> queryTempBacklogList(Long id);

    /**
     * 删除临时清单
     * @param singleEventId
     * @return
     */
    int deleteTempBacklogList(Long singleEventId);
}
