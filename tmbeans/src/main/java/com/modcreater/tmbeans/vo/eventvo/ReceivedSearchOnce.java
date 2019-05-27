package com.modcreater.tmbeans.vo.eventvo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-27
 * Time: 15:09
 */
@Data
public class ReceivedSearchOnce implements Serializable {

    private String userId;

    private String eventId;

    private String appType;

}
