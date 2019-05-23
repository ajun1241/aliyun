package com.modcreater.tmbeans.vo.usersettings;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-23
 * Time: 10:13
 */
@Data
public class GetFriendListInSettings {

    private String userId;
    /**
     * 要操作的类型(invite:限制邀请操作;sustain:限制支持操作)
     */
    private String updateType;

    private String appType;

}
