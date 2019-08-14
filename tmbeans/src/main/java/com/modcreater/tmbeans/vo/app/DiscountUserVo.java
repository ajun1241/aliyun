package com.modcreater.tmbeans.vo.app;

import lombok.Data;

import java.util.Date;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/8/14 10:02
 */
@Data
public class DiscountUserVo {

    private String userId;
    private String appType;
    private String id;
    private String discountId;
    private Date createDate;
    private String starTime;
    private String endTime;
    private String status;
}
