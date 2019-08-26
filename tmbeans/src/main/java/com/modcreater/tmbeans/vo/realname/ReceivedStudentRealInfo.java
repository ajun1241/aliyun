package com.modcreater.tmbeans.vo.realname;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-03
 * Time: 13:53
 */
@Data
public class ReceivedStudentRealInfo implements Serializable {

    private String userId;

    private String userRealName;

    private String studentIDNo;

    private String studentIDCardFront;

    private String studentIDCardVerso;

    private String appType;
}
