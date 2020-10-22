package cn.nihility.unify.annotaion;

import java.lang.annotation.*;

/**
 * 需要认证的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Inherited
public @interface VerifyAuthentication {
    boolean verify() default true;
}
