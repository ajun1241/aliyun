package com.modcreater.tmbiz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@ComponentScan("com.modcreater.tmbiz.controller")
@Component
public class SwaggerConfig {
    @Bean
    public Docket docketid() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2);
        docket.apiInfo(new ApiInfo("时间管理", "描述信息", "1.0.0", " ", "233", "授权来自此处", " "));
        return docket;
    }
}
