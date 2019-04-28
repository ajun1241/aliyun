package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class QueryEventVo implements Serializable {
    private long id;
    private long day;
    private long month;
    private long year;
}
