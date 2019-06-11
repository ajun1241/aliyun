package com.modcreater.tmbiz.config.annotation;

import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * Description: Uses this annotation,
 * it will update the operation time and increase the logon number of days each time it is invoked
 * @Author: Goku_yi
 * @Date: 2019-06-11
 * Time: 14:52
 */
@Documented
@Inherited
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE_USE, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface GLOT {
}
