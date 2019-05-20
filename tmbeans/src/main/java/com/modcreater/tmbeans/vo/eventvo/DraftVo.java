package com.modcreater.tmbeans.vo.eventvo;

import lombok.Data;

import java.io.Serializable;
@Data
public class DraftVo implements Serializable {
    private String userId;
    private String phoneNum;
    private String data;
    private String apptype;
}
