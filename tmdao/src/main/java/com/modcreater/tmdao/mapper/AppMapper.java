package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.AppVersion;
import com.modcreater.tmbeans.pojo.UserNotice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

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
     * @param now
     * @return
     */
    AppVersion getAppVersion(String now);

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
}
