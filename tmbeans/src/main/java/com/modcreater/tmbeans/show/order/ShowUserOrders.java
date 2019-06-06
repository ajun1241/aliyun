package com.modcreater.tmbeans.show.order;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-06
 * Time: 8:45
 */
@Data
public class ShowUserOrders {

    private String serviceId;
    private String orderTitle;
    private String serviceType;
    private String paymentAmount;
    private String number;
    private String createDate;
    private String payTime;
    private String payChannel;

}
