package com.modcreater.tmstore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan(basePackages = {"com.modcreater.tmstore.*"})
@MapperScan("com.modcreater.**.mapper")
@EnableTransactionManagement
public class TmstoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(TmstoreApplication.class, args);
    }

}
