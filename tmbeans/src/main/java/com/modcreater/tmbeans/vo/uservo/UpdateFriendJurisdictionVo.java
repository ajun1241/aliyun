package com.modcreater.tmbeans.vo.uservo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/16 13:59
 */
@Data
public class UpdateFriendJurisdictionVo implements Serializable {
    private String friendId;
    private String userId;
    private String invite;
    private String sustain;
    private String hide;
    private String appType;
}
