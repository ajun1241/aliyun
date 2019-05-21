package com.modcreater.tmbeans.vo.usersettings;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-21
 * Time: 13:52
 */
@Data
public class UserSettingsIdAndStatus {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 设置状态(0/1)
     */
    private int status;
    /**
     * 要修改的设置的名称
     */
    private String type;

    private String appType;

}
