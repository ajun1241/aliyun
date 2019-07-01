package com.modcreater.tmbeans.show.backer;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-07-01
 * Time: 15:33
 */
@Data
public class ShowFriendList {

    private String userId;

    private String friendId;

    private String headImgUrl;

    private String userName;

    private String gender;

    private String userSign;
    /**
     * 是否是本次操作用户所选中的支持者,0为是,1为默认不是
     */
    private String status = "1";

}
