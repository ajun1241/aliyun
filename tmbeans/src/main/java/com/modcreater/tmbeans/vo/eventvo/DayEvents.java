package com.modcreater.tmbeans.vo.eventvo;


import com.modcreater.tmbeans.pojo.SingleEvent;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
@Data
public class DayEvents<T> implements Serializable {

    private Long userId;
    private Long totalNum;
    private ArrayList<T> mySingleEventList ;
    private Long DayEventId;

}
