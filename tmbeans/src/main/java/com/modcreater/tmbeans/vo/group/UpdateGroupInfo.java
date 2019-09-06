package com.modcreater.tmbeans.vo.group;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-06
 * Time: 16:47
 */
@Data
public class UpdateGroupInfo {

    private String userId;

    private String appType;

    private String groupId;

    private String updateType;

    private String value;
}
