package com.modcreater.tmbeans.pojo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-09-06
 * Time: 10:49
 */
@Data
public class GroupPermission {

    private String id;

    private String userId;

    private Long groupUpperLimit;

}
