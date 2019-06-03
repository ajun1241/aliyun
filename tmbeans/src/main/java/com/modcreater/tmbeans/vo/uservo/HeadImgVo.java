package com.modcreater.tmbeans.vo.uservo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/5/28 10:24
 */
@Data
public class HeadImgVo implements Serializable {
    private String userId;
    private String appType;
    private String headImgUrl;
}
