package cn.nihility.api.annotation;

import java.lang.annotation.*;

/**
 * 接口身份认证
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
@Documented
public @interface ApiAuthenticationCheck {

    /**
     * 是否进行身份校验，false - 不校验
     */
    boolean value() default true;

}
