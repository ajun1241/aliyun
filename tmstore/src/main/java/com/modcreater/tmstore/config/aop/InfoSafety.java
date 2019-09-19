package com.modcreater.tmstore.config.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;
import com.modcreater.tmdao.mapper.SafetyMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    @Resource
    private SafetyMapper safetyMapper;

    private Logger logger = LoggerFactory.getLogger(InfoSafety.class);
    /**
     * 定义切点方法
     */
    @Pointcut("@annotation(com.modcreater.tmstore.config.annotation.Safety)")
    public  void pointCut(){}

    /**
     * 获取用户请求明细
     * @param joinPoint
     */
    @Before("pointCut()")
    public void around(JoinPoint joinPoint) throws Exception {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Object[] args=joinPoint.getArgs();
        // 记录下请求内容
        logger.info("【注解：Before】浏览器输入的网址=URL : " + request.getRequestURL().toString());
        logger.info("【注解：Before】HTTP_METHOD : " + request.getMethod());
        logger.info("【注解：Before】IP : " + request.getRemoteAddr());
        logger.info("【注解：Before】本机IP : " + InetAddress.getLocalHost().getHostAddress());
        logger.info("【注解：Before】执行的业务方法名=CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        logger.info("【注解：Before】业务方法获得的参数=ARGS : " + Arrays.toString(args));
        //关键内容记录在数据库
        String param = JSON.toJSONString(args[0]);
        ReceivedId receivedId=JSONObject.parseObject(param,ReceivedId.class);
        Map<String,Object> map=new HashMap<>();
        map.put("userId",receivedId.getUserId() == null ? 0 : receivedId.getUserId());
        map.put("createDate",new Date());
        map.put("operationType",0);
        map.put("networkSourceAddress",request.getRemoteAddr());
        map.put("networkTargetAddress", InetAddress.getLocalHost().getHostAddress());
        map.put("appType",receivedId.getAppType());
        map.put("operationContent",joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        if (safetyMapper.insertRecord(map)<1){
            logger.error("记录失败");
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
