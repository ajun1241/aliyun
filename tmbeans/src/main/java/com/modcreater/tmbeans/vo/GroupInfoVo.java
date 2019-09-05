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

    private String groupName;
    private String groupPicture;
    private String groupUnit;
    private String groupScale;
    private String groupNature;
    private String groupPresentation;

    private String userId;
    private String appType;

}
