package com.modcreater.tmbiz.config.aop;

import com.modcreater.tmbeans.pojo.UserStatistics;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmdao.mapper.AchievementMapper;
import io.rong.messages.TxtMessage;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-11
 * Time: 14:50
 */
@Aspect
@Configuration
@Component
public class GetLastOperatedTime {

    /**
     * 定义切点方法
     */
    @Pointcut("@annotation(com.modcreater.tmbiz.config.annotation.GLOT)")
    public  void pointCut(){}

    @Resource
    private AchievementMapper achievementMapper;

    /**
     * 前置通知
     * @param joinPoint
     */
    @Before("pointCut()")
    public void around(JoinPoint joinPoint) {
        try{
            //1.获取到所有的参数值的数组
            Object[] args = joinPoint.getArgs();
            Signature signature = joinPoint.getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;
            //2.获取到方法的所有参数名称的字符串数组
            String[] parameterNames = methodSignature.getParameterNames();
            for (int i =0 ,len=parameterNames.length;i < len ;i++){
                String param = args[i].toString();
                if (!param.contains("userId")){
                    continue;
                }
                StringBuffer stringBuffer = new StringBuffer(param);
                String result = stringBuffer.substring(param.indexOf("userId") + 7,param.indexOf(","));
                if (achievementMapper.getLoggedDaysUpdated(result) == 0){
                    UserStatistics userStatistics = new UserStatistics();
                    userStatistics.setUserId(Long.valueOf(result));
                    userStatistics.setLoggedDays(1L);
                    userStatistics.setLoggedDaysUpdated(1L);
                    achievementMapper.updateUserStatistics(userStatistics);
                    Long thisTime = System.currentTimeMillis()/1000;
                    achievementMapper.updateUserLastOperatedTime(result,thisTime);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ReceivedId txtMessage=new ReceivedId();
        txtMessage.setUserId("sadas");
        System.out.println(txtMessage.toString());
    }
}
