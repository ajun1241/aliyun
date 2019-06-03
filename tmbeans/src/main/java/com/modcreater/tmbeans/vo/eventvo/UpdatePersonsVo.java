package com.modcreater.tmbeans.vo.eventvo;

import lombok.Data;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/6/3 14:45
 */
@Data
public class UpdatePersonsVo implements Serializable {
    private String userId;
    private String eventId;
    private String updPersons;
    private String apptype;
}
