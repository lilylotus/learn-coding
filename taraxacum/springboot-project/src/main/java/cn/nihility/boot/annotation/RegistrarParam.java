package cn.nihility.boot.annotation;

import java.lang.annotation.*;

/**
 * @author dandelion
 * @date 2020:06:27 18:28
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RegistrarParam {
    String value() default "";
}
