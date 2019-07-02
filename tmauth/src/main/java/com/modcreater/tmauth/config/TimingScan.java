package com.modcreater.tmauth.config;

import com.modcreater.tmbeans.pojo.Account;
import com.modcreater.tmbeans.pojo.UserStatistics;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmdao.mapper.AchievementMapper;
import com.modcreater.tmdao.mapper.BackerMapper;
import com.modcreater.tmutils.RongCloudMethodUtil;
import io.rong.messages.TxtMessage;
import io.rong.models.response.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

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

    private static final String SYSTEMID="100000";

    @Resource
    private AchievementMapper achievementMapper;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private BackerMapper backerMapper;

    /**
     * 每天0点将所有用户登录天数修改状态归零
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void changeLoggedDaysUpdated(){
        UserStatistics userStatistics = new UserStatistics();
        userStatistics.setLoggedDaysUpdated(0L);
        achievementMapper.updateAllUserStatistics(userStatistics);
    }
    /**
     * 每天八点查询明天有支持者的事件，发送信息给支持者
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void remindBacker(){
        try {
            //获取后一天的日期
            Date date=new Date();
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(Calendar.DATE,1);
            date=calendar.getTime();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            String[] now=simpleDateFormat.format(date).split("-");
            logger.info(Arrays.toString(now));
            //查询事件和支持者
            List<Map<String, Object>> backerForEvent = backerMapper.findBackerForEvent(now[0], now[1], now[2]);
            RongCloudMethodUtil rongCloudMethodUtil=new RongCloudMethodUtil();
            for (Map<String,Object> map:backerForEvent) {
                logger.info("有吗："+map.toString());
                //发送消息
                Account account=accountMapper.queryAccount(map.get("userId").toString());
                TxtMessage txtMessage=new TxtMessage("你支持的用户“"+account.getUserName()+"”在明天有很重要的事情，记得提醒TA","");
                ResponseResult result=rongCloudMethodUtil.sendPrivateMsg(SYSTEMID,new String[]{map.get("backerId").toString()},0,txtMessage);
                if (result.getCode()!=200){
                    logger.info("融云消息异常："+result.toString());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }

}
