package com.modcreater.tmbeans.vo.eventvo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-04-30
 * Time: 16:39
 */
@Data
public class SearchEventVo implements Serializable {
    /**
     * 用户Id
     */
    private String userId;
    /**
     * 事件的时间(20190430)
     */
    private String dayEventId;
    /**
     *
     */
    private String apptype;

}
