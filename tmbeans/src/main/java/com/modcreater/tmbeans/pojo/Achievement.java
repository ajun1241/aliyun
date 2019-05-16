package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-16
 * Time: 17:57
 */
@Data
public class Achievement {
    /**
     * 主键
     */
    private Long id;
    /**
     * 成就名称
     */
    private String name;
    /**
     * 成就图片的地址
     */
    private String imgUrl;
    /**
     * 登录天数条件(与完成事件数量条件冲突)
     */
    private Integer loggedDaysCondition;
    /**
     * 完成事件数条件(与登录天数条件冲突)
     */
    private Integer finishedEventsCondition;

}
