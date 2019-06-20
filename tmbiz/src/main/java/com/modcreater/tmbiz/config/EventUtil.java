package com.modcreater.tmbiz.config;

import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmdao.mapper.EventMapper;
import com.modcreater.tmutils.DateUtil;
import com.modcreater.tmutils.SingleEventUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/6/12 16:41
 */
@Component
public class EventUtil {

    @Resource
    private  EventMapper eventMapper;

    public  List<SingleEvent> eventClashUtil(SingleEvent singleEvent) {
        List<SingleEvent> clashList=new ArrayList<>();
        List<SingleEvent> singleEventList = eventMapper.queryClashEventList(singleEvent);
        int week = DateUtil.stringToWeek(null);
        week = week == 7 ? 0 : week;
        for (SingleEvent singleEvent1 : singleEventList) {
            //是重复事件
            if (singleEvent.getIsLoop()==1){
                Boolean[] repeatTime = SingleEventUtil.getRepeatTime(singleEvent);
                if (repeatTime[week]){
                    if (((Long.valueOf(singleEvent1.getStarttime()) > Long.parseLong(singleEvent.getStarttime()) && Long.valueOf(singleEvent1.getEndtime()) < Long.parseLong(singleEvent.getEndtime()))
                            || (Long.valueOf(singleEvent1.getStarttime()) > Long.parseLong(singleEvent.getStarttime()) && Long.valueOf(singleEvent1.getStarttime()) < Long.parseLong(singleEvent.getEndtime()))
                            || (Long.valueOf(singleEvent1.getEndtime()) > Long.parseLong(singleEvent.getStarttime()) && Long.valueOf(singleEvent1.getEndtime()) < Long.parseLong(singleEvent.getEndtime()))
                            || (Long.valueOf(singleEvent1.getStarttime()) <= Long.parseLong(singleEvent.getStarttime()) && Long.valueOf(singleEvent1.getEndtime()) >= Long.parseLong(singleEvent.getEndtime()))) ){
                        //冲突事件添加进集合
                        clashList.add(singleEvent1);
                    }
                }
            }else {
                //不是重复事件
                if (((Long.valueOf(singleEvent1.getStarttime()) > Long.parseLong(singleEvent.getStarttime()) && Long.valueOf(singleEvent1.getEndtime()) < Long.parseLong(singleEvent.getEndtime()))
                        || (Long.valueOf(singleEvent1.getStarttime()) > Long.parseLong(singleEvent.getStarttime()) && Long.valueOf(singleEvent1.getStarttime()) < Long.parseLong(singleEvent.getEndtime()))
                        || (Long.valueOf(singleEvent1.getEndtime()) > Long.parseLong(singleEvent.getStarttime()) && Long.valueOf(singleEvent1.getEndtime()) < Long.parseLong(singleEvent.getEndtime()))
                        || (Long.valueOf(singleEvent1.getStarttime()) <= Long.parseLong(singleEvent.getStarttime()) && Long.valueOf(singleEvent1.getEndtime()) >= Long.parseLong(singleEvent.getEndtime()))) ){
                    //冲突事件添加进集合
                    clashList.add(singleEvent1);
                }
            }
        }
        return clashList;
    }
}
