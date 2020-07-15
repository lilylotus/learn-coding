package cn.nihility.boot.annotation;

import java.lang.annotation.*;

/**
 * @author dandelion
 * @date 2020:06:27 18:18
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RegistrarScan {

    String basePackage() default "";

}
