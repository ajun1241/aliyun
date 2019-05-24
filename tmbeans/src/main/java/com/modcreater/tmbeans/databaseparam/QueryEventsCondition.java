package com.modcreater.tmbeans.databaseparam;

import com.modcreater.tmbeans.pojo.SingleEvent;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-24
 * Time: 10:10
 */
@Data
public class QueryEventsCondition extends SingleEvent {

    private Long pageNum;

    private Long pageSize;

}
