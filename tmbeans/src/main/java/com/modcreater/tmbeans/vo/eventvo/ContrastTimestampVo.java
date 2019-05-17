package com.modcreater.tmbeans.vo.eventvo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-04-30
 * Time: 16:58
 */
@Data
public class ContrastTimestampVo implements Serializable {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 时间戳(1556614847)
     */
    private String time;

}
