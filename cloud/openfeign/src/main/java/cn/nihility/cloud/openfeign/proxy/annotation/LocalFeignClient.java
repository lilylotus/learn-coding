package cn.nihility.cloud.openfeign.proxy.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface LocalFeignClient {

    Class<?> localClass() default Object.class;

    String beanName() default "";

}
