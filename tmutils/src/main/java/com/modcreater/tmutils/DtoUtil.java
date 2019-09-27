package com.modcreater.tmutils;


import com.modcreater.tmbeans.dto.Dto;

/**
 *         1 成功
 *         2 失败
 *         3 异常
 *         4 带结果的成功
 */
public class DtoUtil {

    //成功带信息和状态码
    public  static Dto getSuccessDto(String message,int resCode){
        Dto dto=new Dto();
        dto.setResMsg(message);
        dto.setResCode(resCode);
        return dto;
    }
    //成功带信息和数据
    public  static Dto getSuccesWithDataDto(String message,Object data,int resCode){
        Dto dto=new Dto();
        dto.setResMsg(message);
        dto.setResCode(resCode);
        dto.setData(data);
        return dto;
    }
    //失败带信息和状态码
    public static Dto getFalseDto(String message,int errorcode){
        Dto dto=new Dto();
        dto.setResMsg(message);
        dto.setResCode(errorcode);
        return dto;
    }
    //异常带信息和状态码
    public static Dto getExceptionDto(String message,int errorcode){
        Dto dto=new Dto();
        dto.setResMsg(message);
        dto.setResCode(errorcode);
        return dto;
    }
    //成功只返回数据
    public static Dto getSuccesWithData(Object data){
        Dto dto=new Dto();
        dto.setResMsg("操作成功");
        dto.setResCode(00000);
        dto.setData(data);
        return dto;
    }

}
