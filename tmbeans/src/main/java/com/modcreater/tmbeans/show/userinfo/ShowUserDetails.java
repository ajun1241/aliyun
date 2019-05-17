package com.modcreater.tmbeans.show.userinfo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-17
 * Time: 14:32
 */
@Data
public class ShowUserDetails {
    /**
     * 用户姓名
     */
    private String userName;
    /**
     * 用户描述(签名)
     */
    private String userSign;
    /**
     * 用户头像
     */
    private String userHeadPortrait;

}
