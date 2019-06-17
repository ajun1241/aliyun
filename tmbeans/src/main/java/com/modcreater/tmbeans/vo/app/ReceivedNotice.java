package com.modcreater.tmbeans.vo.app;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-17
 * Time: 14:33
 */
@Data
public class ReceivedNotice {

    private String userId;

    private String appType;

    private String noticeTypeId;

    private String noticeName;

    private String date;

}
