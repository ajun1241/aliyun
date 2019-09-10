package com.modcreater.tmbeans.vo;

import lombok.Data;

/**
 * Description:
 * 搜索已添加好友vo
 * @Author: AJun
 * @Date: 2019/9/9 8:56
 */

@Data
public class SearchFriendVo {
    private String appType;
    private String userId;
    private String searchCondition;
}
