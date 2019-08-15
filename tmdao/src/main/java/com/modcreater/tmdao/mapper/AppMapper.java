package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-03
 * Time: 9:04
 */
@Mapper
public interface AppMapper {

    /**
     * 获取APP版本信息
     * @return
     */
    List<String> getAppVersion();

    /**
     * 更新APP更新人数
     * @param updateTimes
     * @param uploadTime
     * @return
     */
    int updateUpdateTimes(Long updateTimes,Date uploadTime);

    /**
     * 获取用户被通知信息
     * @param userId
     * @return
     */
    UserNotice getUserNotice(String userId);

    /**
     * 添加一条用户通知信息
     * @param userId
     * @return
     */
    int addUserNotice(@Param("userId") String userId,@Param("noticeName")String noticeName);

    /**
     * 获取通知文本
     * @param noticeTypeId
     * @param noticeName
     * @param date
     * @return
     */
    String getNoticeContent(@Param("noticeTypeId") String noticeTypeId,@Param("noticeName") String noticeName,@Param("date") String date);

    /**
     * 修改用户通知信息
     * @param user
     * @return
     */
    int updateUserNotice(UserNotice user);

    /**
     *
     * @param appver
     * @return
     */
    String getAppUrl(String appver);

    /**
     * 获取活动公告
     * @param now
     * @return
     */
    List<ActivityTable> queryActivityTable(Long now);

    /**
     * 领取优惠券
     * @param discountUser
     * @return
     */
    int getDiscountCoupon(DiscountUser discountUser);

    /**
     * 查询优惠券详情
     * @param discountId
     * @return
     */
    DiscountCoupon queryDiscountCoupon(String discountId);

    /**
     * 查询用户优惠券详情
     * @param userId
     * @param discountId
     * @return
     */
    DiscountUser queryDiscountUser(String userId, String discountId);

    /**
     * 查询用户优惠券列表
     * @param userId
     * @return
     */
    List<DiscountUser> queryDiscountUserList(String userId,String couponType,Long now);

    /**
     * 查询用户优惠券列表
     * @param userId
     * @param couponType
     * @return
     */
    List<DiscountUser> queryDiscountUserListByType(String userId,String couponType,Long now);
}
