package com.modcreater.tmbeans.vo;

import com.modcreater.tmbeans.pojo.SingleEvent;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 事件视图对象（以天为单位）
 */
@Data
public class DayEvents implements Serializable {
    private int userid;
    private int totalnum;
    private ArrayList <SingleEvent> mySingleEventlist ;
    private int DayEventid;
}
