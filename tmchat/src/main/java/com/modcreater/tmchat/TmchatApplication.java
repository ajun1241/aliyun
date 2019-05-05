package com.modcreater.tmchat;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TmchatApplication {

    public static void main(String[] args) {
        SpringApplication.run(TmchatApplication.class, args);
        AVOSCloud.initialize("6uI8RKWYiVFfdeYP4r3clcni-gzGzoHsz","0h1p4HiiFsGnB2KwKEVAfxub","B2ljM3ae6Ez7NThQdi4t4Ptx");
        //调试信息
        AVOSCloud.setDebugLogEnabled(true);

        AVObject testObject = new AVObject("TestObject");
        testObject.put("words","Hello World!");
        try {
            testObject.save();
        } catch (AVException e) {
            e.printStackTrace();
        }
    }

}
