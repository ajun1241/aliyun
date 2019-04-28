package com.modcreater.tmbeans.dto;

import lombok.Data;

@Data
public class Dto<T> {
    private int resCode;
    private String resMsg;
    private T data;
}
