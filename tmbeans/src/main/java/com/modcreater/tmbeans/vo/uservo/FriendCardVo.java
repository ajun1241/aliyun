package com.modcreater.tmbeans.vo.uservo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/7/31 16:11
 */
@Data
public class FriendCardVo implements Serializable {
    /**
     * 自己的Id
     */
    private String userId;
    /**
     * 接收者的Id
     */
    private String friendId;
    /**
     * 推荐人的Id
     */
    private String targetId;
    private String appType;
}
