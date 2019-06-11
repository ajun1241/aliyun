package com.modcreater.tmauth;

import com.modcreater.tmbeans.pojo.MsgStatus;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@Component
public class TmauthApplicationTests {
    @Resource
    MsgStatusMapper msgStatusMapper;
    @Test
    public void contextLoads() {
        List<String> list=new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        list.add("e");
//        list.toArray();
        String[] l={"a","b","c","d","e"};
        System.out.println(list.toString());
        System.out.println("***************************");
        String s=String.join(",",list);
        System.out.println("ssdasdasdas===="+s);
    }
    @Test
    public void test(){
    }
}
