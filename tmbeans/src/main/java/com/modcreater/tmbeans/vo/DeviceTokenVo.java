package com.modcreater.tmbeans.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/7/9 10:47
 */
@Data
public class DeviceTokenVo implements Serializable {
    private String userId;
    private String appType;
    private String deviceToken;
}
