package com.modcreater.tmauth;

import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.dto.EventPersons;
import com.modcreater.tmbeans.pojo.Friendship;
import com.modcreater.tmbeans.pojo.MsgStatus;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmdao.mapper.AccountMapper;
import com.modcreater.tmdao.mapper.EventMapper;
import com.modcreater.tmdao.mapper.MsgStatusMapper;
import com.modcreater.tmdao.mapper.SensitiveWordsMapper;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@Component
public class TmauthApplicationTests {
    @Resource
    MsgStatusMapper msgStatusMapper;
    @Resource
    private AccountMapper accountMapper;
    @Resource
    private static SensitiveWordsMapper sensitiveWordsMapper;
    @Test
    public void contextLoads() {

    }
    @Test
    public void test(){
        Set<String> set=sensitiveWordsMapper.sensitiveWords();
        System.out.println(set);
    }
}

/*
class Ad<T>{
    private int resCode;
    private String resMsg;
    private T data;

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}*/
