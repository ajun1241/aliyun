package com.modcreater.tmbiz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.modcreater.**.mapper")
public class TmbizApplication {

    public static void main(String[] args) {
        SpringApplication.run(TmbizApplication.class, args);
    }

}
