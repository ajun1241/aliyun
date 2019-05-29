package com.modcreater.tmutils;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-05-29
 * Time: 10:02
 */
public class RandomNumber {

    public static int getFour(){
        int max=9999;
        int min=1111;
        return new Random().nextInt(max)%(max-min+1) + min;
    }

}
