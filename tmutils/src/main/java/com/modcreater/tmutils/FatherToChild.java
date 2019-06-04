package com.modcreater.tmutils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: Goku_yi
 * @Date: 2019-06-04
 * Time: 13:51
 */
public class FatherToChild {

    public static <T>void change(T father,T child) throws Exception{
        if (child.getClass().getSuperclass() != father.getClass()){
            System.out.println(child.getClass().getName()+"不是"+father.getClass().getName()+"的子类");
        }
        Class fatherClass = father.getClass();
        Field[] fields = fatherClass.getDeclaredFields();
        for (Field field :fields){
            Method method = fatherClass.getDeclaredMethod("get"+field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1));
            Object object = method.invoke(father);
            field.setAccessible(true);
            field.set(child,object);
        }
    }
}
