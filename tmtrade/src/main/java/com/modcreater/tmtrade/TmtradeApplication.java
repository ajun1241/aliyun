package com.modcreater.tmtrade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.modcreater.**.mapper")
@EnableTransactionManagement
public class TmtradeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TmtradeApplication.class, args);
    }

}
