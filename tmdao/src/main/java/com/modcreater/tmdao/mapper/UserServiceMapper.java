package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.EventMsg;
import com.modcreater.tmbeans.pojo.ServiceRemainingTime;
import com.modcreater.tmbeans.vo.trade.ReceivedOrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-27
 * Time: 17:36
 */
@Mapper
public interface UserServiceMapper {

    /**
     * 获取用户服务剩余时间
     * @param userId
     * @param serviceId
     * @return
     */
    Long getTimeRemaining(@Param("userId") String userId,@Param("serviceId") String serviceId);

    /**
     * 查询用户服务剩余
     * @param userId
     * @param serviceId
     * @return
     */
    ServiceRemainingTime getServiceRemainingTime(@Param("userId") String userId,@Param("serviceId") String serviceId);

    /**
     * 添加一条用户服务剩余时间
     * @param serviceRemainingTime
     * @return
     */
    int addNewServiceRemainingTime(ServiceRemainingTime serviceRemainingTime);

    /**
     * 修改用户服务剩余时间
     * @param time
     * @return
     */
    int updateServiceRemainingTime(ServiceRemainingTime time);

    /**
     * 查询用户所有服务
     * @param userId
     * @return
     */
    List<ServiceRemainingTime> getAllServiceRemainingTime(String userId);

    /**
     * 查询用户历史消息
     * @param userId
     * @return
     */
    List<EventMsg> getHistoryMsgList(String userId);

    /**
     * 根据条件查询一个月的事件数量
     * @param userId
     * @param thisMonth
     * @param thisYear
     * @param isOverdue
     * @return
     */
    int countAMonthEvents(@Param("userId") String userId,@Param("thisMonth") int thisMonth,@Param("thisYear") int thisYear,@Param("isOverdue") String isOverdue);

    /**
     * 根据serviceId查询用户次卡剩余
     * @param userId
     * @param serviceId
     * @return
     */
    Long getTimeCard(@Param("userId")String userId, @Param("serviceId")String serviceId);
}
