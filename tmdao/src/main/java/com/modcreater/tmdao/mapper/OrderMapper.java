package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.UserOrders;
import com.modcreater.tmbeans.show.order.ShowUserOrders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-27
 * Time: 9:41
 */
@Mapper
public interface OrderMapper {

    /**
     * 获取订单金额
     * @param serviceId
     * @param serviceType
     * @return
     */
    Double getUnitPrice(@Param("serviceId") String serviceId,@Param("serviceType") String serviceType);

    /**
     * 生成新的订单
     * @param userOrders
     * @return
     */
    int addNewOrder(UserOrders userOrders);

    /**
     * 查询用户的一条订单
     * @param tradeId
     * @return
     */
    UserOrders getUserOrder(String tradeId);

    /**
     * 修改订单
     * @param userOrders
     * @return
     */
    int updateUserOrder(UserOrders userOrders);

    /**
     * 查询已过期但未被及时修改的订单
     * @param timestamp
     * @return
     */
    Long queryExpiredOrders(Long timestamp);

    /**
     * 修改已过期但未被及时修改的订单
     * @param timestamp
     * @return
     */
    Long updateExpiredOrders(Long timestamp);

    /**
     * 查询用户所有的订单
     * @param userId
     * @return
     */
    List<ShowUserOrders> getUserAllOrders(String userId);
}
