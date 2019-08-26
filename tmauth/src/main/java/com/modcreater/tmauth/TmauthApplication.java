package com.modcreater.tmauth;

import com.alibaba.druid.pool.DruidDataSource;
import com.modcreater.tmutils.mobserver.MobPushUtils;
import mob.push.api.MobPushConfig;
import mob.push.api.exception.ApiException;
import mob.push.api.model.PushWork;
import mob.push.api.push.PushClient;
import mob.push.api.utils.AndroidNotifyStyleEnum;
import mob.push.api.utils.PlatEnum;
import mob.push.api.utils.PushTypeEnum;
import mob.push.api.utils.TargetEnum;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sql.DataSource;

@SpringBootApplication
@ComponentScan(basePackages = {"com.modcreater.tmauth.*"})
@MapperScan("com.modcreater.**.mapper")
@EnableTransactionManagement
public class TmauthApplication {
    public static void main(String[] args) {
        SpringApplication.run(TmauthApplication.class, args);
    }
}
