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
     * 达成条件
     */
    private Integer condition;
    /**
     * 成就类型
     */
    private Integer type;

}
