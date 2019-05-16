package com.modcreater.tmauth.config;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-16
 * Time: 17:42
 */
public class MyConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /**
         * @Description: 对文件的路径进行配置, 创建一个虚拟路径/rules/** ，即只要在<img src="/rules/picName.jpg" />便可以直接引用图片
         * classpath:/rules/这是图片的物理路径，rules是文件夹  "file:/+本地图片的地址"
         */
        registry.addResourceHandler("/images/**").addResourceLocations
                ("classpath:/images/");
        super.addResourceHandlers(registry);
    }


}
