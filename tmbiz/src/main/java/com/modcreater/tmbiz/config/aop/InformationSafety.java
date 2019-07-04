package com.modcreater.tmbiz.config.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
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
public class InformationSafety {

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
        System.out.println(Arrays.toString(args));
    }
}
