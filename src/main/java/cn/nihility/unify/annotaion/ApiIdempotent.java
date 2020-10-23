package cn.nihility.unify.annotaion;

import java.lang.annotation.*;

/**
 * API 接口需要实现幂等性校验，应用在 Controller 方法上
 * @author daffodil
 * @date 2020-10-23 22:41:55
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ApiIdempotent {
}
