package com.modcreater.tmauth.config.annotation;

import java.lang.annotation.*;

/**
 * Description:
 *
 * @Author: AJun
 * @Date: 2019/7/3 17:20
 */
@Documented
@Inherited
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE_USE, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Safety {
    String value() default "";
}
