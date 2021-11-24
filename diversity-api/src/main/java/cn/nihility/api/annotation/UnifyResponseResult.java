package cn.nihility.api.annotation;

import java.lang.annotation.*;

/**
 * 使用统一的返回格式返回数据注解
 * 参考 {@link cn.nihility.api.config.UnifyResponseBodyAdvice}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
@Inherited
public @interface UnifyResponseResult {
}
