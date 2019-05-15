package com.modcreater.tmbeans.show;

import lombok.Data;

/**
 * token实体类用于接收融云生成的token
 */
@Data
public class MyToken {
    private String token;
    private String userId;
    private String code;
}
