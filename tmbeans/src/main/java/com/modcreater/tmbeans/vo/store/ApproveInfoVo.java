package com.modcreater.tmbeans.vo.store;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/9/19 14:18
 */
@Data
public class ApproveInfoVo implements Serializable {
    private String appType;
    private String userId;
    private String storeName;
    private String phoneNumber;
    private String businessLicense;
    private List<String> exequatur;
    private String storeLogo;
    private String address;
    private String longitude;
    private String latitude;
    private String businessScope;
    private String detailAddress;
    private String storefrontPicture;
    private String openStoreHours;
    private String closeStoreHours;
}
