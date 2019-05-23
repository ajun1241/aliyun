package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.Achievement;
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
     * 为用户添加一条新的计数数据
     * @param userId
     * @return
     */
    int addNewUserStatistics(String userId);
}
