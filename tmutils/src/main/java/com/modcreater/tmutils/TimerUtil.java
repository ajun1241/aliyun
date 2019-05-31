package com.modcreater.tmutils;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/31 10:34
 */
@EnableAsync
public class TimerUtil {
    private static Date date;
}
