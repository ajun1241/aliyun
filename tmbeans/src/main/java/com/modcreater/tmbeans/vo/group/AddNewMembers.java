package com.modcreater.tmbeans.vo.group;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-11
 * Time: 9:31
 */
@Data
public class AddNewMembers {

    private String userId;

    private String appType;

    private String groupId;

    private String[] membersId;

}
