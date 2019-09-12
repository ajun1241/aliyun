package com.modcreater.tmbeans.vo.group;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-12
 * Time: 13:49
 */
@Data
public class SearchMembersConditions {

    private String userId;

    private String appType;

    private String groupId;

    private String condition;

}
