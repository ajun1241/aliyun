package com.modcreater.tmbeans.vo.backer;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-07-03
 * Time: 11:21
 */
@Data
public class ReceivedBeSupporterFeedback {

    private String userId;

    private String appType;

    private String receiverId;

    private String msgId;
    /**
     * 1接受  0不接受
     */
    private String status;

}
