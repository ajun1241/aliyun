package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.TimedTask;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/6/4 8:45
 */
@Mapper
public interface TimedTaskMapper {
    /**
     * 新增定时任务
     * @param timedTask
     * @return
     */
    int addTimedTask(TimedTask timedTask);

    /**
     * 查询定时任务
     * @param timedTask
     * @return
     */
    TimedTask queryTimedTask(TimedTask timedTask);

    /**
     * 更改定时任务
     * @param timedTask
     * @return
     */
    int updateTimedTask(TimedTask timedTask);

    /**
     * 查询待执行的支持者任务
     * @return
     */
    List<TimedTask> queryWaitExecute();
}
