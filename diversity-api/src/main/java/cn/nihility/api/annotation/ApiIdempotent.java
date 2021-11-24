package cn.nihility.api.annotation;

import java.lang.annotation.*;

/**
 * 接口幂等性校验注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
@Inherited
public @interface ApiIdempotent {

    /**
     * 是否进行幂等性校验，默认进行校验，false 不校验
     */
    boolean value() default true;

}
