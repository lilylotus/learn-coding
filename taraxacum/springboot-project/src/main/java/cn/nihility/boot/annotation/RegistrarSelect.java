package cn.nihility.boot.annotation;

import java.lang.annotation.*;

/**
 * @author dandelion
 * @date 2020:06:27 18:25
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegistrarSelect {
    String value() default "";
}
