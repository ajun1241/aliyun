package com.modcreater.tmbeans.vo.group;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-09
 * Time: 11:25
 */
@Data
public class RemoveMember {

    private String userId;

    private String appType;

    private String groupId;

    private String[] membersId;

}
