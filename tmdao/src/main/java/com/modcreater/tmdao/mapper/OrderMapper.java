package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.UserOrders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
     * @param orderType
     * @return
     */
    Double getPaymentAmount(@Param("serviceId") String serviceId,@Param("orderType") String orderType);

    /**
     * 生成新的订单
     * @param userOrders
     * @return
     */
    int addNewOrder(UserOrders userOrders);
}
