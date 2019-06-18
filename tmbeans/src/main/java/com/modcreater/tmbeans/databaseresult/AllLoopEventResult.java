package com.modcreater.tmbeans.databaseresult;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-18
 * Time: 16:38
 */
@Data
public class AllLoopEventResult {

    private String userId;

    private String eventId;

    private String repeatTime;

    private Integer startTime;

    private Integer endTime;

}
