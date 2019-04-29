package com.modcreater.tmauth;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import javax.sql.DataSource;

@SpringBootApplication
@ComponentScan(basePackages = {"com.modcreater.tmauth.*"})
@MapperScan("com.modcreater.**.mapper")
public class TmauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(TmauthApplication.class, args);
    }
}
