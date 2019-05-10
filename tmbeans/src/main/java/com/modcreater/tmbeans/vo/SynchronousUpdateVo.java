package com.modcreater.tmbeans.vo;

import com.modcreater.tmbeans.pojo.LoopEvent;
import com.modcreater.tmbeans.pojo.SingleEvent;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-04-30
 * Time: 16:48
 */
@Data
public class SynchronousUpdateVo implements Serializable {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 事件集(单位:天)
     */
    private String dayEventsList;
    /**
     * 重复事件
     */
    private String loopEventList;
    /**
     *
     */
    private String apptype;
}
