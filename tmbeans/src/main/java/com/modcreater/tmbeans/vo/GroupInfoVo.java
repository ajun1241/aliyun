package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/9/5 16:08
 */
@Data
public class GroupInfoVo implements Serializable {

    private Long id;
    /**
     * 团队名称
     */
    private String groupName;
    /**
     * 团队头像
     */
    private String groupPicture;
    /**
     * 团队所属单位
     */
    private String groupUnit;
    /**
     *团队规模(1：50人；2：100人；3：150人)
     */
    private String groupScale;
    /**
     *团队性质（0：学习；1：工作；2：商务；3：休闲；4：家庭；5：节日；6：假期；7：其他）
     */
    private String groupNature;
    /**
     *团队介绍/描述
     */
    private String groupPresentation;
    /**
     * 创建人Id
     */
    private String userId;
    private String appType;

}
