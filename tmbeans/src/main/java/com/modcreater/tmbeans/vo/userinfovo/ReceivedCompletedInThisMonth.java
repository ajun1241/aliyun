package com.modcreater.tmbeans.vo.userinfovo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-07-11
 * Time: 17:07
 */
@Data
public class ReceivedCompletedInThisMonth {

    private String userId;

    private String appType;

    private Integer year;

    private Integer month;

}
