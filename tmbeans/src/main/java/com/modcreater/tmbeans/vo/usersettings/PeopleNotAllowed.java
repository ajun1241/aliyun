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
public class PeopleNotAllowed {

    private String userId;

    private String friendsIds;
    /**
     * 体现本次操作为添加限制还是解除限制(0:解除限制,1限制)
     */
    private String status;
    /**
     * 要操作的类型(invite:限制邀请操作;sustain:限制支持操作)
     */
    private String updateType;

    private String appType;

}
