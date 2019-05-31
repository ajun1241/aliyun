package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.ServiceRemainingTime;
import com.modcreater.tmbeans.vo.trade.ReceivedOrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

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
}
