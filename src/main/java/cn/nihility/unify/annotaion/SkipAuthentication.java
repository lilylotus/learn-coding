package cn.nihility.unify.annotaion;

import java.lang.annotation.*;

/**
 * 跳过身份认证注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Inherited
public @interface SkipAuthentication {
    boolean skip() default true;
}
