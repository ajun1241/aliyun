package com.modcreater.tmbeans.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/7/11 15:49
 */
@Data
public class QueryDiaryVo implements Serializable {
    private String userId;
    private String appType;
    private String friendId;
    /**
     * 第几页
     */
    private String pageNumber;
    /**
     * 每页的条数
     */
    private String pageSize;
}
