package com.modcreater.tmbeans.vo.userinfovo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-17
 * Time: 17:55
 */
@Data
public class ReceivedFiltrateUserEvents {

    private String userId;

    private String eventName;

    private String isOverdue;

    private String appType;

}
