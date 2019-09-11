package com.modcreater.tmbeans.show.group;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-11
 * Time: 18:04
 */
@Data
public class ShowGroupEventMsg {

    private String groupEventMsgId;

    private String userId;

    private String eventName;

    private String createTime;

    private String msgBody;

    private String groupPicture;

    private String groupName;

}
