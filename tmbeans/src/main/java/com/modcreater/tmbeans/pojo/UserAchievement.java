package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-16
 * Time: 17:58
 */
@Data
public class UserAchievement {
    /**
     * 主键
     */
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 成就ID
     */
    private Long achievementId;
    /**
     * 成就获得时间(时间戳)
     */
    private String createDate;

}
