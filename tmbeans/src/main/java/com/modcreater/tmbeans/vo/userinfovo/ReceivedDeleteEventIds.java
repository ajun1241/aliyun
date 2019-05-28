package com.modcreater.tmbeans.vo.userinfovo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-28
 * Time: 10:16
 */
@Data
public class ReceivedDeleteEventIds {
    private String userId;

    private Long[] eventIds;

    private String deleteType;

    private String appType;
}
