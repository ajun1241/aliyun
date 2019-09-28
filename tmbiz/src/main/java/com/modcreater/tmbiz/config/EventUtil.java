package com.modcreater.tmbiz.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.modcreater.tmbeans.dto.EventPersons;
import com.modcreater.tmbeans.pojo.SingleEvent;
import com.modcreater.tmdao.mapper.EventMapper;
import com.modcreater.tmutils.DateUtil;
import com.modcreater.tmutils.SingleEventUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

    /**
     * 修改其他参与者的事件
     * @param me
     * @param singleEvent
     */
    public void updateInviterEvent(SingleEvent singleEvent,String me)throws Exception {
         EventPersons eventPersons=JSONObject.parseObject(singleEvent.getPerson(),EventPersons.class);
        String person=eventPersons.getFriendsId();
        if (StringUtils.isEmpty(person)){
            person=me;
        }else {
            person=person.concat(","+me);
        }
        eventPersons.setFriendsId(person);
        //修改创建者事件
        eventMapper.updInviteEventPerson(singleEvent.getUserid().toString(),singleEvent.getEventid().toString(),JSON.toJSONString(eventPersons));
        //修改参与者的事件
        EventPersons eventPersons1=JSONObject.parseObject(singleEvent.getPerson(),EventPersons.class);
        String person1=eventPersons1.getFriendsId();
        if (!StringUtils.isEmpty(person1)){
            for (String inviteId:person1.split(",")) {
                SingleEvent singleEvent2=eventMapper.queryEventOne(inviteId,singleEvent.getEventid().toString());
                EventPersons eventPersons2=JSONObject.parseObject(singleEvent2.getPerson(),EventPersons.class);
                eventPersons2.setFriendsId(eventPersons2.getFriendsId().concat(","+me));
                eventMapper.updInviteEventPerson(inviteId,singleEvent.getEventid().toString(),JSON.toJSONString(eventPersons2));
            }
        }
    }
}
