package com.modcreater.tmbiz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
@MapperScan("com.modcreater.**.mapper")
@Transactional
public class TmbizApplication {

    public static void main(String[] args) {
        SpringApplication.run(TmbizApplication.class, args);
    }

}
