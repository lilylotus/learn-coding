package cn.nihility.boot.annotation;

import java.lang.annotation.*;

/**
 * @author dandelion
 * @date 2020:06:27 10:57
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface PassToken {

    boolean required() default true;

}
