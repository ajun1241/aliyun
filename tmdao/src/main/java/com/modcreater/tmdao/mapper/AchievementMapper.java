package com.modcreater.tmdao.mapper;

import com.modcreater.tmbeans.pojo.Achievement;
import com.modcreater.tmbeans.pojo.UserStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
     * 查询成就完成条件
     * @return
     */
    List<Achievement> queryAchievement();

    int addNewAchievement(@Param("id") Long id, @Param("userId") String userId);

}
