package com.modcreater.tmbeans.vo.eventvo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-13
 * Time: 17:02
 */
@Data
public class ReceivedFriendEventOnce {

    private String userId;

    private String friendId;

    private String appType;

    private String eventId;

}
