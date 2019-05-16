package com.modcreater.tmdao.mapper;

import org.apache.ibatis.annotations.Mapper;

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

}
