package com.modcreater.tmauth.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.pojo.DiscountCoupon;
import com.modcreater.tmbeans.pojo.DiscountUser;
import com.modcreater.tmbeans.vo.app.DiscountUserVo;
import com.modcreater.tmbeans.vo.app.ReceivedAppInfo;
import com.modcreater.tmbeans.vo.app.ReceivedNotice;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedIdExtra;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-03
 * Time: 9:00
 */
public interface AppService {

    /**
     * 更新App
     * @param appInfo
     * @param request
     * @return
     */
    Dto updateApp(ReceivedAppInfo appInfo, HttpServletRequest request);

    /**
     * 获取活动公告
     * @param receivedId
     * @param token
     * @return
     */
    Dto getActivityInform(ReceivedId receivedId, String token);

    /**
     * 领取优惠券
     * @param discountUserVo
     * @param token
     * @return
     */
    Dto getDiscountCoupon(DiscountUserVo discountUserVo, String token);

    /**
     * 根据服务类型查询用户优惠券列表
     * @param receivedId
     * @param token
     * @return
     */
    Dto getUserDiscountList(ReceivedIdExtra receivedId, String token);

    /**
     * 查询用户可用优惠券数量
     * @param receivedId
     * @param token
     * @return
     */
    Dto getUserDiscountCount(ReceivedIdExtra receivedId,String token);


}
