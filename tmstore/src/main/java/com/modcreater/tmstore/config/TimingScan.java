package com.modcreater.tmstore.config;

import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.pojo.StoreGoodsConsumable;
import com.modcreater.tmbeans.pojo.StoreGoodsStock;
import com.modcreater.tmbeans.pojo.StoreOfflineOrders;
import com.modcreater.tmdao.mapper.GoodsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;


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

    @Resource
    private GoodsMapper goodsMapper;

    private static Logger logger = LoggerFactory.getLogger(TimingScan.class);

    @Scheduled(cron = "0 * * * * ?")
    public void orderStatusScan(){
        Long timeOut = 300L;
        List<StoreOfflineOrders> offlineOrders = goodsMapper.getTimeOutOrders(timeOut);
        if (offlineOrders.size() > 0){
            logger.info(offlineOrders.size() + "个超时订单待处理");
            int i = 0;
            for (StoreOfflineOrders orders : offlineOrders){
                String temStock = goodsMapper.getTemStock(orders.getOrderNumber());
                List<Map> result = JSONObject.parseArray(temStock,Map.class);
                for (Map map : result){
                    String storeId = (String) map.get("storeId");
                    String goodsBarCode = (String) map.get("goodsBarCode");
                    Long num = (Long) map.get("num");
                    goodsMapper.resumeStock(storeId,goodsBarCode,num);
                }
                i += goodsMapper.makeOrderFailed(timeOut,orders.getOrderNumber());
            }
            logger.info(i + "个超时订单已处理");
        }
    }

}
