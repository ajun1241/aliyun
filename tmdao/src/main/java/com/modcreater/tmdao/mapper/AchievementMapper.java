package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.Achievement;
import com.modcreater.tmbeans.pojo.UserAchievement;
import com.modcreater.tmbeans.pojo.UserStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-16
 * Time: 18:03
 */
@Mapper
public interface AchievementMapper {

    /**
     * 根据用户ID查询该用户所有成就图片地址
     * @param userId
     * @return
     */
    List<String> searchAllAchievement(String userId);

    /**
     * 根据用户ID查询该用户的统计数据
     * @param userId
     * @return
     */
    UserStatistics queryUserStatistics(String userId);

    /**
     * 根据用户ID查询该用户的"上一次操作时间"
     * @param userId
     * @return
     */
    Long queryUserStatisticsDate(String userId);

    /**
     * 查询成就完成条件
     * @return
     */
    List<Achievement> queryAchievement();

    /**
     * 为用户添加一个新的成就
     * @param id
     * @param userId
     * @return
     */
    int addNewAchievement(@Param("id") Long id, @Param("userId") String userId,@Param("createDate") String createDate);

    /**
     * 修改用户统计表
     * @param userStatistics
     * @param userId
     * @return
     */
    int updateUserStatistics(@Param("userStatistics") UserStatistics userStatistics ,@Param("userId") String userId);

    /**
     * 修改所有用户统计表
     * @param userStatistics
     * @return
     */
    Long updateAllUserStatistics(UserStatistics userStatistics);

    /**
     * 为用户添加一条新的计数数据
     * @param userId
     * @return
     */
    int addNewUserStatistics(String userId);

    /**
     * 查询用户成就是否达成
     * @param userId
     * @param id
     * @return
     */
    UserAchievement queryUserAchievement(@Param("userId") String userId,@Param("achievementId") Long id);

    /**
     * 修改用户上一次操作的时间
     * @param userId
     * @param time
     * @return
     */
    int updateUserLastOperatedTime(@Param("userId") String userId,@Param("time") Long time);

    /**
     * 获取用户是否添加过登录天数
     * @param result
     * @return
     */
    int getLoggedDaysUpdated(String result);

    /**
     * 获取上一次操作的时间
     * @param userId
     * @return
     */
    Long getLastOperatedTime(String userId);

    /**
     * 查询用户是否已经添加过成就数据
     * @param userId
     * @return
     */
    UserAchievement isUserAchievementExists(String userId);
}
