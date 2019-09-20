package com.modcreater.tmstore.config;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-11
 * Time: 16:19
 */
@Component
@EnableScheduling
@EnableAsync
@Transactional(rollbackFor = Exception.class)
public class TimingScan {




}
