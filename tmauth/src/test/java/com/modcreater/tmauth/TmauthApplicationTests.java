package com.modcreater.tmauth;

import com.modcreater.tmbeans.pojo.MsgStatus;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmdao.mapper.EventMapper;
import com.modcreater.tmdao.mapper.MsgStatusMapper;
import com.modcreater.tmutils.DtoUtil;
import com.modcreater.tmutils.MD5Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@Component
public class TmauthApplicationTests {
    @Resource
    MsgStatusMapper msgStatusMapper;
    @Resource
    private EventMapper eventMapper;
    @Test
    public void contextLoads() {
       /* String code="100000adv00";
        String pattern = "^1[\\d]{10}";
        System.out.println(Pattern.matches(pattern,code));*/

    }
    @Test
    public void test(){
        /*List names=new ArrayList<String>();

        names.add("1");

        System.out.println(String.join("-", names));



        String[] arrStr=new String[]{"a","b","c"};

        System.out.println(String.join("-", arrStr));*/
    }
}
