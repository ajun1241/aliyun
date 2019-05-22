package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.SingleEvent;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/22 16:22
 */
@Mapper
public interface EventTemporaryMapper {

    /**
     * 把事件添加进临时表
     * @param singleEvent
     * @return
     */
    int addTempEvent(List<SingleEvent> singleEvent);

}
