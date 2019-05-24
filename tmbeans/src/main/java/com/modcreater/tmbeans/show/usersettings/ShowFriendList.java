package com.modcreater.tmbeans.show.usersettings;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-23
 * Time: 15:47
 */
@Data
public class ShowFriendList {

    private String userId;

    private String userCode;

    private String headImgUrl;

    private String userName;

    private String gender;
    /**
     * 体现本次操作为添加限制还是解除限制(0:解除限制,1限制)
     */
    private String status;

    private String updateType;

}
