package com.modcreater.tmbeans.vo.userinfovo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-20
 * Time: 10:58
 */
@Data
public class ReceivedIdIsOverdue {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 事件状态
     */
    private String isOverdue;

}
