package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.SingleEventVice;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/29 9:52
 */
@Mapper
public interface EventViceMapper {

    /**
     * 新增事件附属表
     * @param singleEventVice
     * @return
     */
    int createEventVice(SingleEventVice singleEventVice);

    /**
     * 修改事件附属表
     * @param singleEventVice
     * @return
     */
    int updateEventVice(SingleEventVice singleEventVice);

    /**
     * 查询事件附属表
     * @param singleEventVice
     * @return
     */
    SingleEventVice queryEventVice(SingleEventVice singleEventVice);

    /**
     * 删除事件附属表
     * @param singleEventVice
     * @return
     */
    int deleteEventVice(SingleEventVice singleEventVice);
}