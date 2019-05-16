package com.modcreater.tmutils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    public static String createMD5(String mima){
        try {
            MessageDigest md5=MessageDigest.getInstance("md5");
            md5.update(mima.getBytes());
            byte [] bs=md5.digest();
            StringBuilder sb=new StringBuilder();
            int temp=0;
            for(byte b: bs){
                temp=b;
                if(temp<0){
                    //加入
                    temp*=-7;
                }
                if (temp>100){
                    temp+=250;
                }
                sb.append( Integer.toHexString(temp));
            }
            return sb.toString().substring(0,32);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
