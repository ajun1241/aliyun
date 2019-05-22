package com.modcreater.tmauth;

import com.modcreater.tmutils.MD5Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TmauthApplicationTests {

    @Test
    public void contextLoads() {
        int a=180;
        int b=a/60;
        int c=a%60;
        System.out.println("b"+b);
        System.out.println("c"+c);
    }

}
