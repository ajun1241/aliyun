package com.modcreater.tmbeans.vo.eventvo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-23
 * Time: 14:31
 */
@Data
public class SearchConditionsForIOS {

    private String userId;
    /**
     * 要查询的日期
     */
    private String date;
    /**
     * 要查询的事件的状态
     */
    private int status;
    /**
     * 要查询事件的类型(单一事件/重复事件)
     */
    private int isLoop;

    private String appType;




}
