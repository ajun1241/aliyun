package com.modcreater.tmstore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@MapperScan("com.modcreater.**.mapper")
@EnableTransactionManagement
@EnableScheduling
public class TmstoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(TmstoreApplication.class, args);
    }

}
