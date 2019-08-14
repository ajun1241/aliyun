package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.DiscountCoupon;
import com.modcreater.tmbeans.pojo.DiscountUser;
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

    /**
     * 修改订单状态
     * @param outTradeNo
     * @param status
     * @return
     */
    int updateOrderStatus(@Param("outTradeNo") String outTradeNo,@Param("status") int status);

    /**
     * 根据优惠券ID查询优惠券
     * @param discountId
     * @return
     */
    DiscountCoupon getDiscountCoupon(String discountId);

    /**
     * 根据优惠券ID查询优惠券
     * @param discountUserId
     * @return
     */
    DiscountUser getDiscountUser(String discountUserId);

    /**
     * 设置用户使用优惠券后生成的订单号并将优惠券状态改为已使用
     * @param discountUserId
     * @param orderId
     * @param status
     * @return
     */
    int setDiscountCouponOrderId(@Param("discountUserId")Long discountUserId, @Param("orderId")String orderId, @Param("status")String status);

    /**
     * 用户支付成功后修改优惠券状态
     * @param orderId
     * @return
     */
    int updateDiscountStatus(String orderId);

    /**
     * 查询正在与订单绑定的优惠券
     * @return
     */
    List<DiscountUser> getBindingDiscountCoupons();
}
