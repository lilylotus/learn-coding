package cn.nihility.web.annotation;

import java.lang.annotation.*;

/**
 * 仿制 spring mvc @RequestMapping 注解
 *
 * @author dandelion
 * @date 2020-06-25 18:23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface MyRequestMapping {
    String value() default "";
}
