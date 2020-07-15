package cn.nihility.web.annotation;

import java.lang.annotation.*;

/**
 * 仿制 spring mvc @Controller 注解
 *
 * @author dandelion
 * @date 2020-06-25 18:19
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface MyController {
    String value() default "";
}
