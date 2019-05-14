package com.modcreater.tmchat;

import io.rong.RongCloud;
import io.rong.methods.user.User;
import io.rong.models.Result;
import io.rong.models.response.TokenResult;
import io.rong.models.user.UserModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TmchatApplicationTests {


    @Test
    @Scheduled(cron = "0/5 * * * * * ?")
    public void contextLoads() throws Exception {
        System.out.println("定时器工作了");
    }
}
