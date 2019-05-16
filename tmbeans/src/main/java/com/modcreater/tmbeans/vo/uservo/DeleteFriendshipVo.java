package com.modcreater.tmbeans.vo.uservo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/16 14:06
 */
@Data
public class DeleteFriendshipVo implements Serializable {
    private String id;
    private String userId;
    private String friendId;
    private String appType;
}
