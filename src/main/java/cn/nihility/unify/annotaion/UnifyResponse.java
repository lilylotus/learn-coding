package cn.nihility.unify.annotaion;

import java.lang.annotation.*;

/**
 * 统一返回格式的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
@Inherited
public @interface UnifyResponse {
}
