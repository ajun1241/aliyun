package com.modcreater.tmauth.config;

import com.modcreater.tmbeans.pojo.UserStatistics;
import com.modcreater.tmdao.mapper.AchievementMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-11
 * Time: 16:19
 */
@Component
@EnableScheduling
@EnableAsync
@Transactional(rollbackFor = Exception.class)
public class TimingScan {

    private Logger logger= LoggerFactory.getLogger(TimingScan.class);

    @Resource
    private AchievementMapper achievementMapper;

    /**
     * 每天0点将所有用户登录天数修改状态归零
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void changeLoggedDaysUpdated(){
        UserStatistics userStatistics = new UserStatistics();
        userStatistics.setLoggedDaysUpdated(0L);
        achievementMapper.updateAllUserStatistics(userStatistics);
    }

}
