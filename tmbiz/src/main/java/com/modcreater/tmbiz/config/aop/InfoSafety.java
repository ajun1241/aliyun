package com.modcreater.tmbiz.config.aop;

import com.modcreater.tmbeans.pojo.UserStatistics;
import com.modcreater.tmbiz.service.impl.EventServiceImpl;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/7/3 17:22
 */
@Aspect
@Configuration
@Component
public class InfoSafety {

    private Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);
    /**
     * 定义切点方法
     */
    @Pointcut("@annotation(com.modcreater.tmbiz.config.annotation.Safety)")
    public  void pointCut(){}

    @Before("pointCut()")
    public void around(JoinPoint joinPoint) {
        //1.获取到所有的参数值的数组
        Object[] args = joinPoint.getArgs();
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        String[] parameterNames = methodSignature.getParameterNames();
        for (int i =0 ,len=parameterNames.length;i < len ;i++){
            String param = args[i].toString();
            //截取用户手机硬件信息
            if (param.contains("appType")){
                logger.info(param);
            }
            //得到事件的全部内容
            if (!param.contains("singleEvent")){
                continue;
            }
            //判断截取到的内容是否包含敏感词
            if (sensitiveWords().toString().contains(param)){
                //提示用户有敏感词并记录
                record(param);
                logger.info("敏感词记录"+param);
            }
        }
    }
    private String[] sensitiveWords(){
        return new String[]{
                "张国焘\n"  +
                "刘少奇\n" +
                "王洪文\n" +
                "乌兰夫\n" +
                "习仲勋\n" +
                "田纪云\n" +
                "陈良宇\n" +
                "王兆国\n" +
                "俞正声\n" +
                "李长春\n" +
                "王刚\n" +
                "范长龙\n" +
                "孙政才\n" +
                "马凯\n" +
                "李锡铭\n" +
                "张春桥\n" +
                "李瑞环\n" +
                "李铁映\n" +
                "陈锡联\n" +
                "宋任穷\n" +
                "林彪\n" +
                "朱德\n"};
    }
    private void record(String param){

    }
}
