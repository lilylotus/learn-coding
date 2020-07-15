package cn.nihility.web.annotation;

import java.lang.annotation.*;

/**
 * 仿制 spring mvc 的请求参数 @Param
 *
 * @author dandelion
 * @date 2020-06-25 18:24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface MyParam {
    String value() default "";
}
