package com.modcreater.tmbeans.vo;


import com.modcreater.tmbeans.pojo.SingleEvent;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
@Data
public class DayEvents implements Serializable {

    private int userId;
    private int totalNum;
    private ArrayList<SingleEvent> mySingleEventList ;
    private int DayEventId;

}
