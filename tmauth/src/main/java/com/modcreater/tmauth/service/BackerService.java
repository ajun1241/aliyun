package com.modcreater.tmauth.service;

import com.modcreater.tmbeans.dto.Dto;
import com.modcreater.tmbeans.vo.backer.ReceivedBeSupporterFeedback;
import com.modcreater.tmbeans.vo.backer.ReceivedChangeBackerInfo;
import com.modcreater.tmbeans.vo.userinfovo.ReceivedId;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-07-01
 * Time: 15:23
 */
public interface BackerService {

    /**
     * 获取好友列表
     * @param receivedId
     * @param token
     * @return
     */
    Dto getFriendList(ReceivedId receivedId, String token);

    /**
     * 更换支持者
     * @param receivedChangeBackerInfo
     * @param token
     * @return
     */
    Dto changeBacker(ReceivedChangeBackerInfo receivedChangeBackerInfo, String token);

    /**
     * 获取我的支持者
     * @param receivedId
     * @param token
     * @return
     */
    Dto getMyBacker(ReceivedId receivedId, String token);

    /**
     * 是否成为支持者做出响应
     * @param receivedBeSupporterFeedback
     * @param token
     * @return
     */
    Dto beSupporterFeedback(ReceivedBeSupporterFeedback receivedBeSupporterFeedback, String token);
}
