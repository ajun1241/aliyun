package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.vo.trade.ReceivedOrderInfo;
import org.apache.ibatis.annotations.Mapper;
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
     * 判断用户是否开启了搜索权限
     * @param userId
     * @return
     */
    int getSearchService(String userId);

    /**
     * 判断用户是否开启了好友权限
     * @param userId
     * @return
     */
    int getFriendService(String userId);

    /**
     * 判断用户是否开启了年报权限
     * @param userId
     * @return
     */
    int getAnnualReportingService(String userId);

    /**
     * 判断用户是否开启了备份权限
     * @param userId
     * @return
     */
    int getBackupService(String userId);

    /**
     * 为用户添加一条服务监测
     * @param userId
     * @return
     */
    int addNewUserService(String userId);

    /**
     * 获取用户服务剩余时间
     * @param receivedOrderInfo
     * @return
     */
    String getTimeRemaining(ReceivedOrderInfo receivedOrderInfo);
}
