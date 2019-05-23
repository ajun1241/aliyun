package com.modcreater.tmbeans.vo.uservo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/16 13:51
 */
@Data
public class FriendshipVo implements Serializable {
    private String userId;
    private String friendId;
    private String appType;
}
