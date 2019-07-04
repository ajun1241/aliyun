package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-07-04
 * Time: 11:16
 */
@Data
public class EventMsg {

    private String id;
    private String msgOwnerId;
    private String eventId;
    private String msgSenderId;
    private String content;
    private Long createDate;

}
